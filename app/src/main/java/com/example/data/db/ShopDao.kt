package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ShopItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop_items ORDER BY isFixed DESC, id ASC")
    fun getAllShopItems(): Flow<List<ShopItemEntity>>

    @Query("SELECT * FROM shop_items ORDER BY isFixed DESC, id ASC")
    suspend fun getAllShopItemsOnce(): List<ShopItemEntity>

    @Query("SELECT * FROM shop_items WHERE id = :id")
    fun getShopItemById(id: Long): Flow<ShopItemEntity?>

    @Query("SELECT * FROM shop_items WHERE id = :id")
    suspend fun getShopItemByIdOnce(id: Long): ShopItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShopItem(item: ShopItemEntity): Long

    @Update
    suspend fun updateShopItem(item: ShopItemEntity)

    @Delete
    suspend fun deleteShopItem(item: ShopItemEntity)

    @Query("DELETE FROM shop_items WHERE id = :id AND isFixed = 0")
    suspend fun deleteCustomShopItemById(id: Long)
}
