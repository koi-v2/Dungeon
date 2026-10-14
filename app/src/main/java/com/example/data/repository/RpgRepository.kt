package com.example.data.repository

import android.content.Context
import android.net.Uri
import com.example.data.db.CharacterDao
import com.example.data.db.CustomSkillDao
import com.example.data.db.EventMonsterDao
import com.example.data.db.GuildDao
import com.example.data.db.ShopDao
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.random.Random

data class AutoLevelUpResult(
    val isLevelUp: Boolean,
    val oldLevel: Int,
    val newLevel: Int,
    val updatedCharacter: CharacterEntity
)

class RpgRepository(
    private val context: Context,
    private val characterDao: CharacterDao,
    private val guildDao: GuildDao,
    private val shopDao: ShopDao,
    private val customSkillDao: CustomSkillDao,
    private val eventMonsterDao: EventMonsterDao
) {
    val allCharacters: Flow<List<CharacterEntity>> = characterDao.getAllCharacters()
    val allGuilds: Flow<List<GuildEntity>> = guildDao.getAllGuilds()
    val allShopItems: Flow<List<ShopItemEntity>> = shopDao.getAllShopItems()
    val allEventMonsters: Flow<List<EventMonsterEntity>> = eventMonsterDao.getAllEventMonsters()
    val activeEventMonsters: Flow<List<EventMonsterEntity>> = eventMonsterDao.getActiveEventMonsters()
    val allCustomSkills: Flow<List<CustomSkillEntity>> = customSkillDao.getAllSkills()

    fun getSkillsForCharacter(characterId: Long): Flow<List<CustomSkillEntity>> {
        return customSkillDao.getSkillsForCharacter(characterId)
    }

    suspend fun getSkillsForCharacterOnce(characterId: Long): List<CustomSkillEntity> {
        return withContext(Dispatchers.IO) {
            customSkillDao.getSkillsForCharacterOnce(characterId)
        }
    }

    suspend fun insertCustomSkill(skill: CustomSkillEntity): Long {
        return withContext(Dispatchers.IO) {
            customSkillDao.insertSkill(skill)
        }
    }

    suspend fun deleteCustomSkill(skill: CustomSkillEntity) {
        withContext(Dispatchers.IO) {
            customSkillDao.deleteSkill(skill)
        }
    }

    suspend fun assignSkillToCharacter(skillId: Long, characterId: Long?) {
        withContext(Dispatchers.IO) {
            customSkillDao.assignSkillToCharacter(skillId, characterId)
        }
    }

    suspend fun assignSkillToMonster(skillId: Long, monsterId: Long?) {
        withContext(Dispatchers.IO) {
            customSkillDao.assignSkillToMonster(skillId, monsterId)
        }
    }

    fun getSkillsForMonster(monsterId: Long): Flow<List<CustomSkillEntity>> {
        return customSkillDao.getSkillsForMonster(monsterId)
    }

    suspend fun getSkillsForMonsterOnce(monsterId: Long): List<CustomSkillEntity> {
        return withContext(Dispatchers.IO) {
            customSkillDao.getSkillsForMonsterOnce(monsterId)
        }
    }

    suspend fun insertEventMonster(monster: EventMonsterEntity): Long {
        return withContext(Dispatchers.IO) {
            eventMonsterDao.insertEventMonster(monster)
        }
    }

    suspend fun deleteEventMonster(monster: EventMonsterEntity) {
        withContext(Dispatchers.IO) {
            eventMonsterDao.deleteEventMonster(monster)
        }
    }

    suspend fun markMonsterDefeated(id: Long) {
        withContext(Dispatchers.IO) {
            eventMonsterDao.markMonsterDefeated(id)
        }
    }

    suspend fun getEventMonsterByIdOnce(id: Long): EventMonsterEntity? {
        return withContext(Dispatchers.IO) {
            eventMonsterDao.getEventMonsterByIdOnce(id)
        }
    }

    fun getCharactersByGuild(guildId: Long): Flow<List<CharacterEntity>> {
        return characterDao.getCharactersByGuild(guildId)
    }

    suspend fun getCharactersByGuildOnce(guildId: Long): List<CharacterEntity> {
        return withContext(Dispatchers.IO) {
            characterDao.getCharactersByGuildOnce(guildId)
        }
    }

    fun getLogsForCharacter(characterId: Long): Flow<List<CharacterActionLogEntity>> {
        return characterDao.getLogsForCharacter(characterId)
    }

    suspend fun insertCharacterLog(log: CharacterActionLogEntity): Long {
        return withContext(Dispatchers.IO) {
            characterDao.insertCharacterLog(log)
        }
    }

    suspend fun insertCharacter(character: CharacterEntity): Long {
        return withContext(Dispatchers.IO) {
            val id = characterDao.insertCharacter(character)
            characterDao.insertCharacterLog(
                CharacterActionLogEntity(
                    characterId = id,
                    actionType = "THINKING",
                    location = "Kota Kerajaan Fantasia",
                    title = "Karakter Didaftarkan ke Serikat Petualang",
                    decisionReason = "Memulai lembaran takdir baru sebagai ${character.characterClass.title}.",
                    narrative = "${character.name} telah resmi mengikat sumpah sebagai petualang. Dengan senjata di tangan dan tekad membara, ia siap menentukan jalannya sendiri.",
                    expGained = 0,
                    goldGained = character.gold
                )
            )
            id
        }
    }

    suspend fun updateCharacter(character: CharacterEntity) {
        withContext(Dispatchers.IO) {
            characterDao.updateCharacter(character)
        }
    }

    suspend fun deleteCharacter(character: CharacterEntity) {
        withContext(Dispatchers.IO) {
            character.avatarUri?.let { path ->
                try {
                    val file = File(path)
                    if (file.exists() && file.parentFile == context.filesDir) {
                        file.delete()
                    }
                } catch (e: Exception) {
                    // Ignore deletion error
                }
            }
            characterDao.deleteLogsForCharacter(character.id)
            characterDao.deleteCharacter(character)
        }
    }

    suspend fun updateCharacterGuild(characterId: Long, guildId: Long?) {
        withContext(Dispatchers.IO) {
            characterDao.updateCharacterGuild(characterId, guildId)
            val char = characterDao.getCharacterByIdOnce(characterId)
            val guild = guildId?.let { guildDao.getGuildByIdOnce(it) }
            if (char != null) {
                characterDao.insertCharacterLog(
                    CharacterActionLogEntity(
                        characterId = characterId,
                        actionType = "THINKING",
                        location = guild?.name ?: "Petualang Bebas",
                        title = if (guild != null) "Bergabung dengan Guild ${guild.name}" else "Keluar dari Guild (Menjadi Petualang Bebas)",
                        decisionReason = if (guild != null) "Mencari kawan seperjuangan di bawah panji ${guild.name}." else "Memilih berkelana sendiri tanpa ikatan.",
                        narrative = if (guild != null) "${char.name} disambut hangat di markas ${guild.name}. Bendera kehormatan guild kini tersemat di perisainya!" else "${char.name} memutuskan berkelana sebagai serigala penyendiri."
                    )
                )
            }
        }
    }

    suspend fun updateVitals(characterId: Long, hp: Int, mp: Int, potions: Int, manaPotions: Int? = null) {
        withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId)
            if (char != null) {
                val updatedChar = char.copy(
                    hp = hp,
                    mp = mp,
                    potions = potions,
                    manaPotions = manaPotions ?: char.manaPotions
                )
                characterDao.updateCharacter(updatedChar)
            }
        }
    }

    // Shop operations
    suspend fun insertShopItem(item: ShopItemEntity): Long {
        return withContext(Dispatchers.IO) {
            shopDao.insertShopItem(item)
        }
    }

    suspend fun deleteCustomShopItem(itemId: Long) {
        withContext(Dispatchers.IO) {
            shopDao.deleteCustomShopItemById(itemId)
        }
    }

    suspend fun buyShopItem(characterId: Long, itemId: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId) ?: return@withContext false
            val item = shopDao.getShopItemByIdOnce(itemId) ?: return@withContext false

            if (char.gold < item.price) return@withContext false

            val newGold = char.gold - item.price

            when (item.itemType) {
                "POTION_HP" -> {
                    val updatedChar = char.copy(
                        gold = newGold,
                        potions = char.potions + 1,
                        hp = (char.hp + item.bonusHp).coerceAtMost(char.maxHp)
                    )
                    characterDao.updateCharacter(updatedChar)
                    characterDao.insertCharacterLog(
                        CharacterActionLogEntity(
                            characterId = characterId,
                            actionType = "SHOP",
                            location = "Toko Alkimia Fantasia",
                            title = "Membeli Potion Darah Merah",
                            decisionReason = "Menyiapkan bekal bertahan hidup dan memulihkan stamina.",
                            narrative = "${char.name} membeli 1x Potion Darah Merah seharga ${item.price} G. Saldo tersisa $newGold G.",
                            goldGained = -item.price
                        )
                    )
                }
                "POTION_MP" -> {
                    val updatedChar = char.copy(
                        gold = newGold,
                        manaPotions = char.manaPotions + 1,
                        mp = (char.mp + item.bonusMp).coerceAtMost(char.maxMp)
                    )
                    characterDao.updateCharacter(updatedChar)
                    characterDao.insertCharacterLog(
                        CharacterActionLogEntity(
                            characterId = characterId,
                            actionType = "SHOP",
                            location = "Toko Alkimia Fantasia",
                            title = "Membeli Potion Mana Biru",
                            decisionReason = "Menyiapkan cadangan energi magis untuk merapal skill dahsyat.",
                            narrative = "${char.name} membeli 1x Potion Mana Biru seharga ${item.price} G. Saldo tersisa $newGold G.",
                            goldGained = -item.price
                        )
                    )
                }
                "POTION_ALL" -> {
                    val updatedChar = char.copy(
                        gold = newGold,
                        potions = char.potions + 1,
                        manaPotions = char.manaPotions + 1,
                        hp = (char.hp + item.bonusHp).coerceAtMost(char.maxHp),
                        mp = (char.mp + item.bonusMp).coerceAtMost(char.maxMp)
                    )
                    characterDao.updateCharacter(updatedChar)
                    characterDao.insertCharacterLog(
                        CharacterActionLogEntity(
                            characterId = characterId,
                            actionType = "SHOP",
                            location = "Toko Alkimia Fantasia",
                            title = "Membeli Elixir Pemulih Agung",
                            decisionReason = "Membeli ramuan legendaris untuk memulihkan seluruh daya fisik dan batin.",
                            narrative = "${char.name} membeli Elixir Pemulih Agung seharga ${item.price} G. Saldo tersisa $newGold G.",
                            goldGained = -item.price
                        )
                    )
                }
                "WEAPON" -> {
                    val updatedChar = char.copy(
                        gold = newGold,
                        equippedWeaponName = item.name,
                        equippedWeaponIcon = item.iconEmoji,
                        equippedWeaponStats = item.statsSummary,
                        atk = char.atk + item.bonusAtk,
                        def = char.def + item.bonusDef,
                        maxHp = char.maxHp + item.bonusHp,
                        hp = char.hp + item.bonusHp,
                        maxMp = char.maxMp + item.bonusMp,
                        mp = char.mp + item.bonusMp,
                        spd = char.spd + item.bonusSpd,
                        crit = (char.crit + item.bonusCrit).coerceAtMost(60)
                    )
                    characterDao.updateCharacter(updatedChar)
                    characterDao.insertCharacterLog(
                        CharacterActionLogEntity(
                            characterId = characterId,
                            actionType = "SHOP",
                            location = "Pandai Besi Toko Senjata",
                            title = "Membeli & Melengkapi ${item.name}",
                            decisionReason = "Senjata ini meningkatkan kekuatan tempur secara signifikan (${item.statsSummary}).",
                            narrative = "${char.name} membeli dan melengkapi ${item.iconEmoji} ${item.name} seharga ${item.price} G!\nKekuatan tempur meningkat: ${item.statsSummary}. Saldo tersisa $newGold G.",
                            goldGained = -item.price
                        )
                    )
                }
            }
            true
        }
    }

    suspend fun simulateAutonomousShopping(characterId: Long): CharacterActionLogEntity? {
        return withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId) ?: return@withContext null
            val allItems = shopDao.getAllShopItemsOnce()

            if (char.potions < 2 && char.gold >= 35) {
                buyShopItem(characterId, allItems.firstOrNull { it.itemType == "POTION_HP" }?.id ?: 1L)
                val log = CharacterActionLogEntity(
                    characterId = characterId,
                    actionType = "SHOP",
                    location = "Toko Petualang",
                    title = "Pikiran Mandiri: Membeli Potion Darah",
                    decisionReason = "Menyadari sisa ramuan HP menipis (${char.potions} tersisa) dan memiliki tabungan ${char.gold} G. Mengutamakan keselamatan nyawa sebelum berburu.",
                    narrative = "${char.name} melangkah ke toko dan membeli Potion Darah Merah untuk bekal perjalanan berikutnya."
                )
                characterDao.insertCharacterLog(log)
                return@withContext log
            }

            if (char.characterClass == CharacterClass.MAGE && char.manaPotions < 2 && char.gold >= 35) {
                allItems.firstOrNull { it.itemType == "POTION_MP" }?.let { mpPotion ->
                    buyShopItem(characterId, mpPotion.id)
                    val log = CharacterActionLogEntity(
                        characterId = characterId,
                        actionType = "SHOP",
                        location = "Toko Petualang",
                        title = "Pikiran Mandiri: Membeli Potion Mana",
                        decisionReason = "Sebagai penyihir, mantra mematikan membutuhkan banyak mana. Memutuskan membeli cadangan potion mana.",
                        narrative = "${char.name} membeli Potion Mana Biru di toko alkimia untuk menjaga kelancaran rapalan sihirnya."
                    )
                    characterDao.insertCharacterLog(log)
                    return@withContext log
                }
            }

            val affordableWeapons = allItems.filter { item ->
                item.itemType == "WEAPON" &&
                (item.compatibleClass == null || item.compatibleClass == char.characterClass) &&
                item.price <= char.gold &&
                item.name != char.equippedWeaponName
            }.sortedByDescending { it.totalPowerBonus }

            if (affordableWeapons.isNotEmpty()) {
                val chosenWeapon = affordableWeapons.first()
                buyShopItem(characterId, chosenWeapon.id)

                val thoughtReason = "Memiliki tabungan ${char.gold} G. Terpikat oleh ${chosenWeapon.iconEmoji} ${chosenWeapon.name} di etalase toko karena memberikan tambahan kekuatan tempur (${chosenWeapon.statsSummary})."
                val narrative = "${char.name} memutuskan membelanjakan ${chosenWeapon.price} G untuk membeli ${chosenWeapon.name}.\nLangsung menyarungkan senjata barunya di pinggang dengan penuh rasa percaya diri!"

                val log = CharacterActionLogEntity(
                    characterId = characterId,
                    actionType = "SHOP",
                    location = "Pandai Besi Toko Senjata",
                    title = "Pikiran Mandiri: Membeli Senjata ${chosenWeapon.name}",
                    decisionReason = thoughtReason,
                    narrative = narrative,
                    goldGained = -chosenWeapon.price
                )
                characterDao.insertCharacterLog(log)
                return@withContext log
            }

            val log = CharacterActionLogEntity(
                characterId = characterId,
                actionType = "THINKING",
                location = "Toko Petualang",
                title = "Meninjau Etalase Toko",
                decisionReason = "Melihat-lihat persediaan senjata dan potion di toko dengan saldo ${char.gold} G.",
                narrative = "${char.name} meneliti deretan senjata di toko. Belum menemukan senjata yang lebih memikat yang dapat dijangkau oleh kantongnya, ia memutuskan menyimpan koin emasnya untuk berburu lebih banyak lagi."
            )
            characterDao.insertCharacterLog(log)
            return@withContext log
        }
    }

    suspend fun addExpAndCheckLevelUp(characterId: Long, expGained: Int, goldGained: Int = 0): AutoLevelUpResult? {
        return withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId) ?: return@withContext null
            val oldLevel = char.level
            val totalExp = char.exp + expGained
            val expNeeded = oldLevel * 100

            var currentLevel = oldLevel
            var remainingExp = totalExp
            var isLevelUp = false

            while (remainingExp >= (currentLevel * 100)) {
                remainingExp -= (currentLevel * 100)
                currentLevel += 1
                isLevelUp = true
            }

            val newGold = char.gold + goldGained

            val updatedChar = if (isLevelUp) {
                val levelsGained = currentLevel - oldLevel
                char.copy(
                    level = currentLevel,
                    exp = remainingExp,
                    gold = newGold,
                    maxHp = char.maxHp + (25 * levelsGained),
                    hp = char.maxHp + (25 * levelsGained),
                    maxMp = char.maxMp + (15 * levelsGained),
                    mp = char.maxMp + (15 * levelsGained),
                    atk = char.atk + (6 * levelsGained),
                    def = char.def + (4 * levelsGained),
                    spd = char.spd + (2 * levelsGained),
                    crit = (char.crit + (1 * levelsGained)).coerceAtMost(60)
                )
            } else {
                char.copy(
                    exp = remainingExp,
                    gold = newGold
                )
            }

            characterDao.updateCharacter(updatedChar)

            if (isLevelUp) {
                characterDao.insertCharacterLog(
                    CharacterActionLogEntity(
                        characterId = characterId,
                        actionType = "LEVEL_UP",
                        location = "Di Medan Tempur",
                        title = "🎉 OTOMATIS NAIK KE LEVEL $currentLevel!",
                        decisionReason = "Mengasah pengalaman tempur dari setiap tebasan dan serangan.",
                        narrative = "Tubuh ${char.name} diselimuti cahaya keemasan! Kekuatan laten terbangun: Max HP +${25 * (currentLevel - oldLevel)}, Max MP +${15 * (currentLevel - oldLevel)}, ATK +${6 * (currentLevel - oldLevel)}, DEF +${4 * (currentLevel - oldLevel)}. Seluruh HP & MP telah pulih sepenuhnya!",
                        expGained = expGained,
                        goldGained = goldGained,
                        isLevelUp = true
                    )
                )
            }

            AutoLevelUpResult(
                isLevelUp = isLevelUp,
                oldLevel = oldLevel,
                newLevel = currentLevel,
                updatedCharacter = updatedChar
            )
        }
    }

    suspend fun recordVictory(characterId: Long, expGained: Int, currentExp: Int, currentLevel: Int, goldGained: Int) {
        addExpAndCheckLevelUp(characterId, expGained, goldGained)
        withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId)
            if (char != null) {
                characterDao.updateCharacter(char.copy(wins = char.wins + 1))
            }
        }
    }

    /**
     * AI Autonomous Event Decision:
     * Karakter melihat monster event yang di-spawn,
     * berpikir dan menimbang apakah mau menantang secara SOLO atau RAID bersama guild,
     * lalu mengeksekusi aksinya dan mencatat log hasil tempur serta menjatuhkan drop yang ditentukan!
     */
    suspend fun simulateAutonomousEventDecision(characterId: Long, monsterId: Long): CharacterActionLogEntity? {
        return withContext(Dispatchers.IO) {
            val char = characterDao.getCharacterByIdOnce(characterId) ?: return@withContext null
            val monster = eventMonsterDao.getEventMonsterByIdOnce(monsterId) ?: return@withContext null
            val guild = char.guildId?.let { guildDao.getGuildByIdOnce(it) }

            // Menimbang: SOLO vs RAID
            // Jika combatPower tinggi dibanding monster (atau level tinggi) -> SOLO
            // Jika monster HP > combatPower * 4 dan karakter punya Guild -> RAID
            val canSolo = (char.combatPower * 3.5 >= monster.maxHp) || (char.level >= 4 && Random.nextBoolean())
            val choosesSolo = canSolo || guild == null

            if (choosesSolo) {
                // Eksekusi SOLO DUEL melawan Event Monster
                val thoughtReason = if (canSolo) {
                    "Melihat ${monster.iconEmoji} ${monster.name} (${monster.maxHp} HP). Dengan Combat Power ${char.combatPower}, pahlawan ini yakin mampu menumbangkannya secara SOLO untuk membuktikan kehebatannya dan mengklaim seluruh drop!"
                } else {
                    "Meskipun berbahaya, darah petualang membara! Memilih bertarung secara SOLO sendirian tanpa bantuan siapa pun demi memburu drop langka ${monster.itemDropName ?: "Pusaka"}!"
                }

                // Kalkulasi damage solo
                val playerDmg = (char.combatPower * 2.8).toInt() + Random.nextInt(50, 150)
                val monsterCounterDmg = max(15, monster.atk - (char.def / 2))
                val newCharHp = max(15, char.hp - monsterCounterDmg)
                characterDao.updateVitals(characterId, newCharHp, char.mp, char.potions)

                // Event monster defeated!
                eventMonsterDao.markMonsterDefeated(monsterId)

                // Drop reward: EXP, Gold, and Custom Weapon/Item
                val expReward = monster.expDrop
                val goldReward = monster.goldDrop

                // If monster drops an item/weapon, register it into shop or character's inventory
                val dropItemText = if (!monster.itemDropName.isNullOrBlank()) {
                    // Also automatically inject the dropped weapon into shop or equip it!
                    shopDao.insertShopItem(
                        ShopItemEntity(
                            name = monster.itemDropName,
                            itemType = "WEAPON",
                            compatibleClass = null,
                            iconEmoji = "🎁",
                            price = 10,
                            bonusAtk = 35,
                            bonusDef = 15,
                            bonusHp = 80,
                            description = "Drop pusaka langka yang dijatuhkan dari ${monster.name}!",
                            isFixed = false
                        )
                    )
                    "\n🎁 DROP SPESIAL DIPEROLEH: Berhasil mendapatkan \"${monster.itemDropName}\"! Pusaka ini kini telah masuk ke persediaan!"
                } else ""

                val levelUpResult = addExpAndCheckLevelUp(characterId, expReward, goldReward)
                val isLevelUp = levelUpResult?.isLevelUp ?: false
                val newLvl = levelUpResult?.newLevel ?: char.level

                val narrative = buildString {
                    append("${char.name} melangkah maju seorang diri menantang ${monster.iconEmoji} ${monster.name}!\n")
                    append("Menerjang dengan serangan pamungkas dan skill ${char.characterClass.skillName}, memberikan $playerDmg DMG!\n")
                    append("Monster membalas dengan ${monster.skillName} (-$monsterCounterDmg HP), tetapi ${char.name} berhasil menghabisinya dengan tebasan terakhir!\n")
                    append("🏆 KEMENANGAN SOLO EVENT! Mendapatkan +$expReward EXP & +$goldReward G.$dropItemText")
                    if (isLevelUp) {
                        append("\n✨ Pertarungan solo legendaris ini memicu AUTO LEVEL UP ke Level $newLvl!")
                    }
                }

                val log = CharacterActionLogEntity(
                    characterId = characterId,
                    actionType = "EVENT_SOLO",
                    location = "Arena Event Boss",
                    title = "Menantang Solo & Mengalahkan ${monster.name}",
                    decisionReason = thoughtReason,
                    narrative = narrative,
                    expGained = expReward,
                    goldGained = goldReward,
                    hpChange = -monsterCounterDmg,
                    isLevelUp = isLevelUp
                )
                characterDao.insertCharacterLog(log)
                return@withContext log
            } else {
                // Eksekusi GUILD RAID melawan Event Monster
                val thoughtReason = "Monster event ${monster.iconEmoji} ${monster.name} memiliki ketahanan ${monster.maxHp} HP dan serangan mematikan ${monster.atk} ATK. Melawannya sendirian terlalu berisiko. Memutuskan memanggil seluruh kawan Guild ${guild?.name ?: "Aliansi"} untuk melancarkan serangan RAID gabungan!"

                val guildMembers = characterDao.getCharactersByGuildOnce(guild!!.id)
                val totalRaidDamage = guildMembers.sumOf { it.combatPower * 2 } + 300
                eventMonsterDao.markMonsterDefeated(monsterId)
                guildDao.addRaidPoints(guild.id, 80)

                val expReward = monster.expDrop
                val goldReward = monster.goldDrop

                val dropItemText = if (!monster.itemDropName.isNullOrBlank()) {
                    shopDao.insertShopItem(
                        ShopItemEntity(
                            name = monster.itemDropName,
                            itemType = "WEAPON",
                            compatibleClass = null,
                            iconEmoji = "🎁",
                            price = 10,
                            bonusAtk = 35,
                            bonusDef = 15,
                            bonusHp = 80,
                            description = "Drop pusaka rampasan raid dari ${monster.name}!",
                            isFixed = false
                        )
                    )
                    "\n🎁 DROP RAID DIPEROLEH: Guild mengamankan \"${monster.itemDropName}\"!"
                } else ""

                val levelUpResult = addExpAndCheckLevelUp(characterId, expReward, goldReward)
                val isLevelUp = levelUpResult?.isLevelUp ?: false
                val newLvl = levelUpResult?.newLevel ?: char.level

                val narrative = buildString {
                    append("${char.name} meniup terompet perang, memimpin barisan ${guild.name} melawan ${monster.name}!\n")
                    append("Seluruh ${guildMembers.size} pahlawan guild melancarkan jurus serentak menghasilkan total $totalRaidDamage DMG!\n")
                    append("${monster.name} akhirnya tumbang di hadapan kerja sama guild yang solid!\n")
                    append("🏆 KEMENANGAN RAID GUILD! (+80 Poin Raid Guild). Setiap pahlawan memperoleh +$expReward EXP & +$goldReward G.$dropItemText")
                    if (isLevelUp) {
                        append("\n✨ Kemenangan raid ini memicu AUTO LEVEL UP ke Level $newLvl!")
                    }
                }

                val log = CharacterActionLogEntity(
                    characterId = characterId,
                    actionType = "EVENT_RAID",
                    location = "Medan Tempur Raid Event",
                    title = "Memimpin Serbuan Raid Guild Melawan ${monster.name}",
                    decisionReason = thoughtReason,
                    narrative = narrative,
                    expGained = expReward,
                    goldGained = goldReward,
                    isLevelUp = isLevelUp
                )
                characterDao.insertCharacterLog(log)
                return@withContext log
            }
        }
    }

    suspend fun simulateAutonomousDecision(characterId: Long): CharacterActionLogEntity? {
        return withContext(Dispatchers.IO) {
            val character = characterDao.getCharacterByIdOnce(characterId) ?: return@withContext null
            val guild = character.guildId?.let { guildDao.getGuildByIdOnce(it) }

            // 1. Cek apakah ada Monster Event aktif yang belum kalah
            val activeMonsters = eventMonsterDao.getActiveEventMonstersOnce()
            val hpPercent = (character.hp.toFloat() / character.maxHp) * 100

            // Jika ada event monster aktif dan karakter fit (>45% HP), coba tantang event monster (Solo atau Raid)!
            if (activeMonsters.isNotEmpty() && hpPercent >= 45f && Random.nextInt(100) < 40) {
                val targetEventMonster = activeMonsters.random()
                val eventLog = simulateAutonomousEventDecision(characterId, targetEventMonster.id)
                if (eventLog != null) return@withContext eventLog
            }

            if (hpPercent < 30f) {
                if (character.gold >= 35 && character.potions == 0) {
                    return@withContext simulateAutonomousShopping(characterId)
                }
                val healAmount = (character.maxHp * 0.6).toInt()
                val newHp = (character.hp + healAmount).coerceAtMost(character.maxHp)
                val newPotions = max(0, character.potions - (if (character.potions > 0) 1 else 0))
                characterDao.updateVitals(characterId, newHp, character.maxMp, newPotions)

                val log = CharacterActionLogEntity(
                    characterId = characterId,
                    actionType = "REST",
                    location = "Tavern Peristirahatan Petualang",
                    title = "Memutuskan Beristirahat & Memulihkan Stamina",
                    decisionReason = "Darah tersisa $hpPercent%. Terlalu berisiko melanjutkan perburuan dalam keadaan terluka.",
                    narrative = "${character.name} memutuskan menepi dari medan laga. Duduk di dekat perapian hangat, meminum ramuan herbal, dan membalut luka tempur. Berhasil memulihkan +$healAmount HP!",
                    hpChange = healAmount
                )
                characterDao.insertCharacterLog(log)
                return@withContext log
            }

            if (character.gold >= 120 && Random.nextInt(100) < 35) {
                val shopResult = simulateAutonomousShopping(characterId)
                if (shopResult != null) return@withContext shopResult
            }

            val zones = getDungeonZones()
            val chosenZone = when {
                character.level <= 1 -> zones[0]
                character.level in 2..3 -> if (Random.nextBoolean()) zones[0] else zones[1]
                else -> if (Random.nextBoolean()) zones[1] else zones[2]
            }

            val monster = chosenZone.possibleEnemies.random()
            val expGain = monster.expReward + Random.nextInt(10, 25)
            val goldGain = monster.goldReward + Random.nextInt(15, 35)
            val selfDamageTaken = max(5, monster.atk - (character.def / 2))
            val newHp = max(10, character.hp - selfDamageTaken)

            characterDao.updateVitals(characterId, newHp, character.mp, character.potions)

            val levelUpResult = addExpAndCheckLevelUp(characterId, expGain, goldGain)
            val isLevelUp = levelUpResult?.isLevelUp ?: false
            val newLvl = levelUpResult?.newLevel ?: character.level

            val thoughtReason = when (character.characterClass) {
                CharacterClass.WARRIOR -> "Mencari lawan sepadan di ${chosenZone.name} untuk mengasah ketahanan perisai dan ketangguhan otot fisiknya."
                CharacterClass.MAGE -> "Merasakan resonansi sihir misterius di ${chosenZone.name} dan berniat menguji daya ledak mantra barunya."
                CharacterClass.ARCHER -> "Mengamati jejak monster di ${chosenZone.name} dari kejauhan dan mengincar sudut tembak terbaik untuk panahnya."
                CharacterClass.ASSASSIN -> "Melihat kesempatan berburu cepat di balik kegelapan ${chosenZone.name} untuk menyergap titik vital mangsa."
            }

            val narrativeText = buildString {
                append("${character.name} berkelana sendirian menuju ${chosenZone.name}.\n")
                append("Di tengah jalan, muncul ${monster.iconEmoji} ${monster.name} (\"${monster.flavorText}\")!\n")
                append("Tanpa ragu, ${character.name} menerjang dengan ${character.characterClass.skillName}, mengalahkan monster setelah pertarungan sengit (-$selfDamageTaken HP).\n")
                append("Hasil buruan: +$expGain EXP & +$goldGain Koin Emas.")
                if (isLevelUp) {
                    append("\n⭐ Pertarungan ini memicu potensi puncak! ${character.name} OTOMATIS NAIK KE LEVEL $newLvl!")
                }
            }

            val log = CharacterActionLogEntity(
                characterId = characterId,
                actionType = "HUNT",
                location = chosenZone.name,
                title = "Berburu Mandiri: Menaklukkan ${monster.name}",
                decisionReason = thoughtReason,
                narrative = narrativeText,
                expGained = expGain,
                goldGained = goldGain,
                hpChange = -selfDamageTaken,
                isLevelUp = isLevelUp
            )

            characterDao.insertCharacterLog(log)
            return@withContext log
        }
    }

    suspend fun insertGuild(guild: GuildEntity): Long {
        return withContext(Dispatchers.IO) {
            guildDao.insertGuild(guild)
        }
    }

    suspend fun updateGuild(guild: GuildEntity) {
        withContext(Dispatchers.IO) {
            guildDao.updateGuild(guild)
        }
    }

    suspend fun deleteGuild(guild: GuildEntity) {
        withContext(Dispatchers.IO) {
            guildDao.deleteGuild(guild)
        }
    }

    suspend fun addGuildRaidPoints(guildId: Long, points: Int) {
        withContext(Dispatchers.IO) {
            guildDao.addRaidPoints(guildId, points)
        }
    }

    suspend fun saveImageToInternalStorage(sourceUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
                val fileName = "avatar_${System.currentTimeMillis()}.jpg"
                val destFile = File(context.filesDir, fileName)
                FileOutputStream(destFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                destFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    fun getDungeonZones(): List<DungeonZone> {
        return listOf(
            DungeonZone(
                id = "forest",
                name = "🌲 Hutan Goblin Lumut",
                levelReq = 1,
                description = "Hutan rimbun yang dipenuhi suku goblin nakal dan serigala hutan lapar.",
                dangerLevel = "Mudah",
                possibleEnemies = listOf(
                    MonsterTemplate(
                        name = "Goblin Pengintai",
                        hp = 120,
                        atk = 24,
                        def = 10,
                        expReward = 35,
                        goldReward = 45,
                        iconEmoji = "👺",
                        flavorText = "Goblin kecil lincah dengan pisau belati berkarat.",
                        specialAttackName = "Lemparan Batu Liar"
                    ),
                    MonsterTemplate(
                        name = "Serigala Hutan Buas",
                        hp = 140,
                        atk = 30,
                        def = 12,
                        expReward = 45,
                        goldReward = 55,
                        iconEmoji = "🐺",
                        flavorText = "Pemangsa malam bertaring tajam yang menerkam tanpa suara.",
                        specialAttackName = "Cakaran Berdarah"
                    ),
                    MonsterTemplate(
                        name = "Kepala Suku Goblin",
                        hp = 220,
                        atk = 38,
                        def = 18,
                        expReward = 75,
                        goldReward = 110,
                        iconEmoji = "👹",
                        flavorText = "Pemimpin gerombolan yang mengayunkan gada berduri raksasa.",
                        specialAttackName = "Gada Penghancur Tulang"
                    )
                )
            ),
            DungeonZone(
                id = "ruins",
                name = "🏛️ Reruntuhan Kuil Kuno",
                levelReq = 2,
                description = "Kuil peradaban kuno yang runtuh, kini dihantui ksatria tengkorak dan roh penyihir terkutuk.",
                dangerLevel = "Menengah",
                possibleEnemies = listOf(
                    MonsterTemplate(
                        name = "Ksatria Tengkorak Abadi",
                        hp = 240,
                        atk = 42,
                        def = 28,
                        expReward = 85,
                        goldReward = 120,
                        iconEmoji = "💀",
                        flavorText = "Prajurit kerangka berbaju zirah kuno yang tak mengenal rasa takut.",
                        specialAttackName = "Tebasan Kutukan Jiwa"
                    ),
                    MonsterTemplate(
                        name = "Penyihir Roh Hitam",
                        hp = 190,
                        atk = 55,
                        def = 15,
                        expReward = 95,
                        goldReward = 140,
                        iconEmoji = "🧙‍♂️",
                        flavorText = "Roh penasaran yang merapal mantra kegelapan kuno.",
                        specialAttackName = "Sinar Kematian Abyss"
                    ),
                    MonsterTemplate(
                        name = "Golem Penjaga Kuil",
                        hp = 340,
                        atk = 46,
                        def = 38,
                        expReward = 130,
                        goldReward = 180,
                        iconEmoji = "🗿",
                        flavorText = "Raksasa batu terukir rune kuno yang terbangun dari tidur seribu tahun.",
                        specialAttackName = "Hantaman Gempa Bumi"
                    )
                )
            ),
            DungeonZone(
                id = "volcano",
                name = "🌋 Gua Lahar Naga Kuno",
                levelReq = 3,
                description = "Kawah gunung berapi tersembunyi yang menjadi sarang naga magma pemusnah peradaban.",
                dangerLevel = "Sangat Bahaya",
                possibleEnemies = listOf(
                    MonsterTemplate(
                        name = "Salamander Api Membara",
                        hp = 300,
                        atk = 55,
                        def = 26,
                        expReward = 135,
                        goldReward = 190,
                        iconEmoji = "🦎",
                        flavorText = "Reptil lahar yang mampu menyemburkan semburan api membakar.",
                        specialAttackName = "Semburan Magma Panas"
                    ),
                    MonsterTemplate(
                        name = "Iblis Pembakar Jiwa",
                        hp = 360,
                        atk = 65,
                        def = 30,
                        expReward = 170,
                        goldReward = 230,
                        iconEmoji = "🔥",
                        flavorText = "Sosok bersayap api dari kedalaman neraka bawah tanah.",
                        specialAttackName = "Badai Neraka Menyala"
                    ),
                    MonsterTemplate(
                        name = "Naga Merah Kuno Ignis",
                        hp = 550,
                        atk = 80,
                        def = 45,
                        expReward = 300,
                        goldReward = 450,
                        iconEmoji = "🐲",
                        flavorText = "Penguasa langit api dengan nafas kiamat yang membakar daratan.",
                        specialAttackName = "Nafas Kiamat Bintang Jatuh"
                    )
                )
            )
        )
    }

    fun getRaidBoss(): RaidBoss {
        return RaidBoss(
            name = "Raja Naga Kegelapan Malakor",
            title = "Penguasa Kegelapan Abadi",
            totalHp = 3500,
            currentHp = 3500,
            atk = 110,
            def = 55,
            iconEmoji = "🐉",
            description = "Naga purba bertanduk ganda yang mengancam akan menenggelamkan seluruh guild ke dalam jurang kehancuran. Dibutuhkan kerja sama seluruh pahlawan guild untuk menaklukkannya!"
        )
    }
}
