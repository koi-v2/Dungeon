package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event_monsters")
data class EventMonsterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val title: String,
    val iconEmoji: String,
    val hp: Int,
    val maxHp: Int,
    val atk: Int,
    val def: Int,
    val spd: Int,
    val skillName: String,
    val skillDescription: String,
    val skillMultiplier: Float = 1.8f,
    val expDrop: Int,
    val goldDrop: Int,
    val itemDropName: String? = null,
    val isDefeated: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
