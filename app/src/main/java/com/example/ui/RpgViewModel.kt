package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.ActiveCombat
import com.example.data.model.BattleLogEntry
import com.example.data.model.BattleLogType
import com.example.data.model.CharacterActionLogEntity
import com.example.data.model.CharacterClass
import com.example.data.model.CharacterEntity
import com.example.data.model.CustomSkillEntity
import com.example.data.model.DungeonZone
import com.example.data.model.EventMonsterEntity
import com.example.data.model.GuildEntity
import com.example.data.model.MonsterTemplate
import com.example.data.model.RaidBoss
import com.example.data.model.ShopItemEntity
import com.example.data.repository.RpgRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.random.Random

data class RaidLogItem(
    val characterName: String,
    val characterClass: CharacterClass,
    val actionText: String,
    val damageDealt: Int
)

data class GuildRaidState(
    val guild: GuildEntity,
    val boss: RaidBoss,
    val logs: List<RaidLogItem>,
    val isFinished: Boolean,
    val isVictory: Boolean,
    val totalRaidPointsGained: Int
)

class RpgViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = RpgRepository(
        context = application,
        characterDao = database.characterDao(),
        guildDao = database.guildDao(),
        shopDao = database.shopDao(),
        customSkillDao = database.customSkillDao(),
        eventMonsterDao = database.eventMonsterDao()
    )

    val characters: StateFlow<List<CharacterEntity>> = repository.allCharacters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val guilds: StateFlow<List<GuildEntity>> = repository.allGuilds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val shopItems: StateFlow<List<ShopItemEntity>> = repository.allShopItems
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customSkills: StateFlow<List<CustomSkillEntity>> = repository.allCustomSkills
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val eventMonsters: StateFlow<List<EventMonsterEntity>> = repository.allEventMonsters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeEventMonsters: StateFlow<List<EventMonsterEntity>> = repository.activeEventMonsters
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedCharacter = MutableStateFlow<CharacterEntity?>(null)
    val selectedCharacter: StateFlow<CharacterEntity?> = _selectedCharacter.asStateFlow()

    private val _selectedGuild = MutableStateFlow<GuildEntity?>(null)
    val selectedGuild: StateFlow<GuildEntity?> = _selectedGuild.asStateFlow()

    private val _activeCombat = MutableStateFlow<ActiveCombat?>(null)
    val activeCombat: StateFlow<ActiveCombat?> = _activeCombat.asStateFlow()

    private val _guildRaidState = MutableStateFlow<GuildRaidState?>(null)
    val guildRaidState: StateFlow<GuildRaidState?> = _guildRaidState.asStateFlow()

    private val _isAutonomousThinking = MutableStateFlow(false)
    val isAutonomousThinking: StateFlow<Boolean> = _isAutonomousThinking.asStateFlow()

    // Character Creation Form State
    var createName = MutableStateFlow("")
    var createClass = MutableStateFlow(CharacterClass.WARRIOR)
    var createHp = MutableStateFlow(CharacterClass.WARRIOR.defaultHp)
    var createMp = MutableStateFlow(CharacterClass.WARRIOR.defaultMp)
    var createAtk = MutableStateFlow(CharacterClass.WARRIOR.defaultAtk)
    var createDef = MutableStateFlow(CharacterClass.WARRIOR.defaultDef)
    var createSpd = MutableStateFlow(CharacterClass.WARRIOR.defaultSpd)
    var createCrit = MutableStateFlow(CharacterClass.WARRIOR.defaultCrit)
    var createBio = MutableStateFlow("")
    var createGuildId = MutableStateFlow<Long?>(null)
    var createAvatarUri = MutableStateFlow<String?>(null)

    // Guild Creation Form State
    var guildCreateName = MutableStateFlow("")
    var guildCreateMotto = MutableStateFlow("")
    var guildCreateDescription = MutableStateFlow("")
    var guildCreateType = MutableStateFlow("Ksatria Garda Depan")
    var guildCreateEmblemColor = MutableStateFlow(0xFFFFB703)
    var guildCreateEmblemIcon = MutableStateFlow("🛡️")

    // Custom Weapon Creation Form State (for Shop)
    var weaponCreateName = MutableStateFlow("")
    var weaponCreateClass = MutableStateFlow<CharacterClass?>(null)
    var weaponCreateIcon = MutableStateFlow("⚔️")
    var weaponCreatePrice = MutableStateFlow(120)
    var weaponCreateAtk = MutableStateFlow(20)
    var weaponCreateDef = MutableStateFlow(5)
    var weaponCreateHp = MutableStateFlow(0)
    var weaponCreateMp = MutableStateFlow(0)
    var weaponCreateSpd = MutableStateFlow(0)
    var weaponCreateCrit = MutableStateFlow(5)
    var weaponCreateDesc = MutableStateFlow("")

    // Event Monster Spawn Form State
    var monsterSpawnName = MutableStateFlow("")
    var monsterSpawnTitle = MutableStateFlow("Penguasa Bencana Kegelapan")
    var monsterSpawnEmoji = MutableStateFlow("👹")
    var monsterSpawnHp = MutableStateFlow(1200)
    var monsterSpawnAtk = MutableStateFlow(65)
    var monsterSpawnDef = MutableStateFlow(28)
    var monsterSpawnSpd = MutableStateFlow(25)
    var monsterSpawnSkillName = MutableStateFlow("Hantaman Penghancur Bumi")
    var monsterSpawnSkillDesc = MutableStateFlow("Serangan dahsyat membelah tanah dan menghancurkan pertahanan lawan.")
    var monsterSpawnSkillMultiplier = MutableStateFlow(2.0f)
    var monsterSpawnExpDrop = MutableStateFlow(250)
    var monsterSpawnGoldDrop = MutableStateFlow(350)
    var monsterSpawnItemDropName = MutableStateFlow("Pedang Bara Api Kuno")

    // Custom Skill Creation Form State
    var skillCreateName = MutableStateFlow("")
    var skillCreateType = MutableStateFlow("ATTACK") // ATTACK, HEAL, ULTIMATE, BUFF
    var skillCreateMpCost = MutableStateFlow(30)
    var skillCreatePowerMultiplier = MutableStateFlow(2.5f)
    var skillCreateIcon = MutableStateFlow("⚡")
    var skillCreateDesc = MutableStateFlow("")
    var skillCreateAssignCharId = MutableStateFlow<Long?>(null)
    var skillCreateAssignMonsterId = MutableStateFlow<Long?>(null)
    var skillCreateIsMonster = MutableStateFlow(false)

    // Event action result modal
    private val _eventActionResult = MutableStateFlow<CharacterActionLogEntity?>(null)
    val eventActionResult: StateFlow<CharacterActionLogEntity?> = _eventActionResult.asStateFlow()

    init {
        viewModelScope.launch {
            characters.collect { list ->
                if (_selectedCharacter.value == null && list.isNotEmpty()) {
                    _selectedCharacter.value = list.first()
                } else if (_selectedCharacter.value != null) {
                    list.find { it.id == _selectedCharacter.value?.id }?.let {
                        _selectedCharacter.value = it
                    }
                }
            }
        }
    }

    fun selectCharacter(character: CharacterEntity) {
        _selectedCharacter.value = character
    }

    fun selectGuild(guild: GuildEntity?) {
        _selectedGuild.value = guild
    }

    fun getLogsForCharacter(characterId: Long): Flow<List<CharacterActionLogEntity>> {
        return repository.getLogsForCharacter(characterId)
    }

    fun setCreateClass(c: CharacterClass) {
        createClass.value = c
        createHp.value = c.defaultHp
        createMp.value = c.defaultMp
        createAtk.value = c.defaultAtk
        createDef.value = c.defaultDef
        createSpd.value = c.defaultSpd
        createCrit.value = c.defaultCrit
    }

    fun resetCharacterForm() {
        createName.value = ""
        setCreateClass(CharacterClass.WARRIOR)
        createBio.value = ""
        createGuildId.value = null
        createAvatarUri.value = null
    }

    fun onAvatarPicked(uri: Uri) {
        viewModelScope.launch {
            val savedPath = repository.saveImageToInternalStorage(uri)
            createAvatarUri.value = savedPath
        }
    }

    fun saveNewCharacter(onSaved: () -> Unit) {
        val name = createName.value.trim()
        if (name.isEmpty()) return

        val newChar = CharacterEntity(
            name = name,
            characterClass = createClass.value,
            avatarUri = createAvatarUri.value,
            hp = createHp.value,
            maxHp = createHp.value,
            mp = createMp.value,
            maxMp = createMp.value,
            atk = createAtk.value,
            def = createDef.value,
            spd = createSpd.value,
            crit = createCrit.value,
            bio = createBio.value.trim(),
            guildId = createGuildId.value,
            level = 1,
            exp = 0,
            gold = 100,
            potions = 3,
            manaPotions = 2,
            wins = 0
        )

        viewModelScope.launch {
            val id = repository.insertCharacter(newChar)
            _selectedCharacter.value = newChar.copy(id = id)
            resetCharacterForm()
            onSaved()
        }
    }

    fun deleteCharacter(character: CharacterEntity) {
        viewModelScope.launch {
            repository.deleteCharacter(character)
            if (_selectedCharacter.value?.id == character.id) {
                _selectedCharacter.value = characters.value.firstOrNull { it.id != character.id }
            }
        }
    }

    fun updateCharacterGuild(characterId: Long, guildId: Long?) {
        viewModelScope.launch {
            repository.updateCharacterGuild(characterId, guildId)
        }
    }

    fun healCharacterWithPotion(character: CharacterEntity) {
        if (character.potions <= 0 || character.hp >= character.maxHp) return
        val healAmount = (character.maxHp * 0.45).toInt().coerceAtLeast(60)
        val newHp = (character.hp + healAmount).coerceAtMost(character.maxHp)
        viewModelScope.launch {
            repository.updateVitals(
                characterId = character.id,
                hp = newHp,
                mp = character.mp,
                potions = character.potions - 1
            )
            repository.insertCharacterLog(
                CharacterActionLogEntity(
                    characterId = character.id,
                    actionType = "REST",
                    location = "Tavern Petualang",
                    title = "Meminum Ramuan Potion Merah",
                    decisionReason = "Memulihkan luka fisik agar siap kembali berpetualang.",
                    narrative = "${character.name} meneguk satu botol potion penyembuh dan memulihkan +$healAmount HP.",
                    hpChange = healAmount
                )
            )
        }
    }

    fun restoreCharacterWithManaPotion(character: CharacterEntity) {
        if (character.manaPotions <= 0 || character.mp >= character.maxMp) return
        val restoreAmount = (character.maxMp * 0.5).toInt().coerceAtLeast(50)
        val newMp = (character.mp + restoreAmount).coerceAtMost(character.maxMp)
        viewModelScope.launch {
            repository.updateVitals(
                characterId = character.id,
                hp = character.hp,
                mp = newMp,
                potions = character.potions,
                manaPotions = character.manaPotions - 1
            )
            repository.insertCharacterLog(
                CharacterActionLogEntity(
                    characterId = character.id,
                    actionType = "REST",
                    location = "Tavern Petualang",
                    title = "Meminum Potion Mana Biru",
                    decisionReason = "Mengisi kembali cadangan energi magis untuk merapal mantra.",
                    narrative = "${character.name} meneguk Potion Mana Biru dan memulihkan +$restoreAmount MP."
                )
            )
        }
    }

    // Shop actions
    fun resetWeaponForm() {
        weaponCreateName.value = ""
        weaponCreateClass.value = null
        weaponCreateIcon.value = "⚔️"
        weaponCreatePrice.value = 120
        weaponCreateAtk.value = 20
        weaponCreateDef.value = 5
        weaponCreateHp.value = 0
        weaponCreateMp.value = 0
        weaponCreateSpd.value = 0
        weaponCreateCrit.value = 5
        weaponCreateDesc.value = ""
    }

    fun saveNewWeapon(onSaved: () -> Unit) {
        val name = weaponCreateName.value.trim()
        if (name.isEmpty()) return

        val weapon = ShopItemEntity(
            name = name,
            itemType = "WEAPON",
            compatibleClass = weaponCreateClass.value,
            iconEmoji = weaponCreateIcon.value,
            price = weaponCreatePrice.value.coerceAtLeast(10),
            bonusAtk = weaponCreateAtk.value,
            bonusDef = weaponCreateDef.value,
            bonusHp = weaponCreateHp.value,
            bonusMp = weaponCreateMp.value,
            bonusSpd = weaponCreateSpd.value,
            bonusCrit = weaponCreateCrit.value,
            description = weaponCreateDesc.value.trim().ifEmpty { "Senjata sakti buatan pandai besi kerajaan." },
            isFixed = false
        )

        viewModelScope.launch {
            repository.insertShopItem(weapon)
            resetWeaponForm()
            onSaved()
        }
    }

    fun deleteCustomShopItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteCustomShopItem(itemId)
        }
    }

    fun buyShopItem(characterId: Long, itemId: Long, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val success = repository.buyShopItem(characterId, itemId)
            onResult?.invoke(success)
        }
    }

    fun triggerAutonomousShopping(characterId: Long, onCompleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isAutonomousThinking.value = true
            repository.simulateAutonomousShopping(characterId)
            _isAutonomousThinking.value = false
            onCompleted?.invoke()
        }
    }

    fun triggerAutonomousThinking(characterId: Long, onCompleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isAutonomousThinking.value = true
            repository.simulateAutonomousDecision(characterId)
            _isAutonomousThinking.value = false
            onCompleted?.invoke()
        }
    }

    fun triggerAllCharactersAutonomous(onCompleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            _isAutonomousThinking.value = true
            characters.value.forEach { char ->
                repository.simulateAutonomousDecision(char.id)
            }
            _isAutonomousThinking.value = false
            onCompleted?.invoke()
        }
    }

    fun resetGuildForm() {
        guildCreateName.value = ""
        guildCreateMotto.value = ""
        guildCreateDescription.value = ""
        guildCreateType.value = "Ksatria Garda Depan"
        guildCreateEmblemColor.value = 0xFFFFB703
        guildCreateEmblemIcon.value = "🛡️"
    }

    fun saveNewGuild(onSaved: () -> Unit) {
        val name = guildCreateName.value.trim()
        if (name.isEmpty()) return

        val newGuild = GuildEntity(
            name = name,
            motto = guildCreateMotto.value.trim(),
            description = guildCreateDescription.value.trim(),
            guildType = guildCreateType.value,
            emblemColorHex = guildCreateEmblemColor.value,
            emblemIcon = guildCreateEmblemIcon.value,
            level = 1,
            raidPoints = 0
        )

        viewModelScope.launch {
            val id = repository.insertGuild(newGuild)
            _selectedGuild.value = newGuild.copy(id = id)
            resetGuildForm()
            onSaved()
        }
    }

    fun deleteGuild(guild: GuildEntity) {
        viewModelScope.launch {
            repository.deleteGuild(guild)
            if (_selectedGuild.value?.id == guild.id) {
                _selectedGuild.value = null
            }
        }
    }

    fun dismissEventActionResult() {
        _eventActionResult.value = null
    }

    // Custom Skill Management
    fun resetSkillCreateForm() {
        skillCreateName.value = ""
        skillCreateType.value = "ATTACK"
        skillCreateMpCost.value = 30
        skillCreatePowerMultiplier.value = 2.5f
        skillCreateIcon.value = "⚡"
        skillCreateDesc.value = ""
        skillCreateAssignCharId.value = null
        skillCreateAssignMonsterId.value = null
        skillCreateIsMonster.value = false
    }

    fun saveCustomSkill(onSaved: () -> Unit) {
        val name = skillCreateName.value.trim()
        if (name.isEmpty()) return

        val skill = CustomSkillEntity(
            name = name,
            skillType = skillCreateType.value,
            mpCost = skillCreateMpCost.value.coerceAtLeast(5),
            powerMultiplier = skillCreatePowerMultiplier.value.coerceAtLeast(1.0f),
            iconEmoji = skillCreateIcon.value.ifEmpty { "⚡" },
            description = skillCreateDesc.value.trim().ifEmpty { "Jurus sakti berkekuatan magis tinggi." },
            assignedCharacterId = skillCreateAssignCharId.value,
            assignedMonsterId = skillCreateAssignMonsterId.value,
            isMonsterSkill = skillCreateIsMonster.value
        )

        viewModelScope.launch {
            repository.insertCustomSkill(skill)
            resetSkillCreateForm()
            onSaved()
        }
    }

    fun deleteCustomSkill(skill: CustomSkillEntity) {
        viewModelScope.launch {
            repository.deleteCustomSkill(skill)
        }
    }

    fun assignSkillToCharacter(skillId: Long, characterId: Long?) {
        viewModelScope.launch {
            repository.assignSkillToCharacter(skillId, characterId)
        }
    }

    fun assignSkillToMonster(skillId: Long, monsterId: Long?) {
        viewModelScope.launch {
            repository.assignSkillToMonster(skillId, monsterId)
        }
    }

    fun getSkillsForCharacter(characterId: Long): Flow<List<CustomSkillEntity>> {
        return repository.getSkillsForCharacter(characterId)
    }

    fun getSkillsForMonster(monsterId: Long): Flow<List<CustomSkillEntity>> {
        return repository.getSkillsForMonster(monsterId)
    }

    // Event Monster Management
    fun resetMonsterSpawnForm() {
        monsterSpawnName.value = ""
        monsterSpawnTitle.value = "Penguasa Bencana Kegelapan"
        monsterSpawnEmoji.value = "👹"
        monsterSpawnHp.value = 1200
        monsterSpawnAtk.value = 65
        monsterSpawnDef.value = 28
        monsterSpawnSpd.value = 25
        monsterSpawnSkillName.value = "Hantaman Penghancur Bumi"
        monsterSpawnSkillDesc.value = "Serangan dahsyat membelah tanah dan menghancurkan pertahanan lawan."
        monsterSpawnSkillMultiplier.value = 2.0f
        monsterSpawnExpDrop.value = 250
        monsterSpawnGoldDrop.value = 350
        monsterSpawnItemDropName.value = "Pedang Bara Api Kuno"
    }

    fun spawnEventMonster(onSpawned: () -> Unit) {
        val name = monsterSpawnName.value.trim()
        if (name.isEmpty()) return

        val monster = EventMonsterEntity(
            name = name,
            title = monsterSpawnTitle.value.trim().ifEmpty { "Monster Bencana Event" },
            iconEmoji = monsterSpawnEmoji.value.ifEmpty { "👹" },
            hp = monsterSpawnHp.value.coerceAtLeast(100),
            maxHp = monsterSpawnHp.value.coerceAtLeast(100),
            atk = monsterSpawnAtk.value.coerceAtLeast(10),
            def = monsterSpawnDef.value.coerceAtLeast(5),
            spd = monsterSpawnSpd.value.coerceAtLeast(5),
            skillName = monsterSpawnSkillName.value.trim().ifEmpty { "Serangan Liar" },
            skillDescription = monsterSpawnSkillDesc.value.trim().ifEmpty { "Serangan mematikan berkekuatan tinggi." },
            skillMultiplier = monsterSpawnSkillMultiplier.value.coerceAtLeast(1.2f),
            expDrop = monsterSpawnExpDrop.value.coerceAtLeast(50),
            goldDrop = monsterSpawnGoldDrop.value.coerceAtLeast(50),
            itemDropName = monsterSpawnItemDropName.value.trim().ifEmpty { "Kristal Pusaka Abadi" },
            isDefeated = false
        )

        viewModelScope.launch {
            repository.insertEventMonster(monster)
            resetMonsterSpawnForm()
            onSpawned()
        }
    }

    fun deleteEventMonster(monster: EventMonsterEntity) {
        viewModelScope.launch {
            repository.deleteEventMonster(monster)
        }
    }

    fun triggerEventMonsterSoloRaid(monster: EventMonsterEntity, characterId: Long? = null) {
        viewModelScope.launch {
            _isAutonomousThinking.value = true
            val chosenCharId = characterId ?: _selectedCharacter.value?.id ?: characters.value.firstOrNull()?.id
            if (chosenCharId != null) {
                val log = repository.simulateAutonomousEventDecision(chosenCharId, monster.id)
                _eventActionResult.value = log
            }
            _isAutonomousThinking.value = false
        }
    }

    fun getDungeonZones(): List<DungeonZone> = repository.getDungeonZones()

    fun startDungeonEncounter(zone: DungeonZone, character: CharacterEntity) {
        val enemyTemplate = zone.possibleEnemies.random()
        val initialLogs = listOf(
            BattleLogEntry(
                turn = 1,
                message = "⚔️ Kamu memasuki ${zone.name}.\nDi balik kabut misterius, muncul ${enemyTemplate.iconEmoji} ${enemyTemplate.name}! \"${enemyTemplate.flavorText}\"",
                logType = BattleLogType.INFO
            )
        )

        _activeCombat.value = ActiveCombat(
            dungeonName = zone.name,
            monsterName = enemyTemplate.name,
            monsterEmoji = enemyTemplate.iconEmoji,
            monsterHp = enemyTemplate.hp,
            monsterMaxHp = enemyTemplate.hp,
            monsterAtk = enemyTemplate.atk,
            monsterDef = enemyTemplate.def,
            monsterSpecial = enemyTemplate.specialAttackName,
            expReward = enemyTemplate.expReward,
            goldReward = enemyTemplate.goldReward,
            playerHp = character.hp.coerceAtLeast(1),
            playerMaxHp = character.maxHp,
            playerMp = character.mp,
            playerMaxMp = character.maxMp,
            isDefending = false,
            isGameOver = false,
            isVictory = false,
            turnCount = 1,
            logs = initialLogs
        )
    }

    fun startEventMonsterManualCombat(monster: EventMonsterEntity, character: CharacterEntity) {
        val initialLogs = listOf(
            BattleLogEntry(
                turn = 1,
                message = "🔥 [PERTARUNGAN EVENT BOSS]\nKamu menantang ${monster.iconEmoji} ${monster.name} (\"${monster.title}\")!\nHadiah kemenangan: +${monster.expDrop} EXP, +${monster.goldDrop} Koin Emas, & Drop Spesial [${monster.itemDropName ?: "Pusaka"}]!",
                logType = BattleLogType.INFO
            )
        )

        _activeCombat.value = ActiveCombat(
            dungeonName = "Arena Event: ${monster.name}",
            monsterName = monster.name,
            monsterEmoji = monster.iconEmoji,
            monsterHp = monster.hp,
            monsterMaxHp = monster.maxHp,
            monsterAtk = monster.atk,
            monsterDef = monster.def,
            monsterSpecial = monster.skillName,
            expReward = monster.expDrop,
            goldReward = monster.goldDrop,
            eventMonsterId = monster.id,
            itemDropName = monster.itemDropName,
            playerHp = character.hp.coerceAtLeast(1),
            playerMaxHp = character.maxHp,
            playerMp = character.mp,
            playerMaxMp = character.maxMp,
            isDefending = false,
            isGameOver = false,
            isVictory = false,
            turnCount = 1,
            logs = initialLogs
        )
    }

    fun playerAttack() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        val isCrit = Random.nextInt(100) < character.crit
        val baseDamage = max(10, character.atk - (combat.monsterDef / 2))
        val finalDamage = if (isCrit) (baseDamage * 1.8).toInt() else baseDamage + Random.nextInt(-3, 6)
        val finalPlayerDamage = finalDamage.coerceAtLeast(5)

        val newMonsterHp = max(0, combat.monsterHp - finalPlayerDamage)

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = if (isCrit) {
                    "💥 [CRITICAL HIT!] ${character.name} menebas titik lemah ${combat.monsterName} sebesar $finalPlayerDamage DMG!"
                } else {
                    "🗡️ ${character.name} menyerang ${combat.monsterName} dan menghasilkan $finalPlayerDamage DMG."
                },
                isCritical = isCrit,
                logType = BattleLogType.PLAYER_ATTACK
            )
        )

        val strikeExp = if (isCrit) 20 else 14
        viewModelScope.launch {
            val levelUpResult = repository.addExpAndCheckLevelUp(character.id, strikeExp)
            if (levelUpResult != null && levelUpResult.isLevelUp) {
                val updatedChar = levelUpResult.updatedCharacter
                nextLogs.add(
                    BattleLogEntry(
                        turn = turn,
                        message = "🎉 [AUTO LEVEL UP!] Ayunan serangan memicu lonjakan kekuatan! ${character.name} NAIK KE LEVEL ${levelUpResult.newLevel}!\nHP & MP terisi penuh dan statistik tempur meningkat!",
                        isCritical = true,
                        logType = BattleLogType.VICTORY
                    )
                )
                _activeCombat.value = _activeCombat.value?.copy(
                    playerHp = updatedChar.hp,
                    playerMaxHp = updatedChar.maxHp,
                    playerMp = updatedChar.mp,
                    playerMaxMp = updatedChar.maxMp,
                    logs = nextLogs
                )
            }
        }

        if (newMonsterHp <= 0) {
            handleVictory(combat, character, nextLogs, newMonsterHp)
            return
        }

        handleMonsterTurn(combat, character, nextLogs, newMonsterHp, playerDefending = false)
    }

    fun playerSkill() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val skillCost = character.characterClass.skillMpCost
        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        if (combat.playerMp < skillCost) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚠️ Mana tidak mencukupi! Butuh $skillCost MP untuk merapal ${character.characterClass.skillName}.",
                    logType = BattleLogType.INFO
                )
            )
            _activeCombat.value = combat.copy(logs = nextLogs)
            return
        }

        val newPlayerMp = combat.playerMp - skillCost
        val skillMultiplier = when (character.characterClass) {
            CharacterClass.WARRIOR -> 2.2
            CharacterClass.MAGE -> 2.8
            CharacterClass.ARCHER -> 2.5
            CharacterClass.ASSASSIN -> 2.7
        }
        val isCrit = Random.nextInt(100) < (character.crit + 25)
        val baseDamage = (character.atk * skillMultiplier).toInt()
        val finalDamage = if (isCrit) (baseDamage * 1.6).toInt() else baseDamage
        val finalSkillDamage = finalDamage.coerceAtLeast(15)

        val newMonsterHp = max(0, combat.monsterHp - finalSkillDamage)

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = "✨ [SKILL] ${character.name} melepaskan ${character.characterClass.iconEmoji} \"${character.characterClass.skillName}\"!\nMenghancurkan musuh sebesar $finalSkillDamage DMG! (-$skillCost MP)",
                isCritical = true,
                logType = BattleLogType.PLAYER_SKILL
            )
        )

        val skillExp = 28
        viewModelScope.launch {
            val levelUpResult = repository.addExpAndCheckLevelUp(character.id, skillExp)
            if (levelUpResult != null && levelUpResult.isLevelUp) {
                val updatedChar = levelUpResult.updatedCharacter
                nextLogs.add(
                    BattleLogEntry(
                        turn = turn,
                        message = "🎉 [AUTO LEVEL UP!] Rapalan jurus memicu pencerahan magis! ${character.name} NAIK KE LEVEL ${levelUpResult.newLevel}!\nHP & MP terisi penuh dan statistik tempur meningkat!",
                        isCritical = true,
                        logType = BattleLogType.VICTORY
                    )
                )
                _activeCombat.value = _activeCombat.value?.copy(
                    playerHp = updatedChar.hp,
                    playerMaxHp = updatedChar.maxHp,
                    playerMp = updatedChar.mp,
                    playerMaxMp = updatedChar.maxMp,
                    logs = nextLogs
                )
            }
        }

        if (newMonsterHp <= 0) {
            handleVictory(combat.copy(playerMp = newPlayerMp), character, nextLogs, newMonsterHp)
            return
        }

        val defenseBuff = character.characterClass == CharacterClass.WARRIOR
        handleMonsterTurn(combat.copy(playerMp = newPlayerMp), character, nextLogs, newMonsterHp, playerDefending = defenseBuff)
    }

    fun playerCastCustomSkill(skill: CustomSkillEntity) {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        if (combat.playerMp < skill.mpCost) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚠️ Mana tidak mencukupi! Butuh ${skill.mpCost} MP untuk merapal ${skill.name}.",
                    logType = BattleLogType.INFO
                )
            )
            _activeCombat.value = combat.copy(logs = nextLogs)
            return
        }

        val newPlayerMp = combat.playerMp - skill.mpCost

        if (skill.skillType == "HEAL") {
            val healAmount = (character.maxHp * (skill.powerMultiplier * 0.25f)).toInt().coerceAtLeast(80)
            val newPlayerHp = (combat.playerHp + healAmount).coerceAtMost(combat.playerMaxHp)

            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "💖 [CUSTOM SKILL] ${character.name} merapal ${skill.iconEmoji} \"${skill.name}\"!\nMemulihkan +$healAmount HP! (-${skill.mpCost} MP)",
                    logType = BattleLogType.PLAYER_SKILL
                )
            )

            _activeCombat.value = combat.copy(
                playerHp = newPlayerHp,
                playerMp = newPlayerMp,
                logs = nextLogs
            )

            viewModelScope.launch {
                repository.updateVitals(character.id, newPlayerHp, newPlayerMp, character.potions)
            }

            handleMonsterTurn(combat.copy(playerHp = newPlayerHp, playerMp = newPlayerMp), character, nextLogs, combat.monsterHp, playerDefending = false)
        } else {
            val isCrit = Random.nextInt(100) < (character.crit + 20)
            val baseDamage = (character.atk * skill.powerMultiplier).toInt()
            val finalDamage = if (isCrit) (baseDamage * 1.6).toInt() else baseDamage
            val finalSkillDamage = finalDamage.coerceAtLeast(20)

            val newMonsterHp = max(0, combat.monsterHp - finalSkillDamage)

            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚡ [CUSTOM SKILL] ${character.name} melepaskan jurus ${skill.iconEmoji} \"${skill.name}\"!\nMenghancurkan lawan sebesar $finalSkillDamage DMG! (-${skill.mpCost} MP)",
                    isCritical = isCrit,
                    logType = BattleLogType.PLAYER_SKILL
                )
            )

            _activeCombat.value = combat.copy(
                monsterHp = newMonsterHp,
                playerMp = newPlayerMp,
                logs = nextLogs
            )

            viewModelScope.launch {
                repository.updateVitals(character.id, combat.playerHp, newPlayerMp, character.potions)
            }

            if (newMonsterHp <= 0) {
                handleVictory(combat.copy(monsterHp = 0, playerMp = newPlayerMp), character, nextLogs, 0)
                return
            }

            handleMonsterTurn(combat.copy(monsterHp = newMonsterHp, playerMp = newPlayerMp), character, nextLogs, newMonsterHp, playerDefending = false)
        }
    }

    fun playerDefend() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        val recoveredMp = (combat.playerMp + 12).coerceAtMost(combat.playerMaxMp)

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = "🛡️ ${character.name} memasang kuda-kuda bertahan! Kerusakan yang diterima berkurang 60% dan memulihkan 12 MP.",
                logType = BattleLogType.PLAYER_DEFEND
            )
        )

        handleMonsterTurn(combat.copy(playerMp = recoveredMp), character, nextLogs, combat.monsterHp, playerDefending = true)
    }

    fun playerPotion() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        if (character.potions <= 0) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚠️ Kamu kehabisan Potion Darah di tasmu! Beli di Toko untuk menambah persediaan.",
                    logType = BattleLogType.INFO
                )
            )
            _activeCombat.value = combat.copy(logs = nextLogs)
            return
        }

        val healAmount = (combat.playerMaxHp * 0.45).toInt().coerceAtLeast(60)
        val newPlayerHp = (combat.playerHp + healAmount).coerceAtMost(combat.playerMaxHp)

        viewModelScope.launch {
            repository.updateVitals(
                characterId = character.id,
                hp = newPlayerHp,
                mp = combat.playerMp,
                potions = character.potions - 1
            )
        }

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = "🧪 ${character.name} meneguk Potion Merah!\nMemulihkan +$healAmount HP! (Tersisa ${character.potions - 1} Potion HP)",
                logType = BattleLogType.PLAYER_POTION
            )
        )

        handleMonsterTurn(combat.copy(playerHp = newPlayerHp), character, nextLogs, combat.monsterHp, playerDefending = false)
    }

    fun playerManaPotion() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount

        if (character.manaPotions <= 0) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚠️ Kamu kehabisan Potion Mana Biru di tasmu! Kunjungi Toko untuk membeli Potion Mana.",
                    logType = BattleLogType.INFO
                )
            )
            _activeCombat.value = combat.copy(logs = nextLogs)
            return
        }

        val restoreAmount = (combat.playerMaxMp * 0.5).toInt().coerceAtLeast(50)
        val newPlayerMp = (combat.playerMp + restoreAmount).coerceAtMost(combat.playerMaxMp)

        viewModelScope.launch {
            repository.updateVitals(
                characterId = character.id,
                hp = combat.playerHp,
                mp = newPlayerMp,
                potions = character.potions,
                manaPotions = character.manaPotions - 1
            )
        }

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = "💧 ${character.name} meneguk Potion Mana Biru!\nMemulihkan +$restoreAmount MP! (Tersisa ${character.manaPotions - 1} Potion MP)",
                logType = BattleLogType.PLAYER_POTION
            )
        )

        handleMonsterTurn(combat.copy(playerMp = newPlayerMp), character, nextLogs, combat.monsterHp, playerDefending = false)
    }

    fun playerFlee() {
        val combat = _activeCombat.value ?: return
        val character = _selectedCharacter.value ?: return
        if (combat.isGameOver) return

        val nextLogs = combat.logs.toMutableList()
        val turn = combat.turnCount
        val fleeSuccess = (character.spd > 25) || (Random.nextInt(100) < 65)

        if (fleeSuccess) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "🏃💨 ${character.name} berhasil meloloskan diri dan mundur dengan selamat ke kota aman!",
                    logType = BattleLogType.INFO
                )
            )
            _activeCombat.value = combat.copy(
                isGameOver = true,
                isVictory = false,
                logs = nextLogs
            )
            viewModelScope.launch {
                repository.updateVitals(character.id, combat.playerHp, combat.playerMp, character.potions)
            }
        } else {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "❌ Upaya melarikan diri gagal! ${combat.monsterName} menghalangi jalan keluar!",
                    logType = BattleLogType.INFO
                )
            )
            handleMonsterTurn(combat, character, nextLogs, combat.monsterHp, playerDefending = false)
        }
    }

    private fun handleMonsterTurn(
        combat: ActiveCombat,
        character: CharacterEntity,
        nextLogs: MutableList<BattleLogEntry>,
        newMonsterHp: Int,
        playerDefending: Boolean
    ) {
        val turn = combat.turnCount
        val useSpecial = Random.nextInt(100) < 25
        val baseDamage = max(8, combat.monsterAtk - (character.def / 2))
        val rawDamage = if (useSpecial) (baseDamage * 1.5).toInt() else baseDamage + Random.nextInt(-2, 4)
        val finalMonsterDamage = (if (playerDefending) (rawDamage * 0.4).toInt() else rawDamage).coerceAtLeast(3)

        val newPlayerHp = max(0, combat.playerHp - finalMonsterDamage)

        if (useSpecial) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚡ ${combat.monsterEmoji} ${combat.monsterName} merapal serangan khusus \"${combat.monsterSpecial}\"!\nMenerjangmu sebesar $finalMonsterDamage DMG!",
                    logType = BattleLogType.ENEMY_SKILL
                )
            )
        } else {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn,
                    message = "⚔️ ${combat.monsterEmoji} ${combat.monsterName} melancarkan serangan dan memberikan $finalMonsterDamage DMG.",
                    logType = BattleLogType.ENEMY_ATTACK
                )
            )
        }

        if (newPlayerHp <= 0) {
            nextLogs.add(
                BattleLogEntry(
                    turn = turn + 1,
                    message = "☠️ ${character.name} tumbang dalam pertempuran...\nKawan seperjuangan membawamu kembali ke Guild Hall untuk dirawat.",
                    logType = BattleLogType.DEFEAT
                )
            )
            _activeCombat.value = combat.copy(
                monsterHp = newMonsterHp,
                playerHp = 0,
                isGameOver = true,
                isVictory = false,
                turnCount = turn + 1,
                logs = nextLogs
            )
            viewModelScope.launch {
                val reviveHp = (character.maxHp * 0.35).toInt().coerceAtLeast(30)
                repository.updateVitals(character.id, reviveHp, character.maxMp / 2, character.potions)
            }
        } else {
            _activeCombat.value = combat.copy(
                monsterHp = newMonsterHp,
                playerHp = newPlayerHp,
                turnCount = turn + 1,
                logs = nextLogs
            )
            viewModelScope.launch {
                repository.updateVitals(character.id, newPlayerHp, combat.playerMp, character.potions)
            }
        }
    }

    private fun handleVictory(
        combat: ActiveCombat,
        character: CharacterEntity,
        nextLogs: MutableList<BattleLogEntry>,
        newMonsterHp: Int
    ) {
        val turn = combat.turnCount
        val potionFound = Random.nextInt(100) < 30
        val potionMsg = if (potionFound) "\n🧪 Kamu juga menemukan 1x Potion Penyembuh di saku musuh!" else ""
        val dropMsg = if (!combat.itemDropName.isNullOrBlank()) "\n🎁 Berhasil mengklaim Drop Pusaka: \"${combat.itemDropName}\"!" else ""

        nextLogs.add(
            BattleLogEntry(
                turn = turn,
                message = "🏆 KEMENANGAN!\n${combat.monsterEmoji} ${combat.monsterName} berhasil dikalahkan!\n⭐ Memperoleh +${combat.expReward} EXP\n💰 Memperoleh +${combat.goldReward} Koin Emas$potionMsg$dropMsg",
                logType = BattleLogType.VICTORY
            )
        )

        _activeCombat.value = combat.copy(
            monsterHp = newMonsterHp,
            isGameOver = true,
            isVictory = true,
            turnCount = turn + 1,
            logs = nextLogs
        )

        viewModelScope.launch {
            val newPotions = if (potionFound) character.potions + 1 else character.potions
            repository.updateVitals(character.id, combat.playerHp, combat.playerMp, newPotions)
            val levelUpResult = repository.addExpAndCheckLevelUp(
                characterId = character.id,
                expGained = combat.expReward,
                goldGained = combat.goldReward
            )

            // If event monster, mark defeated and award drop to character
            if (combat.eventMonsterId != null) {
                repository.markMonsterDefeated(combat.eventMonsterId)
                if (!combat.itemDropName.isNullOrBlank()) {
                    val charNow = database.characterDao().getCharacterByIdOnce(character.id)
                    if (charNow != null) {
                        database.characterDao().updateCharacter(charNow.copy(equippedCustomDrop = combat.itemDropName, wins = charNow.wins + 1))
                    }
                    repository.insertShopItem(
                        ShopItemEntity(
                            name = combat.itemDropName,
                            itemType = "WEAPON",
                            compatibleClass = null,
                            iconEmoji = "🎁",
                            price = 15,
                            bonusAtk = 38,
                            bonusDef = 16,
                            bonusHp = 85,
                            description = "Pusaka langka rampasan tempur dari event boss ${combat.monsterName}!",
                            isFixed = false
                        )
                    )
                } else {
                    val charNow = database.characterDao().getCharacterByIdOnce(character.id)
                    if (charNow != null) {
                        database.characterDao().updateCharacter(charNow.copy(wins = charNow.wins + 1))
                    }
                }
            } else {
                val charAfter = database.characterDao().getCharacterByIdOnce(character.id)
                if (charAfter != null) {
                    database.characterDao().updateCharacter(charAfter.copy(wins = charAfter.wins + 1))
                }
            }

            val actionType = if (combat.eventMonsterId != null) "EVENT_SOLO" else "HUNT"
            val titleText = if (combat.eventMonsterId != null) "Solo Menaklukkan Event Boss: ${combat.monsterName}" else "Menaklukkan ${combat.monsterName}"

            repository.insertCharacterLog(
                CharacterActionLogEntity(
                    characterId = character.id,
                    actionType = actionType,
                    location = combat.dungeonName,
                    title = titleText,
                    decisionReason = "Pertarungan langsung di medan pertempuran.",
                    narrative = "Berhasil menumpas ${combat.monsterEmoji} ${combat.monsterName}. Mendapatkan +${combat.expReward} EXP dan +${combat.goldReward} G." + (if (!combat.itemDropName.isNullOrBlank()) " Mendapatkan drop pusaka: [${combat.itemDropName}]." else "") + if (levelUpResult?.isLevelUp == true) " ✨ Kemenangan ini memicu AUTO LEVEL UP ke Level ${levelUpResult.newLevel}!" else "",
                    expGained = combat.expReward,
                    goldGained = combat.goldReward,
                    isLevelUp = levelUpResult?.isLevelUp ?: false
                )
            )
        }
    }

    fun closeCombat() {
        _activeCombat.value = null
    }

    // Guild Raid Simulation
    fun startGuildRaid(guild: GuildEntity) {
        viewModelScope.launch {
            val members = repository.getCharactersByGuildOnce(guild.id)
            val boss = repository.getRaidBoss()
            val raidLogs = mutableListOf<RaidLogItem>()

            if (members.isEmpty()) {
                _guildRaidState.value = GuildRaidState(
                    guild = guild,
                    boss = boss,
                    logs = listOf(
                        RaidLogItem(
                            characterName = "Guild Master",
                            characterClass = CharacterClass.WARRIOR,
                            actionText = "Belum ada anggota yang bergabung di Guild ini. Tambahkan atau masukkan karakter ke guild terlebih dahulu!",
                            damageDealt = 0
                        )
                    ),
                    isFinished = true,
                    isVictory = false,
                    totalRaidPointsGained = 0
                )
                return@launch
            }

            var currentBossHp = boss.totalHp
            var totalDamage = 0

            for (member in members) {
                val memberDamage = member.combatPower * Random.nextInt(2, 4)
                totalDamage += memberDamage
                currentBossHp = max(0, currentBossHp - memberDamage)

                raidLogs.add(
                    RaidLogItem(
                        characterName = member.name,
                        characterClass = member.characterClass,
                        actionText = "melancarkan serangan gabungan ${member.characterClass.skillName}!",
                        damageDealt = memberDamage
                    )
                )

                repository.insertCharacterLog(
                    CharacterActionLogEntity(
                        characterId = member.id,
                        actionType = "RAID",
                        location = "Sarang Naga Malakor",
                        title = "Partisipasi Ekspedisi Raid Guild",
                        decisionReason = "Menjawab panggilan perang Guild ${guild.name}.",
                        narrative = "${member.name} melancarkan ${member.characterClass.skillName} menghasilkan $memberDamage DMG pada Naga Malakor!",
                        expGained = 60,
                        goldGained = 80
                    )
                )
                repository.addExpAndCheckLevelUp(member.id, 60, 80)
            }

            val isVictory = currentBossHp <= 0 || totalDamage >= 1200
            val points = if (isVictory) 150 else 60

            repository.addGuildRaidPoints(guild.id, points)

            _guildRaidState.value = GuildRaidState(
                guild = guild,
                boss = boss.copy(currentHp = max(0, currentBossHp)),
                logs = raidLogs,
                isFinished = true,
                isVictory = isVictory,
                totalRaidPointsGained = points
            )
        }
    }

    fun closeGuildRaid() {
        _guildRaidState.value = null
    }
}
