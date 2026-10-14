package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY id DESC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterById(id: Long): Flow<CharacterEntity?>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterByIdOnce(id: Long): CharacterEntity?

    @Query("SELECT * FROM characters WHERE guildId = :guildId")
    fun getCharactersByGuild(guildId: Long): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE guildId = :guildId")
    suspend fun getCharactersByGuildOnce(guildId: Long): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)

    @Query("UPDATE characters SET guildId = :guildId WHERE id = :characterId")
    suspend fun updateCharacterGuild(characterId: Long, guildId: Long?)

    @Query("UPDATE characters SET hp = :hp, mp = :mp, potions = :potions WHERE id = :id")
    suspend fun updateVitals(id: Long, hp: Int, mp: Int, potions: Int)

    @Query("UPDATE characters SET exp = :exp, level = :level, gold = :gold, wins = wins + 1 WHERE id = :id")
    suspend fun recordVictory(id: Long, exp: Int, level: Int, gold: Int)

    @Query("SELECT * FROM character_logs WHERE characterId = :characterId ORDER BY timestamp DESC")
    fun getLogsForCharacter(characterId: Long): Flow<List<com.example.data.model.CharacterActionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterLog(log: com.example.data.model.CharacterActionLogEntity): Long

    @Query("DELETE FROM character_logs WHERE characterId = :characterId")
    suspend fun deleteLogsForCharacter(characterId: Long)
}
