package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_logs",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["characterId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["characterId"])]
)
data class CharacterActionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val characterId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val actionType: String, // "HUNT", "RAID", "LEVEL_UP", "REST", "THINKING"
    val location: String,
    val title: String,
    val decisionReason: String,
    val narrative: String,
    val expGained: Int = 0,
    val goldGained: Int = 0,
    val hpChange: Int = 0,
    val isLevelUp: Boolean = false
)
