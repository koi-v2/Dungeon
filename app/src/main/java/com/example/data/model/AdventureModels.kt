package com.example.data.model

data class DungeonZone(
    val id: String,
    val name: String,
    val levelReq: Int,
    val description: String,
    val dangerLevel: String,
    val possibleEnemies: List<MonsterTemplate>
)

data class MonsterTemplate(
    val name: String,
    val hp: Int,
    val atk: Int,
    val def: Int,
    val expReward: Int,
    val goldReward: Int,
    val iconEmoji: String,
    val flavorText: String,
    val specialAttackName: String
)

data class BattleLogEntry(
    val turn: Int,
    val message: String,
    val isCritical: Boolean = false,
    val logType: BattleLogType = BattleLogType.INFO
)

enum class BattleLogType {
    INFO,
    PLAYER_ATTACK,
    PLAYER_SKILL,
    PLAYER_DEFEND,
    PLAYER_POTION,
    ENEMY_ATTACK,
    ENEMY_SKILL,
    VICTORY,
    DEFEAT
}

data class ActiveCombat(
    val dungeonName: String,
    val monsterName: String,
    val monsterEmoji: String,
    var monsterHp: Int,
    val monsterMaxHp: Int,
    val monsterAtk: Int,
    val monsterDef: Int,
    val monsterSpecial: String,
    val expReward: Int,
    val goldReward: Int,
    val eventMonsterId: Long? = null,
    val itemDropName: String? = null,
    var playerHp: Int,
    var playerMaxHp: Int,
    var playerMp: Int,
    var playerMaxMp: Int,
    var isDefending: Boolean = false,
    var isGameOver: Boolean = false,
    var isVictory: Boolean = false,
    var turnCount: Int = 1,
    val logs: List<BattleLogEntry> = emptyList()
)

data class RaidBoss(
    val name: String,
    val title: String,
    val totalHp: Int,
    var currentHp: Int,
    val atk: Int,
    val def: Int,
    val iconEmoji: String,
    val description: String
)
