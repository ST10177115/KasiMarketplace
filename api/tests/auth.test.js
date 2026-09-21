const mongoose = require("mongoose");
const request = require("supertest");
const { MongoMemoryServer } = require("mongodb-memory-server");

process.env.JWT_SECRET = "test_secret_key";
process.env.JWT_EXPIRES_IN = "1h";

const app = require("../server");

let mongoServer;

beforeAll(async () => {
  mongoServer = await MongoMemoryServer.create();
  await mongoose.connect(mongoServer.getUri());
});

afterAll(async () => {
  await mongoose.disconnect();
  await mongoServer.stop();
});

afterEach(async () => {
  const collections = mongoose.connection.collections;
  for (const key in collections) {
    await collections[key].deleteMany({});
  }
});

describe("POST /api/v1/auth/register", () => {
  it("registers a new user and returns a token", async () => {
    const res = await request(app).post("/api/v1/auth/register").send({
      name: "Thabo Mokoena",
      email: "thabo@example.com",
      password: "SecurePass123",
    });

    expect(res.statusCode).toBe(201);
    expect(res.body.token).toBeDefined();
    expect(res.body.user.email).toBe("thabo@example.com");
    // The password hash must never be returned to the client.
    expect(res.body.user.passwordHash).toBeUndefined();
  });

  it("rejects registration with a password shorter than 8 characters", async () => {
    const res = await request(app).post("/api/v1/auth/register").send({
      name: "Thabo Mokoena",
      email: "thabo2@example.com",
      password: "short",
    });
    expect(res.statusCode).toBe(400);
  });

  it("rejects a duplicate email", async () => {
    await request(app).post("/api/v1/auth/register").send({
      name: "Thabo Mokoena",
      email: "duplicate@example.com",
      password: "SecurePass123",
    });

    const res = await request(app).post("/api/v1/auth/register").send({
      name: "Another Person",
      email: "duplicate@example.com",
      password: "SecurePass123",
    });

    expect(res.statusCode).toBe(409);
  });
});

describe("POST /api/v1/auth/login", () => {
  beforeEach(async () => {
    await request(app).post("/api/v1/auth/register").send({
      name: "Naledi Dube",
      email: "naledi@example.com",
      password: "SecurePass123",
    });
  });

  it("logs in with correct credentials", async () => {
    const res = await request(app).post("/api/v1/auth/login").send({
      email: "naledi@example.com",
      password: "SecurePass123",
    });
    expect(res.statusCode).toBe(200);
    expect(res.body.token).toBeDefined();
  });

  it("rejects an incorrect password", async () => {
    const res = await request(app).post("/api/v1/auth/login").send({
      email: "naledi@example.com",
      password: "WrongPassword",
    });
    expect(res.statusCode).toBe(401);
  });
});
