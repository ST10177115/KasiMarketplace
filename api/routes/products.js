const express = require("express");
const Product = require("../models/Product");
const { requireAuth } = require("../middleware/auth");

const router = express.Router();

// Converts a populated Product document into the flat shape the Android app
// expects: vendorId as a plain string id, plus a flattened vendorName
// (businessName if the vendor has one, otherwise their personal name).
function serializeProduct(productDoc) {
  const json = productDoc.toJSON();
  const vendor = productDoc.vendorId; // populated User document, or just an ObjectId if not populated
  if (vendor && typeof vendor === "object" && vendor.name) {
    json.vendorId = vendor._id.toString();
    json.vendorName = vendor.businessName || vendor.name;
  } else {
    json.vendorName = "Unknown Vendor";
  }
  return json;
}

// GET /api/v1/products — browse/search listings. Public (no login required to browse).
// Supports optional query params: ?search=bread&category=Bakery
router.get("/", async (req, res) => {
  try {
    const { search, category } = req.query;
    const filter = {};
    if (search) filter.name = { $regex: search, $options: "i" };
    if (category) filter.category = category;

    const products = await Product.find(filter)
      .populate("vendorId", "name businessName")
      .sort({ createdAt: -1 });

    res.json(products.map(serializeProduct));
  } catch (err) {
    console.error("Fetch products error:", err.message);
    res.status(500).json({ message: "Something went wrong while fetching products." });
  }
});

// GET /api/v1/products/:id — single product detail
router.get("/:id", async (req, res) => {
  try {
    const product = await Product.findById(req.params.id).populate("vendorId", "name businessName");
    if (!product) return res.status(404).json({ message: "Product not found." });
    res.json(serializeProduct(product));
  } catch (err) {
    res.status(400).json({ message: "Invalid product id." });
  }
});

// POST /api/v1/products — create a listing (requires login; the logged-in user becomes the vendor)
router.post("/", requireAuth, async (req, res) => {
  try {
    const { name, description, price, category, stockQty, imageUrl } = req.body;
    if (!name || !description || price === undefined || !category) {
      return res.status(400).json({ message: "name, description, price, and category are required." });
    }

    const product = await Product.create({
      vendorId: req.user.userId,
      name,
      description,
      price,
      category,
      stockQty: stockQty ?? 1,
      imageUrl: imageUrl ?? "",
    });

    const populated = await product.populate("vendorId", "name businessName");
    res.status(201).json(serializeProduct(populated));
  } catch (err) {
    console.error("Create product error:", err.message);
    res.status(500).json({ message: "Something went wrong while creating the listing." });
  }
});

module.exports = router;
