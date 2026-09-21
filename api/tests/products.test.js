const mongoose = require("mongoose");
const request = require("supertest");
const { MongoMemoryServer } = require("mongodb-memory-server");

process.env.JWT_SECRET = "test_secret_key";

const app = require("../server");

let mongoServer;
let token;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  await mongoose.connect(mongoServer.getUri());

  const res = await request(app).post("/api/v1/auth/register").send({
    name: "Vendor Test",
    email: "vendor@example.com",
    password: "SecurePass123",
  });
  token = res.body.token;
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

describe("Products and Orders", () => {
  let productId;

  it("creates a product listing when authenticated", async () => {
    const res = await request(app)
      .post("/api/v1/products")
      .set("Authorization", `Bearer ${token}`)
      .send({
        name: "Homemade Vetkoek (6 pack)",
        description: "Freshly made vetkoek, ready in 30 minutes.",
        price: 45,
        category: "Bakery",
        stockQty: 10,
      });

    expect(res.statusCode).toBe(201);
    expect(res.body.name).toBe("Homemade Vetkoek (6 pack)");
    expect(res.body.vendorName).toBe("Vendor Test");
    productId = res.body.id;
  });

  it("rejects creating a product without authentication", async () => {
    const res = await request(app).post("/api/v1/products").send({
      name: "No auth product",
      description: "Should fail",
      price: 10,
      category: "Other",
    });
    expect(res.statusCode).toBe(401);
  });

  it("lists products publicly without authentication", async () => {
    const res = await request(app).get("/api/v1/products");
    expect(res.statusCode).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body.length).toBeGreaterThan(0);
  });

  it("places an order and decrements stock", async () => {
    const before = await request(app).get(`/api/v1/products/${productId}`);
    const stockBefore = before.body.stockQty;

    const res = await request(app)
      .post("/api/v1/orders")
      .set("Authorization", `Bearer ${token}`)
      .send({ items: [{ productId, quantity: 2 }] });

    expect(res.statusCode).toBe(201);
    expect(res.body.totalAmount).toBe(90); // 45 * 2

    const after = await request(app).get(`/api/v1/products/${productId}`);
    expect(after.body.stockQty).toBe(stockBefore - 2);
  });

  it("rejects an order that exceeds available stock", async () => {
    const res = await request(app)
      .post("/api/v1/orders")
      .set("Authorization", `Bearer ${token}`)
      .send({ items: [{ productId, quantity: 9999 }] });

    expect(res.statusCode).toBe(409);
  });
});
