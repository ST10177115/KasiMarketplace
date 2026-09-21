const mongoose = require("mongoose");

// Represents both buyers and vendors. A user becomes a "vendor" simply by
// having isVendor set to true and a businessName populated - we don't use a
// separate collection, which keeps auth and settings logic shared between
// buyers and vendors (see the UML design in the Part 1 Planning & Design doc).
const userSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    // Never store a raw password. This field always holds a bcrypt hash.
    passwordHash: { type: String, required: true },
    phone: { type: String, trim: true },
    preferredLanguage: { type: String, enum: ["en", "zu", "xh"], default: "en" },
    isVendor: { type: Boolean, default: false },
    businessName: { type: String, trim: true },
    // Kept as a single flat flag for this prototype (Part 2). The fuller,
    // per-category notification design from Part 1 (order updates, new
    // messages, price drops individually) is a PoE-only requirement and can
    // be expanded back into an object at that stage without breaking clients,
    // since this field would simply become notificationPreferences.orderUpdates.
    notificationsEnabled: { type: Boolean, default: true },
  },
  {
    timestamps: true,
    toJSON: {
      transform: (_doc, ret) => {
        ret.id = ret._id.toString();
        delete ret._id;
        delete ret.__v;
        delete ret.passwordHash;
        return ret;
      },
    },
  }
);

// Safe to send to the client: relies on the toJSON transform above to strip
// passwordHash and convert _id to a plain string id.
userSchema.methods.toSafeObject = function () {
  return this.toJSON();
};

module.exports = mongoose.model("User", userSchema);
