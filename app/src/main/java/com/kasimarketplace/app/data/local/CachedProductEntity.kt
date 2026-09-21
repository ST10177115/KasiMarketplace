package com.kasimarketplace.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kasimarketplace.app.data.model.Product

/**
 * Local Room copy of a Product, used so the Home screen can still show the last-loaded
 * listings when the device has no connectivity. This is a lightweight version of the
 * fuller offline-mode-with-sync feature designed in Part 1 (which also queues cart/order
 * actions offline); that full sync behaviour is a PoE-only requirement and is deferred
 * beyond this prototype.
 */
@Entity(tableName = "cached_products")
data class CachedProductEntity(
    @PrimaryKey val id: String,
    val vendorId: String,
    val vendorName: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val stockQty: Int,
    val imageUrl: String?
)

fun CachedProductEntity.toProduct() = Product(
    id = id, vendorId = vendorId, vendorName = vendorName, name = name,
    description = description, price = price, category = category,
    stockQty = stockQty, imageUrl = imageUrl
)

fun Product.toCachedEntity() = CachedProductEntity(
    id = id, vendorId = vendorId, vendorName = vendorName, name = name,
    description = description, price = price, category = category,
    stockQty = stockQty, imageUrl = imageUrl
)
