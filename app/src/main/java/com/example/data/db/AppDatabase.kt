package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CharacterActionLogEntity
import com.example.data.model.CharacterClass
import com.example.data.model.CharacterEntity
import com.example.data.model.CustomSkillEntity
import com.example.data.model.EventMonsterEntity
import com.example.data.model.GuildEntity
import com.example.data.model.ShopItemEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CharacterEntity::class,
        GuildEntity::class,
        CharacterActionLogEntity::class,
        ShopItemEntity::class,
        CustomSkillEntity::class,
        EventMonsterEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun guildDao(): GuildDao
    abstract fun shopDao(): ShopDao
    abstract fun customSkillDao(): CustomSkillDao
    abstract fun eventMonsterDao(): EventMonsterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "text_rpg_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }
        }

        private suspend fun populateInitialData(database: AppDatabase) {
            val guildDao = database.guildDao()
            val characterDao = database.characterDao()
            val shopDao = database.shopDao()
            val customSkillDao = database.customSkillDao()
            val eventMonsterDao = database.eventMonsterDao()

            // 1. Prepopulate Fixed Potions
            shopDao.insertShopItem(
                ShopItemEntity(
                    name = "Potion Darah Merah",
                    itemType = "POTION_HP",
                    compatibleClass = null,
                    iconEmoji = "🧪",
                    price = 35,
                    bonusHp = 150,
                    description = "Ramuan penyembuh luka fisik. Memulihkan +150 HP seketika saat bertualang.",
                    isFixed = true
                )
            )

            shopDao.insertShopItem(
                ShopItemEntity(
                    name = "Potion Mana Biru",
                    itemType = "POTION_MP",
                    compatibleClass = null,
                    iconEmoji = "💧",
                    price = 35,
                    bonusMp = 100,
                    description = "Cairan kristal biru penghubung energi kosmik. Memulihkan +100 MP untuk merapal skill.",
                    isFixed = true
                )
            )

            shopDao.insertShopItem(
                ShopItemEntity(
                    name = "Elixir Pemulih Agung",
                    itemType = "POTION_ALL",
                    compatibleClass = null,
                    iconEmoji = "✨",
                    price = 65,
                    bonusHp = 200,
                    bonusMp = 120,
                    description = "Ramuan alkimia legendaris yang memulihkan darah (HP) dan mana (MP) sekaligus.",
                    isFixed = true
                )
            )

            // 2. Starter Custom Weapons in Shop
            shopDao.insertShopItem(
                ShopItemEntity(
                    name = "Pedang Baja Penjaga Naga",
                    itemType = "WEAPON",
                    compatibleClass = CharacterClass.WARRIOR,
                    iconEmoji = "⚔️",
                    price = 150,
                    bonusAtk = 24,
                    bonusDef = 14,
                    bonusHp = 60,
                    description = "Pedang berat berlapis baja naga yang meningkatkan daya tebas dan pertahanan.",
                    isFixed = false
                )
            )

            shopDao.insertShopItem(
                ShopItemEntity(
                    name = "Tongkat Kristal Kosmik",
                    itemType = "WEAPON",
                    compatibleClass = CharacterClass.MAGE,
                    iconEmoji = "🔮",
                    price = 175,
                    bonusAtk = 32,
                    bonusMp = 70,
                    bonusCrit = 8,
                    description = "Tongkat sihir yang menyimpan kekuatan bintang jatuh, meledakkan damage mantra.",
                    isFixed = false
                )
            )

            // 3. Initial Guilds
            val guild1Id = guildDao.insertGuild(
                GuildEntity(
                    name = "Legiun Garuda Emas",
                    motto = "Kehormatan di Ujung Pedang, Cahaya bagi yang Lemah!",
                    description = "Guild ksatria tertua di benua Fantasia. Dikenal karena disiplin baja dan perlindungan terhadap desa-desa perbatasan.",
                    guildType = "Ksatria Garda Depan",
                    emblemColorHex = 0xFFFFB703,
                    emblemIcon = "🛡️",
                    level = 3,
                    raidPoints = 120
                )
            )

            val guild2Id = guildDao.insertGuild(
                GuildEntity(
                    name = "Kultus Bayangan Abyss",
                    motto = "Sunyi di Malam, Mematikan dalam Kegelapan.",
                    description = "Perkumpulan rahasia para pembunuh bayaran dan penyihir terlarang yang menguasai seni kegelapan dan spionase.",
                    guildType = "Sindikat Bayangan",
                    emblemColorHex = 0xFF9D4EDD,
                    emblemIcon = "🗡️",
                    level = 2,
                    raidPoints = 85
                )
            )

            // 4. Starter Characters
            val valenId = characterDao.insertCharacter(
                CharacterEntity(
                    name = "Valen sang Penjaga",
                    characterClass = CharacterClass.WARRIOR,
                    avatarUri = null,
                    hp = 380,
                    maxHp = 380,
                    mp = 70,
                    maxMp = 70,
                    atk = 52,
                    def = 40,
                    spd = 22,
                    crit = 15,
                    bio = "Veteran perang yang bersumpah mengabdikan pedangnya untuk membasmi iblis dan menjaga kedamaian rakyat kecil.",
                    guildId = guild1Id,
                    level = 2,
                    exp = 40,
                    gold = 250,
                    potions = 4,
                    manaPotions = 2,
                    equippedWeaponName = "Pedang Besi Pelindung",
                    equippedWeaponIcon = "⚔️",
                    equippedWeaponStats = "ATK +12 • DEF +8",
                    wins = 3
                )
            )

            val sylviaId = characterDao.insertCharacter(
                CharacterEntity(
                    name = "Sylvia Angin Malam",
                    characterClass = CharacterClass.ASSASSIN,
                    avatarUri = null,
                    hp = 250,
                    maxHp = 250,
                    mp = 85,
                    maxMp = 85,
                    atk = 72,
                    def = 20,
                    spd = 55,
                    crit = 45,
                    bio = "Gadis pengembara bertopeng yang gerakannya secepat bayangan kilat. Tidak pernah meleset dalam menyelesaikan kontrak perburuan.",
                    guildId = guild2Id,
                    level = 1,
                    exp = 10,
                    gold = 180,
                    potions = 3,
                    manaPotions = 3,
                    equippedWeaponName = "Belati Pengintai Malam",
                    equippedWeaponIcon = "🗡️",
                    equippedWeaponStats = "ATK +14 • CRIT +8%",
                    wins = 1
                )
            )

            // 5. Prepopulate Starter Custom Skills
            val skill1Id = customSkillDao.insertSkill(
                CustomSkillEntity(
                    name = "Tebasan Halilintar Purba",
                    skillType = "ATTACK",
                    mpCost = 35,
                    powerMultiplier = 2.8f,
                    iconEmoji = "⚡",
                    description = "Mengalirkan kilat petir murni ke ujung senjata dan menyengat lawan dengan tegangan tinggi.",
                    assignedCharacterId = valenId,
                    isMonsterSkill = false
                )
            )

            customSkillDao.insertSkill(
                CustomSkillEntity(
                    name = "Tusukan Kabut Beracun",
                    skillType = "ATTACK",
                    mpCost = 30,
                    powerMultiplier = 3.0f,
                    iconEmoji = "🩸",
                    description = "Menyelimuti diri dalam kabut hitam dan menusuk titik saraf lawan.",
                    assignedCharacterId = sylviaId,
                    isMonsterSkill = false
                )
            )

            customSkillDao.insertSkill(
                CustomSkillEntity(
                    name = "Semburan Gelombang Magma",
                    skillType = "ATTACK",
                    mpCost = 40,
                    powerMultiplier = 2.4f,
                    iconEmoji = "🔥",
                    description = "Skill khusus monster untuk memuntahkan lahar panas membakar daratan.",
                    assignedCharacterId = null,
                    isMonsterSkill = true
                )
            )

            // 6. Prepopulate Starter Event Monster
            eventMonsterDao.insertEventMonster(
                EventMonsterEntity(
                    name = "Leviathan Samudra Hitam",
                    title = "Monster Event: Penguasa Palung Abyss",
                    iconEmoji = "🐉",
                    hp = 1500,
                    maxHp = 1500,
                    atk = 75,
                    def = 35,
                    spd = 30,
                    skillName = "Tsunami Pemecah Karang",
                    skillDescription = "Membangkitkan ombak hitam pekat yang menghantam seluruh medan laga.",
                    skillMultiplier = 2.0f,
                    expDrop = 280,
                    goldDrop = 380,
                    itemDropName = "Trisula Emas Samudra",
                    isDefeated = false
                )
            )
        }
    }
}
