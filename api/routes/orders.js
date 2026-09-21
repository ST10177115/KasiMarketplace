const express = require("express");
const Order = require("../models/Order");
const Product = require("../models/Product");
const { requireAuth } = require("../middleware/auth");

const router = express.Router();

// GET /api/v1/orders — the logged-in user's own order history
router.get("/", requireAuth, async (req, res) => {
  const orders = await Order.find({ buyerId: req.user.userId }).sort({ createdAt: -1 });
  res.json(orders);
});

// POST /api/v1/orders — place an order from the cart
// Body: { items: [{ productId, quantity }] }
router.post("/", requireAuth, async (req, res) => {
  try {
    const { items } = req.body;
    if (!Array.isArray(items) || items.length === 0) {
      return res.status(400).json({ message: "Order must contain at least one item." });
    }

    // Re-fetch products server-side rather than trusting client-supplied prices,
    // so a tampered request can't place an order at the wrong price.
    const orderItems = [];
    let totalAmount = 0;

    for (const item of items) {
      const product = await Product.findById(item.productId);
      if (!product) {
        return res.status(404).json({ message: `Product ${item.productId} not found.` });
      }
      if (product.stockQty < item.quantity) {
        return res.status(409).json({ message: `Not enough stock for "${product.name}".` });
      }

      orderItems.push({
        productId: product._id,
        name: product.name,
        quantity: item.quantity,
        unitPrice: product.price,
      });
      totalAmount += product.price * item.quantity;

      product.stockQty -= item.quantity;
      await product.save();
    }

    const order = await Order.create({
      buyerId: req.user.userId,
      items: orderItems,
      totalAmount,
      status: "Pending",
    });

    res.status(201).json(order);
  } catch (err) {
    console.error("Create order error:", err.message);
    res.status(500).json({ message: "Something went wrong while placing the order." });
  }
});

module.exports = router;
