package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.GuildEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuildDao {
    @Query("SELECT * FROM guilds ORDER BY level DESC, raidPoints DESC, id ASC")
    fun getAllGuilds(): Flow<List<GuildEntity>>

    @Query("SELECT * FROM guilds WHERE id = :id")
    fun getGuildById(id: Long): Flow<GuildEntity?>

    @Query("SELECT * FROM guilds WHERE id = :id")
    suspend fun getGuildByIdOnce(id: Long): GuildEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuild(guild: GuildEntity): Long

    @Update
    suspend fun updateGuild(guild: GuildEntity)

    @Delete
    suspend fun deleteGuild(guild: GuildEntity)

    @Query("UPDATE guilds SET raidPoints = raidPoints + :points WHERE id = :id")
    suspend fun addRaidPoints(id: Long, points: Int)
}
