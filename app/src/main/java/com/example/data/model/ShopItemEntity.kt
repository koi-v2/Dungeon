package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val itemType: String, // "POTION_HP", "POTION_MP", "WEAPON"
    val compatibleClass: CharacterClass? = null, // null = semua kelas
    val iconEmoji: String,
    val price: Int,
    val bonusAtk: Int = 0,
    val bonusDef: Int = 0,
    val bonusHp: Int = 0,
    val bonusMp: Int = 0,
    val bonusSpd: Int = 0,
    val bonusCrit: Int = 0,
    val description: String,
    val isFixed: Boolean = false, // true untuk potion tetap yang tidak bisa diubah/dihapus
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalPowerBonus: Int
        get() = (bonusHp / 5) + (bonusMp / 5) + (bonusAtk * 3) + (bonusDef * 2) + bonusSpd + (bonusCrit * 2)

    val statsSummary: String
        get() = buildList {
            if (bonusAtk > 0) add("ATK +$bonusAtk")
            if (bonusDef > 0) add("DEF +$bonusDef")
            if (bonusHp > 0) add("HP +$bonusHp")
            if (bonusMp > 0) add("MP +$bonusMp")
            if (bonusSpd > 0) add("SPD +$bonusSpd")
            if (bonusCrit > 0) add("CRIT +$bonusCrit%")
        }.joinToString(" • ").ifEmpty { "Tanpa bonus stat tambahan" }
}
