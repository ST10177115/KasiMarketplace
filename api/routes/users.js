const express = require("express");
const bcrypt = require("bcryptjs");
const User = require("../models/User");
const { requireAuth } = require("../middleware/auth");

const router = express.Router();

// GET /api/v1/users/me — return the logged-in user's own profile/settings
router.get("/me", requireAuth, async (req, res) => {
  const user = await User.findById(req.user.userId);
  if (!user) return res.status(404).json({ message: "User not found." });
  res.json(user.toSafeObject());
});

// PATCH /api/v1/users/me — update settings: name, phone, notificationsEnabled
// (Requirement 3 from Part 1: the user must be able to change their settings)
router.patch("/me", requireAuth, async (req, res) => {
  try {
    const { name, phone, notificationsEnabled } = req.body;

    const update = {};
    if (name !== undefined) update.name = name;
    if (phone !== undefined) update.phone = phone;
    if (notificationsEnabled !== undefined) update.notificationsEnabled = notificationsEnabled;

    const user = await User.findByIdAndUpdate(req.user.userId, update, { new: true, runValidators: true });
    if (!user) return res.status(404).json({ message: "User not found." });

    res.json(user.toSafeObject());
  } catch (err) {
    console.error("Update settings error:", err.message);
    res.status(500).json({ message: "Something went wrong while updating settings." });
  }
});

// PATCH /api/v1/users/me/password — change password (requires current password)
router.patch("/me/password", requireAuth, async (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;
    if (!currentPassword || !newPassword) {
      return res.status(400).json({ message: "Current and new password are required." });
    }
    if (newPassword.length < 8) {
      return res.status(400).json({ message: "New password must be at least 8 characters." });
    }

    const user = await User.findById(req.user.userId);
    if (!user) return res.status(404).json({ message: "User not found." });

    const matches = await bcrypt.compare(currentPassword, user.passwordHash);
    if (!matches) {
      return res.status(401).json({ message: "Current password is incorrect." });
    }

    const salt = await bcrypt.genSalt(10);
    user.passwordHash = await bcrypt.hash(newPassword, salt);
    await user.save();

    res.json({ message: "Password updated successfully." });
  } catch (err) {
    console.error("Change password error:", err.message);
    res.status(500).json({ message: "Something went wrong while changing your password." });
  }
});

module.exports = router;
