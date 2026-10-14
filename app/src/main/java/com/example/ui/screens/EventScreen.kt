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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
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
import com.example.data.model.CharacterEntity
import com.example.data.model.CustomSkillEntity
import com.example.data.model.EventMonsterEntity
import com.example.ui.RpgViewModel
import com.example.ui.components.StatChip
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.MpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SpdColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    viewModel: RpgViewModel,
    onNavigateToSpawnMonster: () -> Unit,
    onNavigateToCreateSkill: () -> Unit,
    onNavigateToAdventure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eventMonsters by viewModel.eventMonsters.collectAsStateWithLifecycle()
    val activeEventMonsters by viewModel.activeEventMonsters.collectAsStateWithLifecycle()
    val customSkills by viewModel.customSkills.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val isAutonomousThinking by viewModel.isAutonomousThinking.collectAsStateWithLifecycle()
    val eventActionResult by viewModel.eventActionResult.collectAsStateWithLifecycle()

    var currentSubTab by remember { mutableIntStateOf(0) } // 0: Monster Event, 1: Bengkel Skill
    var monsterFilter by remember { mutableStateOf("ALL") } // "ALL", "ACTIVE", "DEFEATED"
    var skillFilter by remember { mutableStateOf("ALL") } // "ALL", "CHARACTER", "MONSTER"

    var skillToAssign by remember { mutableStateOf<CustomSkillEntity?>(null) }
    var monsterToDelete by remember { mutableStateOf<EventMonsterEntity?>(null) }
    var skillToDelete by remember { mutableStateOf<CustomSkillEntity?>(null) }

    // Dialog konfirmasi hapus monster
    if (monsterToDelete != null) {
        AlertDialog(
            onDismissRequest = { monsterToDelete = null },
            title = { Text("Hapus Monster Event?", fontWeight = FontWeight.Bold) },
            text = { Text("Monster \"${monsterToDelete?.name}\" akan dihapus dari catatan event dunia.") },
            confirmButton = {
                Button(
                    onClick = {
                        monsterToDelete?.let { viewModel.deleteEventMonster(it) }
                        monsterToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { monsterToDelete = null }) {
                    Text("Batal")
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog konfirmasi hapus skill
    if (skillToDelete != null) {
        AlertDialog(
            onDismissRequest = { skillToDelete = null },
            title = { Text("Hapus Skill Kustom?", fontWeight = FontWeight.Bold) },
            text = { Text("Skill \"${skillToDelete?.name}\" akan dihapus permanen.") },
            confirmButton = {
                Button(
                    onClick = {
                        skillToDelete?.let { viewModel.deleteCustomSkill(it) }
                        skillToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                ) {
                    Text("Hapus", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { skillToDelete = null }) {
                    Text("Batal")
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Dialog Pasang Skill ke Karakter
    if (skillToAssign != null) {
        val skill = skillToAssign!!
        AlertDialog(
            onDismissRequest = { skillToAssign = null },
            title = {
                Text(
                    text = "Pasang Skill: ${skill.name}",
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pilih karakter yang akan mempelajari dan merapal jurus ${skill.iconEmoji} ${skill.name} ini:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.assignSkillToCharacter(skill.id, null)
                                skillToAssign = null
                            }
                    ) {
                        Text(
                            text = "❌ Lepas dari Karakter (Jadikan Bebas)",
                            modifier = Modifier.padding(12.dp),
                            color = WarriorRed,
                            fontSize = 13.sp
                        )
                    }

                    characters.forEach { char ->
                        val isAssigned = skill.assignedCharacterId == char.id
                        Surface(
                            color = if (isAssigned) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            border = if (isAssigned) androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary) else null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.assignSkillToCharacter(skill.id, char.id)
                                    skillToAssign = null
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${char.characterClass.iconEmoji} ${char.name} (Lvl ${char.level})",
                                    fontWeight = if (isAssigned) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isAssigned) GoldPrimary else TextPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isAssigned) {
                                    Icon(Icons.Default.Check, contentDescription = "Terpasang", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { skillToAssign = null }) {
                    Text("Tutup", color = GoldPrimary)
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    // Modal Hasil Simulasi Solo / Raid Karakter terhadap Event Monster
    if (eventActionResult != null) {
        val actionLog = eventActionResult!!
        AlertDialog(
            onDismissRequest = { viewModel.dismissEventActionResult() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (actionLog.actionType == "EVENT_SOLO") "⚔️ HASIL SOLO EVENT" else "🛡️ HASIL RAID GUILD EVENT",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 17.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Reason Box (Kenapa karakter mikir solo atau raid)
                    Surface(
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Psychology, contentDescription = "Pikiran Karakter", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pikiran & Pertimbangan Karakter:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = actionLog.decisionReason,
                                fontSize = 12.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    // Narasi Pertarungan
                    Surface(
                        color = ObsidianBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = actionLog.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = actionLog.narrative,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // Rewards Box
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("EXP Diperoleh", fontSize = 11.sp, color = TextSecondary)
                                Text("+${actionLog.expGained}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Gold Didapat", fontSize = 11.sp, color = TextSecondary)
                                Text("+${actionLog.goldGained} G", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                            }
                            if (actionLog.isLevelUp) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Status", fontSize = 11.sp, color = TextSecondary)
                                    Text("⭐ LEVEL UP!", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = HpColor)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissEventActionResult() },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg)
                ) {
                    Text("Tutup Laporan Tempur", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (currentSubTab == 0) {
                        viewModel.resetMonsterSpawnForm()
                        onNavigateToSpawnMonster()
                    } else {
                        viewModel.resetSkillCreateForm()
                        onNavigateToCreateSkill()
                    }
                },
                containerColor = if (currentSubTab == 0) WarriorRed else GoldPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("event_fab_action")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (currentSubTab == 0) "Summon Monster" else "Tempa Skill Baru",
                        fontWeight = FontWeight.Bold
                    )
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
            // Top Tab Row
            TabRow(
                selectedTabIndex = currentSubTab,
                containerColor = DarkSurface,
                contentColor = GoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[currentSubTab]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = currentSubTab == 0,
                    onClick = { currentSubTab = 0 },
                    text = {
                        Text(
                            text = "👹 Monster Event (${activeEventMonsters.size} Aktif)",
                            fontWeight = if (currentSubTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_event_monsters")
                )
                Tab(
                    selected = currentSubTab == 1,
                    onClick = { currentSubTab = 1 },
                    text = {
                        Text(
                            text = "✨ Bengkel Skill (${customSkills.size})",
                            fontWeight = if (currentSubTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_custom_skills")
                )
            }

            if (currentSubTab == 0) {
                // ==================== SUB-TAB 0: MONSTER EVENT ====================
                val filteredMonsters = eventMonsters.filter { m ->
                    when (monsterFilter) {
                        "ACTIVE" -> !m.isDefeated
                        "DEFEATED" -> m.isDefeated
                        else -> true
                    }
                }

                // Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL" to "Semua (${eventMonsters.size})", "ACTIVE" to "Aktif (${activeEventMonsters.size})", "DEFEATED" to "Tumbang").forEach { (f, label) ->
                        FilterChip(
                            selected = monsterFilter == f,
                            onClick = { monsterFilter = f },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = ObsidianBg
                            )
                        )
                    }
                }

                if (filteredMonsters.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👹", fontSize = 52.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Monster Event",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kamu bisa summon atau spawn monster kustom dengan HP, ATK, DEF, skill, dan custom drop pusaka sesuai keinginan!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.resetMonsterSpawnForm()
                                    onNavigateToSpawnMonster()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                            ) {
                                Text("Summon Monster Event Sekarang 👹", color = Color.White)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredMonsters, key = { it.id }) { monster ->
                            EventMonsterCard(
                                monster = monster,
                                isThinking = isAutonomousThinking,
                                selectedCharacter = selectedCharacter,
                                onAutonomousDecision = {
                                    viewModel.triggerEventMonsterSoloRaid(monster, selectedCharacter?.id)
                                },
                                onManualBattle = {
                                    selectedCharacter?.let { char ->
                                        viewModel.startEventMonsterManualCombat(monster, char)
                                        onNavigateToAdventure()
                                    }
                                },
                                onDelete = { monsterToDelete = monster }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            } else {
                // ==================== SUB-TAB 1: BENGKEL SKILL ====================
                val filteredSkills = customSkills.filter { s ->
                    when (skillFilter) {
                        "CHARACTER" -> !s.isMonsterSkill
                        "MONSTER" -> s.isMonsterSkill
                        else -> true
                    }
                }

                // Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL" to "Semua Skill", "CHARACTER" to "Skill Karakter", "MONSTER" to "Skill Monster").forEach { (f, label) ->
                        FilterChip(
                            selected = skillFilter == f,
                            onClick = { skillFilter = f },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = ObsidianBg
                            )
                        )
                    }
                }

                if (filteredSkills.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("✨", fontSize = 52.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum Ada Skill Kustom",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Bikin skill sendiri dengan nama, ikon, MP cost, multiplier damage atau heal, lalu pasang ke karaktermu atau monster event!",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = {
                                    viewModel.resetSkillCreateForm()
                                    onNavigateToCreateSkill()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg)
                            ) {
                                Text("Tempa Skill Pertama ✨", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredSkills, key = { it.id }) { skill ->
                            val assignedChar = characters.find { it.id == skill.assignedCharacterId }
                            val assignedMonster = eventMonsters.find { it.id == skill.assignedMonsterId }

                            CustomSkillCard(
                                skill = skill,
                                assignedCharacter = assignedChar,
                                assignedMonster = assignedMonster,
                                onAssignClick = { skillToAssign = skill },
                                onUnassignClick = { viewModel.assignSkillToCharacter(skill.id, null) },
                                onDelete = { skillToDelete = skill }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventMonsterCard(
    monster: EventMonsterEntity,
    isThinking: Boolean,
    selectedCharacter: CharacterEntity?,
    onAutonomousDecision: () -> Unit,
    onManualBattle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            width = if (monster.isDefeated) 1.dp else 1.5.dp,
            color = if (monster.isDefeated) CardBorder else WarriorRed.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Emoji, Name, Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(WarriorRed.copy(alpha = 0.2f))
                            .border(2.dp, if (monster.isDefeated) CardBorder else WarriorRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = monster.iconEmoji, fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = monster.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (monster.isDefeated) TextMuted else TextPrimary
                            )
                        }
                        Text(
                            text = monster.title,
                            fontSize = 12.sp,
                            color = GoldPrimary
                        )
                    }
                }

                if (monster.isDefeated) {
                    Surface(
                        color = CardBorder,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "☠️ TUMBANG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                } else {
                    Surface(
                        color = WarriorRed.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarriorRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🔥 AKTIF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarriorRed,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // HP Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ketahanan Bos (HP):", fontSize = 11.sp, color = TextSecondary)
                    Text(
                        text = if (monster.isDefeated) "0 / ${monster.maxHp} HP" else "${monster.hp} / ${monster.maxHp} HP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = HpColor
                    )
                }
                StatProgressBar(
                    current = if (monster.isDefeated) 0 else monster.hp,
                    max = monster.maxHp,
                    fillColor = if (monster.isDefeated) CardBorder else HpColor,
                    height = 8.dp
                )
            }

            // Stats chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip(label = "ATK", value = monster.atk.toString(), color = AtkColor, modifier = Modifier.weight(1f))
                StatChip(label = "DEF", value = monster.def.toString(), color = DefColor, modifier = Modifier.weight(1f))
                StatChip(label = "SPD", value = monster.spd.toString(), color = SpdColor, modifier = Modifier.weight(1f))
            }

            // Skill Bos
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚡ Jurus: ${monster.skillName}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GoldPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("(${"%.1f".format(monster.skillMultiplier)}x Power)", fontSize = 11.sp, color = TextSecondary)
                    }
                    if (monster.skillDescription.isNotBlank()) {
                        Text(monster.skillDescription, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }

            // Drops Kustom Box
            Surface(
                color = GoldPrimary.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("🎁 Hadiah Drop Spesial:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "⭐ +${monster.expDrop} EXP • 💰 +${monster.goldDrop} Gold",
                        fontSize = 12.sp,
                        color = TextPrimary
                    )
                    if (!monster.itemDropName.isNullOrBlank()) {
                        Text(
                            text = "👑 Drop Pusaka Unik: \"${monster.itemDropName}\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight
                        )
                    }
                }
            }

            // Action Buttons
            if (!monster.isDefeated) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tombol AI Karakter Mikir Sendiri (Solo / Raid)
                    Button(
                        onClick = onAutonomousDecision,
                        enabled = !isThinking,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBg
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("autonomous_solo_raid_button_${monster.id}")
                    ) {
                        if (isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = ObsidianBg
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Karakter Sedang Menimbang...", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.Psychology, contentDescription = "AI Berpikir")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🧠 Karakter Mikir & Serang (Solo/Raid)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Tantang Manual Langsung
                        OutlinedButton(
                            onClick = onManualBattle,
                            enabled = selectedCharacter != null,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarriorRed),
                            border = androidx.compose.foundation.BorderStroke(1.dp, WarriorRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
                        ) {
                            Icon(Icons.Default.SportsKabaddi, contentDescription = "Tantang", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("⚔️ Tantang Langsung", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        // Tombol Hapus
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bersihkan Monster", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomSkillCard(
    skill: CustomSkillEntity,
    assignedCharacter: CharacterEntity?,
    assignedMonster: EventMonsterEntity?,
    onAssignClick: () -> Unit,
    onUnassignClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldPrimary.copy(alpha = 0.18f))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = skill.iconEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = skill.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(
                                color = when (skill.skillType) {
                                    "HEAL" -> HpColor.copy(alpha = 0.2f)
                                    "ULTIMATE" -> GoldPrimary.copy(alpha = 0.2f)
                                    else -> AtkColor.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = skill.skillType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (skill.skillType) {
                                        "HEAL" -> HpColor
                                        "ULTIMATE" -> GoldPrimary
                                        else -> AtkColor
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "Biaya: ${skill.mpCost} MP • Daya: ${"%.1f".format(skill.powerMultiplier)}x",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Skill", tint = TextSecondary, modifier = Modifier.size(16.dp))
                }
            }

            if (skill.description.isNotBlank()) {
                Text(
                    text = skill.description,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // Status Pemasangan
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val statusText = when {
                        assignedCharacter != null -> "👤 Terpasang pada: ${assignedCharacter.name}"
                        assignedMonster != null -> "👹 Terpasang pada: ${assignedMonster.name}"
                        skill.isMonsterSkill -> "👹 Skill Khusus Monster"
                        else -> "Belum Dipasang ke Siapa pun"
                    }

                    Text(
                        text = statusText,
                        fontSize = 12.sp,
                        fontWeight = if (assignedCharacter != null || assignedMonster != null) FontWeight.Bold else FontWeight.Normal,
                        color = if (assignedCharacter != null) GoldPrimary else if (assignedMonster != null) WarriorRed else TextSecondary
                    )

                    if (!skill.isMonsterSkill) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (assignedCharacter != null) {
                                TextButton(
                                    onClick = onUnassignClick,
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Lepas", fontSize = 11.sp, color = WarriorRed)
                                }
                            }
                            Button(
                                onClick = onAssignClick,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(
                                    text = if (assignedCharacter != null) "Ganti..." else "Pasang...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
