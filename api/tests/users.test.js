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
    name: "Lindiwe Nkosi",
    email: "lindiwe@example.com",
    password: "SecurePass123",
  });
  token = res.body.token;
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

describe("GET /api/v1/users/me", () => {
  it("rejects a request with no token", async () => {
    const res = await request(app).get("/api/v1/users/me");
    expect(res.statusCode).toBe(401);
  });

  it("returns the logged-in user's profile", async () => {
    const res = await request(app)
      .get("/api/v1/users/me")
      .set("Authorization", `Bearer ${token}`);
    expect(res.statusCode).toBe(200);
    expect(res.body.email).toBe("lindiwe@example.com");
    expect(res.body.notificationsEnabled).toBe(true); // default value
  });
});

describe("PATCH /api/v1/users/me", () => {
  it("updates the user's name, phone, and notification preference", async () => {
    const res = await request(app)
      .patch("/api/v1/users/me")
      .set("Authorization", `Bearer ${token}`)
      .send({ name: "Lindiwe N.", phone: "0821234567", notificationsEnabled: false });

    expect(res.statusCode).toBe(200);
    expect(res.body.name).toBe("Lindiwe N.");
    expect(res.body.phone).toBe("0821234567");
    expect(res.body.notificationsEnabled).toBe(false);
  });

  it("rejects an update with no auth token", async () => {
    const res = await request(app)
      .patch("/api/v1/users/me")
      .send({ name: "Should Fail" });
    expect(res.statusCode).toBe(401);
  });
});
