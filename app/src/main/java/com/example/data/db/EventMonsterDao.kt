package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EventMonsterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventMonsterDao {
    @Query("SELECT * FROM event_monsters ORDER BY isDefeated ASC, id DESC")
    fun getAllEventMonsters(): Flow<List<EventMonsterEntity>>

    @Query("SELECT * FROM event_monsters WHERE isDefeated = 0 ORDER BY id DESC")
    fun getActiveEventMonsters(): Flow<List<EventMonsterEntity>>

    @Query("SELECT * FROM event_monsters WHERE isDefeated = 0 ORDER BY id DESC")
    suspend fun getActiveEventMonstersOnce(): List<EventMonsterEntity>

    @Query("SELECT * FROM event_monsters WHERE id = :id")
    suspend fun getEventMonsterByIdOnce(id: Long): EventMonsterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEventMonster(monster: EventMonsterEntity): Long

    @Update
    suspend fun updateEventMonster(monster: EventMonsterEntity)

    @Delete
    suspend fun deleteEventMonster(monster: EventMonsterEntity)

    @Query("UPDATE event_monsters SET isDefeated = 1, hp = 0 WHERE id = :id")
    suspend fun markMonsterDefeated(id: Long)
}
