package com.kasimarketplace.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM cached_products ORDER BY name ASC")
    fun observeAll(): Flow<List<CachedProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<CachedProductEntity>)

    @Query("DELETE FROM cached_products")
    suspend fun clearAll()
}
