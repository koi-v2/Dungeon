package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CharacterActionLogEntity
import com.example.data.model.CharacterClass
import com.example.data.model.CharacterEntity
import com.example.data.model.GuildEntity
import com.example.ui.RpgViewModel
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.GuildEmblemBadge
import com.example.ui.components.StatChip
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.AssassinPurple
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CritColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.MageBlue
import com.example.ui.theme.MpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SpdColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharactersScreen(
    viewModel: RpgViewModel,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val guilds by viewModel.guilds.collectAsStateWithLifecycle()
    val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val isAutonomousThinking by viewModel.isAutonomousThinking.collectAsStateWithLifecycle()

    var selectedClassFilter by remember { mutableStateOf<CharacterClass?>(null) }
    var inspectedCharacter by remember { mutableStateOf<CharacterEntity?>(null) }
    var characterToDelete by remember { mutableStateOf<CharacterEntity?>(null) }

    // If inspected character was updated in database, keep it fresh
    val freshInspectedCharacter = inspectedCharacter?.let { char ->
        characters.find { it.id == char.id } ?: char
    }

    val filteredCharacters = characters.filter { char ->
        selectedClassFilter == null || char.characterClass == selectedClassFilter
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = GoldPrimary,
                contentColor = ObsidianBg,
                modifier = Modifier.testTag("add_character_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Karakter")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Karakter Baru", fontWeight = FontWeight.Bold)
                }
            }
        },
        containerColor = ObsidianBg,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Stats & Autonomous Action bar
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Daftar Pahlawan",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Total ${characters.size} Karakter Terdaftar",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )
                        }

                        // Tombol simulasi semua pahlawan berpikir & berburu sendiri
                        if (characters.isNotEmpty()) {
                            Button(
                                onClick = { viewModel.triggerAllCharactersAutonomous() },
                                enabled = !isAutonomousThinking,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GoldLight.copy(alpha = 0.18f),
                                    contentColor = GoldPrimary
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("simulate_all_autonomous_button")
                            ) {
                                if (isAutonomousThinking) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Berpikir...", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pikiran Mandiri Semua", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Class Filter Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedClassFilter == null,
                                onClick = { selectedClassFilter = null },
                                label = { Text("Semua (${characters.size})") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = ObsidianBg
                                )
                            )
                        }
                        items(CharacterClass.entries.toTypedArray()) { cls ->
                            val count = characters.count { it.characterClass == cls }
                            FilterChip(
                                selected = selectedClassFilter == cls,
                                onClick = { selectedClassFilter = cls },
                                label = { Text("${cls.iconEmoji} ${cls.title} ($count)") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = cls.themeColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            if (filteredCharacters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🛡️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (characters.isEmpty()) "Belum ada karakter yang dibuat" else "Tidak ada karakter di kelas ini",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tekan tombol + di bawah untuk meracik pahlawanmu!",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    items(filteredCharacters, key = { it.id }) { character ->
                        val characterGuild = guilds.find { it.id == character.guildId }
                        val isActiveHero = selectedCharacter?.id == character.id

                        CharacterCard(
                            character = character,
                            guild = characterGuild,
                            isActiveHero = isActiveHero,
                            onSelectActive = { viewModel.selectCharacter(character) },
                            onCardClick = { inspectedCharacter = character }
                        )
                    }
                }
            }
        }
    }

    // Inspect & Edit Character Bottom Sheet with Personal Logs
    freshInspectedCharacter?.let { char ->
        CharacterDetailBottomSheet(
            character = char,
            guilds = guilds,
            viewModel = viewModel,
            onDismiss = { inspectedCharacter = null },
            onSelectActive = {
                viewModel.selectCharacter(char)
                inspectedCharacter = null
            },
            onHeal = {
                viewModel.healCharacterWithPotion(char)
            },
            onChangeGuild = { newGuildId ->
                viewModel.updateCharacterGuild(char.id, newGuildId)
            },
            onDelete = {
                characterToDelete = char
                inspectedCharacter = null
            }
        )
    }

    // Delete Confirmation Dialog
    characterToDelete?.let { char ->
        AlertDialog(
            onDismissRequest = { characterToDelete = null },
            title = { Text("Hapus Karakter?", fontWeight = FontWeight.Bold) },
            text = { Text("Karakter ${char.name} akan dihapus secara permanen dari pendaftaran pahlawan.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCharacter(char)
                        characterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { characterToDelete = null }) {
                    Text("Batal")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun CharacterCard(
    character: CharacterEntity,
    guild: GuildEntity?,
    isActiveHero: Boolean,
    onSelectActive: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            if (isActiveHero) 2.dp else 1.dp,
            if (isActiveHero) GoldPrimary else CardBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("character_card_${character.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Avatar, Name, Level, Class Badge
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterAvatar(
                    avatarUri = character.avatarUri,
                    characterClass = character.characterClass,
                    size = 58.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        if (isActiveHero) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GoldPrimary,
                                modifier = Modifier.padding(start = 2.dp)
                            ) {
                                Text(
                                    text = "HERO AKTIF",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ObsidianBg,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = character.characterClass.themeColor.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, character.characterClass.themeColor.copy(alpha = 0.5f))
                        ) {
                            Text(
                                text = "${character.characterClass.iconEmoji} ${character.characterClass.title} Lv.${character.level}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = character.characterClass.themeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (guild != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(guild.emblemIcon, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = guild.name,
                                    fontSize = 11.sp,
                                    color = Color(guild.emblemColorHex),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            Text(
                                text = "Solo / Tanpa Guild",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // Compact Stats: ATK, DEF, SPD, CRIT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatChip(label = "ATK", value = "${character.atk}", color = AtkColor, icon = "⚔️")
                StatChip(label = "DEF", value = "${character.def}", color = DefColor, icon = "🛡️")
                StatChip(label = "SPD", value = "${character.spd}", color = SpdColor, icon = "⚡")
                StatChip(label = "CRIT", value = "${character.crit}%", color = CritColor, icon = "🎯")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Row: Gold, Potion, and Quick Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💰 ${character.gold} G  •  🧪 ${character.potions} Potion  •  ⭐ ${character.combatPower} PWR",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Lihat Log & Profil ➔",
                    fontSize = 11.sp,
                    color = GoldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailBottomSheet(
    character: CharacterEntity,
    guilds: List<GuildEntity>,
    viewModel: RpgViewModel,
    onDismiss: () -> Unit,
    onSelectActive: () -> Unit,
    onHeal: () -> Unit,
    onChangeGuild: (Long?) -> Unit,
    onDelete: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isGuildDropdownExpanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Log & Pikiran Mandiri as requested!

    val characterLogs by viewModel.getLogsForCharacter(character.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val equippedSkills by viewModel.getSkillsForCharacter(character.id)
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val isAutonomousThinking by viewModel.isAutonomousThinking.collectAsStateWithLifecycle()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp)
        ) {
            // Header: Avatar, Name, Level
            Row(verticalAlignment = Alignment.CenterVertically) {
                CharacterAvatar(
                    avatarUri = character.avatarUri,
                    characterClass = character.characterClass,
                    size = 64.dp
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "${character.characterClass.iconEmoji} ${character.characterClass.title} • Level ${character.level}",
                        fontSize = 13.sp,
                        color = character.characterClass.themeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Total Kemenangan: ${character.wins}x Menang  •  EXP: ${character.exp}/${character.level * 100}",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Two Tabs: [1] Log & Pikiran Mandiri, [2] Profil & Stat
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurfaceVariant,
                contentColor = GoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "📊 Profil & Stat",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "🧠 Log Petualangan Mandiri (${characterLogs.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 1) {
                // TAB 1: Log & Pikiran Mandiri (Autonomous Thinking & History)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    // Tombol trigger mikir sendiri
                    Button(
                        onClick = { viewModel.triggerAutonomousThinking(character.id) },
                        enabled = !isAutonomousThinking,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("trigger_autonomous_thinking_button")
                    ) {
                        if (isAutonomousThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = ObsidianBg
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sedang Berpikir & Menentukan Jalur...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🧠 Suruh Karakter Berpikir & Berpetualang Sendiri", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (characterLogs.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("📜", fontSize = 36.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Belum Ada Catatan Aksi",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Tekan tombol di atas agar karakter berpikir sendiri mau berburu (hunt) atau menyerang raid boss!",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        val dateFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(characterLogs, key = { it.id }) { log ->
                                CharacterActionLogCard(log = log, timeString = dateFormat.format(Date(log.timestamp)))
                            }
                        }
                    }
                }
            } else {
                // TAB 0: Profil, Bio, Stat, Guild Assignment
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Bio / Lore Section
                        item {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "📜 Riwayat & Tentang Karakter",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (character.bio.isBlank()) "Tidak ada catatan riwayat." else character.bio,
                                        fontSize = 12.sp,
                                        color = TextSecondary,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }

                        // Full Stats Breakdown
                        item {
                            Text(
                                text = "Statistik Tempur",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                StatChip(label = "HP", value = "${character.hp}/${character.maxHp}", color = HpColor, icon = "❤️", modifier = Modifier.weight(1f))
                                StatChip(label = "MP", value = "${character.mp}/${character.maxMp}", color = MpColor, icon = "💧", modifier = Modifier.weight(1f))
                                StatChip(label = "ATK", value = "${character.atk}", color = AtkColor, icon = "⚔️", modifier = Modifier.weight(1f))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                StatChip(label = "DEF", value = "${character.def}", color = DefColor, icon = "🛡️", modifier = Modifier.weight(1f))
                                StatChip(label = "SPD", value = "${character.spd}", color = SpdColor, icon = "⚡", modifier = Modifier.weight(1f))
                                StatChip(label = "CRIT", value = "${character.crit}%", color = CritColor, icon = "🎯", modifier = Modifier.weight(1f))
                            }
                        }

                        // Guild Selection Dropdown
                        item {
                            Text(
                                text = "Keanggotaan Guild",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            ExposedDropdownMenuBox(
                                expanded = isGuildDropdownExpanded,
                                onExpandedChange = { isGuildDropdownExpanded = !isGuildDropdownExpanded }
                            ) {
                                val currentGuild = guilds.find { it.id == character.guildId }
                                val displayText = currentGuild?.let { "${it.emblemIcon} ${it.name} (Lv.${it.level})" } ?: "Tanpa Guild / Petualang Bebas"

                                OutlinedTextField(
                                    value = displayText,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGuildDropdownExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GoldPrimary,
                                        unfocusedBorderColor = CardBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = isGuildDropdownExpanded,
                                    onDismissRequest = { isGuildDropdownExpanded = false },
                                    modifier = Modifier.background(DarkSurfaceVariant)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Tanpa Guild / Petualang Bebas", color = TextPrimary) },
                                        onClick = {
                                            onChangeGuild(null)
                                            isGuildDropdownExpanded = false
                                        }
                                    )
                                    guilds.forEach { g ->
                                        DropdownMenuItem(
                                            text = { Text("${g.emblemIcon} ${g.name} (Lv.${g.level})", color = TextPrimary) },
                                            onClick = {
                                                onChangeGuild(g.id)
                                                isGuildDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Pusaka Drop Event
                        if (!character.equippedCustomDrop.isNullOrBlank()) {
                            item {
                                Surface(
                                    color = GoldPrimary.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("👑", fontSize = 24.sp)
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Pusaka Rampasan Bos Event",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoldPrimary
                                            )
                                            Text(
                                                text = character.equippedCustomDrop,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Jurus & Skill Kustom yang Dikuasai
                        item {
                            Text(
                                text = "✨ Jurus & Skill Kustom (${equippedSkills.size})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            if (equippedSkills.isEmpty()) {
                                Surface(
                                    color = DarkSurfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Belum ada skill kustom terpasang. Tempa skill kustom di tab Event & Bos lalu pasang ke karakter ini!",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    equippedSkills.forEach { sk ->
                                        Surface(
                                            color = DarkSurfaceVariant,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(sk.iconEmoji, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(sk.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                                    Text("${sk.skillType} • ${sk.mpCost} MP • Daya ${"%.1f".format(sk.powerMultiplier)}x", fontSize = 11.sp, color = GoldPrimary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Action Buttons: Minum potion & Jadikan Hero Aktif
                        item {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                Button(
                                    onClick = onHeal,
                                    enabled = character.potions > 0 && character.hp < character.maxHp,
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.LocalDrink, contentDescription = null, tint = HpColor, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Minum Potion (${character.potions})", fontSize = 11.sp, color = TextPrimary)
                                }

                                Button(
                                    onClick = onSelectActive,
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Jadikan Hero Aktif", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            TextButton(
                                onClick = onDelete,
                                colors = ButtonDefaults.textButtonColors(contentColor = WarriorRed),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Hapus Karakter Ini", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CharacterActionLogCard(log: CharacterActionLogEntity, timeString: String) {
    val typeBadgeColor = when (log.actionType) {
        "HUNT" -> ArcherGreen
        "RAID" -> WarriorRed
        "EVENT_SOLO" -> WarriorRed
        "EVENT_RAID" -> GoldPrimary
        "SHOP" -> GoldLight
        "LEVEL_UP" -> GoldPrimary
        "REST" -> MageBlue
        else -> AssassinPurple
    }

    val typeIcon = when (log.actionType) {
        "HUNT" -> "🌲 HUNT"
        "RAID" -> "🐉 RAID"
        "EVENT_SOLO" -> "⚔️ SOLO EVENT"
        "EVENT_RAID" -> "🛡️ RAID EVENT"
        "SHOP" -> "🏪 TOKO"
        "LEVEL_UP" -> "🎉 LEVEL UP"
        "REST" -> "💤 ISTIRAHAT"
        else -> "💭 PIKIRAN"
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = DarkSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            if (log.isLevelUp) 1.5.dp else 1.dp,
            if (log.isLevelUp) GoldPrimary else CardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Type Badge, Location, Timestamp
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = typeBadgeColor.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, typeBadgeColor.copy(alpha = 0.6f))
                ) {
                    Text(
                        text = typeIcon,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = typeBadgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = log.location,
                    fontSize = 11.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = timeString,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = log.title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (log.isLevelUp) GoldPrimary else TextPrimary
            )

            // Decision Reason / Thought process
            if (log.decisionReason.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = ObsidianBg.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("💭", fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Alasan Pikiran: \"${log.decisionReason}\"",
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = GoldLight,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // Narrative action
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = log.narrative,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            // Reward Chips (EXP, Gold, HP Change)
            if (log.expGained > 0 || log.goldGained > 0 || log.hpChange != 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (log.expGained > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "⭐ +${log.expGained} EXP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (log.goldGained > 0) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = AtkColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "💰 +${log.goldGained} G",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AtkColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    if (log.hpChange != 0) {
                        val hpColor = if (log.hpChange > 0) HpColor else WarriorRed
                        val hpSign = if (log.hpChange > 0) "+" else ""
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = hpColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "❤️ $hpSign${log.hpChange} HP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = hpColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
