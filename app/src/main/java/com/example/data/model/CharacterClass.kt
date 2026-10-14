package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.AssassinPurple
import com.example.ui.theme.MageBlue
import com.example.ui.theme.WarriorRed

enum class CharacterClass(
    val title: String,
    val role: String,
    val description: String,
    val defaultHp: Int,
    val defaultMp: Int,
    val defaultAtk: Int,
    val defaultDef: Int,
    val defaultSpd: Int,
    val defaultCrit: Int,
    val skillName: String,
    val skillDescription: String,
    val skillMpCost: Int,
    val iconEmoji: String
) {
    WARRIOR(
        title = "Warrior",
        role = "Ksatria Garda Depan",
        description = "Pertahanan baja dan ketahanan fisik tangguh. Mengandalkan pedang besar dan perisai berat.",
        defaultHp = 350,
        defaultMp = 60,
        defaultAtk = 48,
        defaultDef = 35,
        defaultSpd = 22,
        defaultCrit = 12,
        skillName = "Tebasan Badai Baja",
        skillDescription = "Menyerang dengan kekuatan penuh dan meningkatkan pertahanan 50% selama 1 giliran.",
        skillMpCost = 25,
        iconEmoji = "🛡️"
    ),
    MAGE(
        title = "Mage",
        role = "Penyihir Elemen Misterius",
        description = "Ahli sihir kehancuran berjarak jauh. Mana melimpah dan serangan magis berdaya ledak dahsyat.",
        defaultHp = 220,
        defaultMp = 200,
        defaultAtk = 65,
        defaultDef = 16,
        defaultSpd = 26,
        defaultCrit = 18,
        skillName = "Ledakan Meteor Neraka",
        skillDescription = "Memanggil kobaran api kosmik yang menghasilkan damage sihir masif tak tertangkis.",
        skillMpCost = 45,
        iconEmoji = "🔮"
    ),
    ARCHER(
        title = "Archer",
        role = "Pemanah Ulung Hening",
        description = "Kecepatan dan akurasi tinggi. Mampu menembak titik vital musuh dari kejauhan dengan presisi mematikan.",
        defaultHp = 260,
        defaultMp = 90,
        defaultAtk = 52,
        defaultDef = 22,
        defaultSpd = 45,
        defaultCrit = 32,
        skillName = "Panah Badai Presisi",
        skillDescription = "Tembakan cepat beruntun dengan peluang kritikal 80% dan menembus pertahanan lawan.",
        skillMpCost = 30,
        iconEmoji = "🏹"
    ),
    ASSASSIN(
        title = "Assassin",
        role = "Pembunuh Bayangan Gelap",
        description = "Bergerak tanpa suara di balik kegelapan. Serangan tiba-tiba dengan serangan kritikal paling mematikan.",
        defaultHp = 240,
        defaultMp = 80,
        defaultAtk = 68,
        defaultDef = 18,
        defaultSpd = 52,
        defaultCrit = 40,
        skillName = "Tikaman Belati Beracun",
        skillDescription = "Menyelinap ke belakang musuh dan menusuk titik lemah dengan racun mematikan.",
        skillMpCost = 35,
        iconEmoji = "🗡️"
    );

    val themeColor: Color
        get() = when (this) {
            WARRIOR -> WarriorRed
            MAGE -> MageBlue
            ARCHER -> ArcherGreen
            ASSASSIN -> AssassinPurple
        }
}
