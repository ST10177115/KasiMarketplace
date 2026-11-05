const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { initializeApp, cert, getApps } = require("firebase-admin/app");
const { getAuth } = require("firebase-admin/auth");
const User = require("../models/User");

const router = express.Router();

// Firebase Admin is only used to verify the ID token the app receives after Google sign-in.
// The service-account key is read from an environment variable so it is never committed to GitHub.
if (!getApps().length && process.env.FIREBASE_SERVICE_ACCOUNT) {
  initializeApp({
    credential: cert(JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT)),
  });
}

function signToken(userId) {
  return jwt.sign({ userId }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN || "7d",
  });
}

// POST /api/v1/auth/register
router.post("/register", async (req, res) => {
  try {
    const { name, email, password, phone } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({ message: "Name, email, and password are required." });
    }
    if (password.length < 8) {
      return res.status(400).json({ message: "Password must be at least 8 characters." });
    }

    const existing = await User.findOne({ email: email.toLowerCase() });
    if (existing) {
      return res.status(409).json({ message: "An account with this email already exists." });
    }

    // Passwords are hashed with bcrypt before they ever touch the database.
    // The raw password is never stored, logged, or returned.
    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const user = await User.create({ name, email, passwordHash, phone });
    const token = signToken(user._id);

    res.status(201).json({ token, user: user.toSafeObject() });
  } catch (err) {
    console.error("Register error:", err.message);
    res.status(500).json({ message: "Something went wrong while registering." });
  }
});

// POST /api/v1/auth/login
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ message: "Email and password are required." });
    }

    const user = await User.findOne({ email: email.toLowerCase() });
    if (!user) {
      // Deliberately vague, so we don't reveal whether the email exists.
      return res.status(401).json({ message: "Invalid email or password." });
    }

    // Accounts created through Google sign-in have no password to compare against.
    // Without this check, bcrypt.compare would throw and the user would see a generic 500 error.
    if (!user.passwordHash) {
      return res.status(401).json({
        message: "This account uses Google sign-in. Please tap 'Continue with Google'.",
      });
    }

    const passwordMatches = await bcrypt.compare(password, user.passwordHash);
    if (!passwordMatches) {
      return res.status(401).json({ message: "Invalid email or password." });
    }

    const token = signToken(user._id);
    res.status(200).json({ token, user: user.toSafeObject() });
  } catch (err) {
    console.error("Login error:", err.message);
    res.status(500).json({ message: "Something went wrong while logging in." });
  }
});

// POST /api/v1/auth/google
// The app signs in with Google through Firebase, then sends us the resulting Firebase ID token.
// We verify it with Google, find or create the matching user, and return this API's own JWT
// in the same shape as register/login, so the rest of the app works unchanged.
router.post("/google", async (req, res) => {
  try {
    const { idToken } = req.body;
    if (!idToken) {
      return res.status(400).json({ message: "A Google ID token is required." });
    }
    if (!getApps().length) {
      return res.status(503).json({ message: "Google sign-in is not configured on the server." });
    }

    const decoded = await getAuth().verifyIdToken(idToken);
    const email = decoded.email ? decoded.email.toLowerCase() : null;
    if (!email || decoded.email_verified === false) {
      return res.status(401).json({ message: "Your Google account must have a verified email." });
    }

    let user = await User.findOne({ $or: [{ googleId: decoded.uid }, { email }] });
    if (!user) {
      user = await User.create({
        name: decoded.name || email.split("@")[0],
        email,
        googleId: decoded.uid,
      });
    } else if (!user.googleId) {
      // Same email as an existing password account: link the two instead of duplicating.
      user.googleId = decoded.uid;
      await user.save();
    }

    const token = signToken(user._id);
    res.status(200).json({ token, user: user.toSafeObject() });
  } catch (err) {
    console.error("Google login error:", err.message);
    res.status(401).json({ message: "Google sign-in could not be verified." });
  }
});

module.exports = router;