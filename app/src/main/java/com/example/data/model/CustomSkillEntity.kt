package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_skills")
data class CustomSkillEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val skillType: String = "ATTACK", // "ATTACK", "HEAL", "ULTIMATE"
    val mpCost: Int = 30,
    val powerMultiplier: Float = 2.5f,
    val iconEmoji: String = "⚡",
    val description: String = "",
    val assignedCharacterId: Long? = null, // null jika skill bebas / belum di-assign
    val assignedMonsterId: Long? = null, // null jika skill bukan untuk monster tertentu
    val isMonsterSkill: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
