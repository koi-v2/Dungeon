package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CustomSkillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomSkillDao {
    @Query("SELECT * FROM custom_skills ORDER BY id DESC")
    fun getAllSkills(): Flow<List<CustomSkillEntity>>

    @Query("SELECT * FROM custom_skills ORDER BY id DESC")
    suspend fun getAllSkillsOnce(): List<CustomSkillEntity>

    @Query("SELECT * FROM custom_skills WHERE assignedCharacterId = :characterId")
    fun getSkillsForCharacter(characterId: Long): Flow<List<CustomSkillEntity>>

    @Query("SELECT * FROM custom_skills WHERE assignedCharacterId = :characterId")
    suspend fun getSkillsForCharacterOnce(characterId: Long): List<CustomSkillEntity>

    @Query("SELECT * FROM custom_skills WHERE isMonsterSkill = 1")
    fun getMonsterSkills(): Flow<List<CustomSkillEntity>>

    @Query("SELECT * FROM custom_skills WHERE isMonsterSkill = 1")
    suspend fun getMonsterSkillsOnce(): List<CustomSkillEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: CustomSkillEntity): Long

    @Update
    suspend fun updateSkill(skill: CustomSkillEntity)

    @Delete
    suspend fun deleteSkill(skill: CustomSkillEntity)

    @Query("UPDATE custom_skills SET assignedCharacterId = :characterId WHERE id = :skillId")
    suspend fun assignSkillToCharacter(skillId: Long, characterId: Long?)

    @Query("UPDATE custom_skills SET assignedMonsterId = :monsterId WHERE id = :skillId")
    suspend fun assignSkillToMonster(skillId: Long, monsterId: Long?)

    @Query("SELECT * FROM custom_skills WHERE assignedMonsterId = :monsterId")
    fun getSkillsForMonster(monsterId: Long): Flow<List<CustomSkillEntity>>

    @Query("SELECT * FROM custom_skills WHERE assignedMonsterId = :monsterId")
    suspend fun getSkillsForMonsterOnce(monsterId: Long): List<CustomSkillEntity>
}
