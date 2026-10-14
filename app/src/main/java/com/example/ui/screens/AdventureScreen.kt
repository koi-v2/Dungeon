package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.ActiveCombat
import com.example.data.model.BattleLogType
import com.example.data.model.CharacterEntity
import com.example.data.model.DungeonZone
import com.example.ui.RpgViewModel
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.MageBlue
import com.example.ui.theme.MpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

@Composable
fun AdventureScreen(
    viewModel: RpgViewModel,
    onNavigateToCharacters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val activeCombat by viewModel.activeCombat.collectAsStateWithLifecycle()
    val guilds by viewModel.guilds.collectAsStateWithLifecycle()
    val dungeonZones = viewModel.getDungeonZones()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
    ) {
        // Active Character Header
        if (selectedCharacter == null) {
            NoActiveCharacterBanner(onNavigateToCharacters)
        } else {
            val char = selectedCharacter!!
            val charGuild = guilds.find { it.id == char.guildId }

            ActiveCharacterHeader(
                character = char,
                guildName = charGuild?.name,
                onHeal = { viewModel.healCharacterWithPotion(char) },
                onChangeCharacter = onNavigateToCharacters
            )

            // Content Area: Either Dungeon Zone Selection or Active Text Combat Console
            if (activeCombat != null) {
                ActiveTextCombatView(
                    combat = activeCombat!!,
                    character = char,
                    viewModel = viewModel
                )
            } else {
                DungeonZoneSelectionView(
                    character = char,
                    zones = dungeonZones,
                    onSelectZone = { zone -> viewModel.startDungeonEncounter(zone, char) }
                )
            }
        }
    }
}

@Composable
fun NoActiveCharacterBanner(onNavigateToCharacters: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("⚔️", fontSize = 48.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Pilih Pahlawan Terlebih Dahulu",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Kamu belum memilih pahlawan untuk bertualang. Buka tab Karakter untuk memilih atau membuat pahlawan baru.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onNavigateToCharacters,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg)
                ) {
                    Text("Buka Daftar Karakter", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActiveCharacterHeader(
    character: CharacterEntity,
    guildName: String?,
    onHeal: () -> Unit,
    onChangeCharacter: () -> Unit
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterAvatar(
                    avatarUri = character.avatarUri,
                    characterClass = character.characterClass,
                    size = 50.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = character.characterClass.themeColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "Lv.${character.level} ${character.characterClass.title}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = character.characterClass.themeColor,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }

                    Text(
                        text = if (guildName != null) "🛡️ Guild: $guildName" else "Petualang Bebas (Solo)",
                        fontSize = 11.sp,
                        color = GoldLight
                    )
                }

                // Quick Heal button
                IconButton(
                    onClick = onHeal,
                    enabled = character.potions > 0 && character.hp < character.maxHp,
                    modifier = Modifier.testTag("quick_heal_button")
                ) {
                    Icon(
                        Icons.Default.LocalDrink,
                        contentDescription = "Minum Potion",
                        tint = if (character.potions > 0 && character.hp < character.maxHp) HpColor else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bars: HP & MP
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatProgressBar(
                    label = "HP",
                    currentValue = character.hp,
                    maxValue = character.maxHp,
                    color = HpColor,
                    modifier = Modifier.weight(1f)
                )
                StatProgressBar(
                    label = "MP",
                    currentValue = character.mp,
                    maxValue = character.maxMp,
                    color = MpColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mini stats: EXP, Gold, Potion
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "EXP: ${character.exp}/${character.level * 100}  •  💰 ${character.gold} G  •  🧪 ${character.potions} HP  •  💧 ${character.manaPotions} MP",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Ganti Hero",
                    fontSize = 11.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onChangeCharacter() }
                )
            }
        }
    }
}

@Composable
fun DungeonZoneSelectionView(
    character: CharacterEntity,
    zones: List<DungeonZone>,
    onSelectZone: (DungeonZone) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Adventure Quest Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_banner_quest),
                    contentDescription = "Quest Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, ObsidianBg.copy(alpha = 0.95f))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Jelajah Dungeon & Ekspedisi",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Pilih lokasi bertualang untuk mencari harta dan mengasah kekuatan pahlawan!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Text(
                text = "Pilih Zona Dungeon:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(zones) { zone ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectZone(zone) }
                    .testTag("dungeon_zone_${zone.id}")
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = zone.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (zone.dangerLevel == "Mudah") ArcherGreen.copy(alpha = 0.2f) else if (zone.dangerLevel == "Menengah") GoldPrimary.copy(alpha = 0.2f) else WarriorRed.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = zone.dangerLevel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (zone.dangerLevel == "Mudah") ArcherGreen else if (zone.dangerLevel == "Menengah") GoldPrimary else WarriorRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = zone.description,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = "Monster Potensial: ${zone.possibleEnemies.joinToString { it.iconEmoji + " " + it.name }}",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Mulai Ekspedisi",
                        tint = GoldPrimary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun ActiveTextCombatView(
    combat: ActiveCombat,
    character: CharacterEntity,
    viewModel: RpgViewModel
) {
    val listState = rememberLazyListState()
    val equippedSkills by viewModel.getSkillsForCharacter(character.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // Auto scroll to bottom when new battle logs arrive
    LaunchedEffect(combat.logs.size) {
        if (combat.logs.isNotEmpty()) {
            listState.animateScrollToItem(combat.logs.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Monster Status Banner
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, WarriorRed.copy(alpha = 0.6f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(combat.monsterEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = combat.monsterName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = WarriorRed
                        )
                        Text(
                            text = "⚔️ ATK: ${combat.monsterAtk}  •  🛡️ DEF: ${combat.monsterDef}  •  Khusus: ${combat.monsterSpecial}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                StatProgressBar(
                    label = "HP Musuh",
                    currentValue = combat.monsterHp,
                    maxValue = combat.monsterMaxHp,
                    color = WarriorRed,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Text RPG Console Window
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A0912)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Console title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF151324))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("📟 LOG PETUALANGAN TEKS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GoldLight)
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Giliran ke-${combat.turnCount}", fontSize = 10.sp, color = TextMuted)
                }

                // Logs list
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(combat.logs) { log ->
                        val textColor = when (log.logType) {
                            BattleLogType.INFO -> TextSecondary
                            BattleLogType.PLAYER_ATTACK -> Color(0xFF80ED99)
                            BattleLogType.PLAYER_SKILL -> GoldLight
                            BattleLogType.PLAYER_DEFEND -> Color(0xFF90E0EF)
                            BattleLogType.PLAYER_POTION -> Color(0xFFFFB4A2)
                            BattleLogType.ENEMY_ATTACK -> Color(0xFFFF758F)
                            BattleLogType.ENEMY_SKILL -> Color(0xFFFF4D6D)
                            BattleLogType.VICTORY -> GoldPrimary
                            BattleLogType.DEFEAT -> WarriorRed
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF13111E),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, textColor.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = log.message,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = textColor,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tactical Action Controls or End of Battle Panel
        if (combat.isGameOver) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (combat.isVictory) GoldPrimary else CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (combat.isVictory) "🏆 PETUALANGAN SELESAI DENGAN GEMILANG!" else "💀 PERTARUNGAN BERAKHIR!",
                        fontWeight = FontWeight.Bold,
                        color = if (combat.isVictory) GoldPrimary else WarriorRed,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.closeCombat() },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("continue_adventure_button")
                    ) {
                        Text("Lanjutkan Perjalanan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // 5 Tactical Battle Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Serang Biasa
                    Button(
                        onClick = { viewModel.playerAttack() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("combat_attack_button")
                    ) {
                        Text("⚔️ Serang", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    // Skill Kelas
                    Button(
                        onClick = { viewModel.playerSkill() },
                        enabled = combat.playerMp >= character.characterClass.skillMpCost,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = character.characterClass.themeColor.copy(alpha = 0.25f),
                            contentColor = character.characterClass.themeColor
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, character.characterClass.themeColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("combat_skill_button")
                    ) {
                        Text(
                            "✨ ${character.characterClass.skillName} (${character.characterClass.skillMpCost} MP)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Bertahan
                    Button(
                        onClick = { viewModel.playerDefend() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("combat_defend_button")
                    ) {
                        Text("🛡️ Tahan", fontSize = 11.sp, color = TextPrimary)
                    }

                    // Minum Potion HP
                    Button(
                        onClick = { viewModel.playerPotion() },
                        enabled = character.potions > 0 && combat.playerHp < combat.playerMaxHp,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .testTag("combat_potion_button")
                    ) {
                        Text("🧪 HP (${character.potions})", fontSize = 11.sp, color = TextPrimary)
                    }

                    // Minum Potion MP
                    Button(
                        onClick = { viewModel.playerManaPotion() },
                        enabled = character.manaPotions > 0 && combat.playerMp < combat.playerMaxMp,
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(44.dp)
                            .testTag("combat_mana_potion_button")
                    ) {
                        Text("💧 MP (${character.manaPotions})", fontSize = 11.sp, color = TextPrimary)
                    }

                    // Kabur
                    Button(
                        onClick = { viewModel.playerFlee() },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .height(44.dp)
                            .testTag("combat_flee_button")
                    ) {
                        Text("🏃 Kabur", fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}
