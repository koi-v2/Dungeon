package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = GuildEntity::class,
            parentColumns = ["id"],
            childColumns = ["guildId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["guildId"])]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val characterClass: CharacterClass,
    val avatarUri: String? = null,
    val hp: Int,
    val maxHp: Int,
    val mp: Int,
    val maxMp: Int,
    val atk: Int,
    val def: Int,
    val spd: Int,
    val crit: Int,
    val bio: String,
    val guildId: Long? = null,
    val level: Int = 1,
    val exp: Int = 0,
    val gold: Int = 100,
    val potions: Int = 3, // Potion HP
    val manaPotions: Int = 2, // Potion MP
    val equippedWeaponName: String? = null,
    val equippedWeaponIcon: String? = null,
    val equippedWeaponStats: String? = null,
    val equippedCustomDrop: String? = null,
    val wins: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
) {
    val combatPower: Int
        get() = (maxHp / 5) + (maxMp / 5) + (atk * 3) + (def * 2) + spd + (crit * 2)
}
