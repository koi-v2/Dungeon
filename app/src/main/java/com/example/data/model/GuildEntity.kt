package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guilds")
data class GuildEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val motto: String,
    val description: String,
    val guildType: String,
    val emblemColorHex: Long,
    val emblemIcon: String,
    val level: Int = 1,
    val raidPoints: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
