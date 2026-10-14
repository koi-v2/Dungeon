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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.RpgViewModel
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.MpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

val SKILL_EMOJIS = listOf("⚡", "🔥", "❄️", "🗡️", "🏹", "✨", "💀", "💥", "🛡️", "🌪️", "☄️", "🔮", "🩸", "🌟", "⚔️", "👁️")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateCustomSkillScreen(
    viewModel: RpgViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name by viewModel.skillCreateName.collectAsStateWithLifecycle()
    val type by viewModel.skillCreateType.collectAsStateWithLifecycle()
    val mpCost by viewModel.skillCreateMpCost.collectAsStateWithLifecycle()
    val powerMultiplier by viewModel.skillCreatePowerMultiplier.collectAsStateWithLifecycle()
    val icon by viewModel.skillCreateIcon.collectAsStateWithLifecycle()
    val desc by viewModel.skillCreateDesc.collectAsStateWithLifecycle()
    val assignedCharId by viewModel.skillCreateAssignCharId.collectAsStateWithLifecycle()
    val assignedMonsterId by viewModel.skillCreateAssignMonsterId.collectAsStateWithLifecycle()
    val isMonsterSkill by viewModel.skillCreateIsMonster.collectAsStateWithLifecycle()

    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val eventMonsters by viewModel.eventMonsters.collectAsStateWithLifecycle()

    var charDropdownExpanded by remember { mutableStateOf(false) }
    var monsterDropdownExpanded by remember { mutableStateOf(false) }

    val skillTypes = listOf(
        "ATTACK" to "⚔️ Serangan Fisik / Sihir",
        "HEAL" to "💖 Pemulihan HP",
        "ULTIMATE" to "⚡ Jurus Pamungkas (Ultimate)",
        "BUFF" to "🛡️ Pertahanan & Daya Juang"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tempa Skill Sendiri",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_skill_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextSecondary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = ObsidianBg,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Preview Card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldPrimary.copy(alpha = 0.2f))
                            .border(2.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 30.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (name.isBlank()) "Nama Skill Kustom" else name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Tipe: $type • Biaya: $mpCost MP • Daya: ${"%.1f".format(powerMultiplier)}x",
                            fontSize = 12.sp,
                            color = GoldPrimary
                        )
                        if (desc.isNotBlank()) {
                            Text(
                                text = desc,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // Nama Skill
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.skillCreateName.value = it },
                label = { Text("Nama Skill (Wajib)") },
                placeholder = { Text("cth: Tebasan Badai Halilintar Purba") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("skill_name_input")
            )

            // Pilihan Emoji Ikon
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Pilih Ikon Skill:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SKILL_EMOJIS.forEach { em ->
                        val isSelected = em == icon
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else CardBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { viewModel.skillCreateIcon.value = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = em, fontSize = 20.sp)
                        }
                    }
                }
            }

            // Tipe Skill
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Tipe Jurus:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                skillTypes.forEach { (typeKey, label) ->
                    val isSelected = type == typeKey
                    Surface(
                        color = if (isSelected) GoldPrimary.copy(alpha = 0.15f) else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) GoldPrimary else CardBorder
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.skillCreateType.value = typeKey }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoldPrimary else TextPrimary
                            )
                        }
                    }
                }
            }

            // Slider Biaya MP
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Konsumsi MP (Mana)", fontSize = 13.sp, color = TextSecondary)
                        Text("$mpCost MP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MpColor)
                    }
                    Slider(
                        value = mpCost.toFloat(),
                        onValueChange = { viewModel.skillCreateMpCost.value = it.toInt() },
                        valueRange = 10f..100f,
                        steps = 17,
                        colors = SliderDefaults.colors(
                            thumbColor = MpColor,
                            activeTrackColor = MpColor,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )
                }
            }

            // Slider Pengali Kekuatan
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (type == "HEAL") "Efek Pemulihan HP" else "Pengali Kekuatan Damage",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "${"%.1f".format(powerMultiplier)}x Multiplier",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                    }
                    Slider(
                        value = powerMultiplier,
                        onValueChange = { viewModel.skillCreatePowerMultiplier.value = it },
                        valueRange = 1.2f..5.0f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = GoldPrimary,
                            activeTrackColor = GoldPrimary,
                            inactiveTrackColor = DarkSurfaceVariant
                        )
                    )
                }
            }

            // Deskripsi Skill
            OutlinedTextField(
                value = desc,
                onValueChange = { viewModel.skillCreateDesc.value = it },
                label = { Text("Deskripsi & Narasi Jurus") },
                placeholder = { Text("cth: Mengumpulkan energi petir di ujung senjata lalu melepaskannya.") },
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoldPrimary,
                    unfocusedLabelColor = TextSecondary,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Target Pemasangan Skill (Karakter atau Monster)
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Target Pemasangan Skill (Bisa Langsung Dipasang):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GoldPrimary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = !isMonsterSkill,
                            onClick = {
                                viewModel.skillCreateIsMonster.value = false
                                viewModel.skillCreateAssignMonsterId.value = null
                            },
                            label = { Text("👤 Untuk Karakter") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary,
                                selectedLabelColor = ObsidianBg
                            )
                        )
                        FilterChip(
                            selected = isMonsterSkill,
                            onClick = {
                                viewModel.skillCreateIsMonster.value = true
                                viewModel.skillCreateAssignCharId.value = null
                            },
                            label = { Text("👹 Untuk Monster") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarriorRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    if (!isMonsterSkill) {
                        // Dropdown pilih Karakter
                        val selectedChar = characters.find { it.id == assignedCharId }
                        ExposedDropdownMenuBox(
                            expanded = charDropdownExpanded,
                            onExpandedChange = { charDropdownExpanded = !charDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedChar?.let { "${it.name} (${it.characterClass.title})" } ?: "— Bebas / Belum Dipasang —",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = charDropdownExpanded) },
                                label = { Text("Pasang ke Karakter:") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldPrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = charDropdownExpanded,
                                onDismissRequest = { charDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("— Bebas / Belum Dipasang —") },
                                    onClick = {
                                        viewModel.skillCreateAssignCharId.value = null
                                        charDropdownExpanded = false
                                    }
                                )
                                characters.forEach { char ->
                                    DropdownMenuItem(
                                        text = { Text("${char.characterClass.iconEmoji} ${char.name} (Lvl ${char.level})") },
                                        onClick = {
                                            viewModel.skillCreateAssignCharId.value = char.id
                                            charDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Dropdown pilih Monster
                        val selectedMonster = eventMonsters.find { it.id == assignedMonsterId }
                        ExposedDropdownMenuBox(
                            expanded = monsterDropdownExpanded,
                            onExpandedChange = { monsterDropdownExpanded = !monsterDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedMonster?.let { "${it.iconEmoji} ${it.name}" } ?: "— Bebas / Semua Monster —",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = monsterDropdownExpanded) },
                                label = { Text("Pasang ke Monster Event:") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = WarriorRed,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = DarkSurfaceVariant,
                                    unfocusedContainerColor = DarkSurfaceVariant
                                ),
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = monsterDropdownExpanded,
                                onDismissRequest = { monsterDropdownExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("— Bebas / Semua Monster —") },
                                    onClick = {
                                        viewModel.skillCreateAssignMonsterId.value = null
                                        monsterDropdownExpanded = false
                                    }
                                )
                                eventMonsters.forEach { monster ->
                                    DropdownMenuItem(
                                        text = { Text("${monster.iconEmoji} ${monster.name}") },
                                        onClick = {
                                            viewModel.skillCreateAssignMonsterId.value = monster.id
                                            monsterDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Simpan
            Button(
                onClick = {
                    viewModel.saveCustomSkill(onSaved = onDismiss)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_save_skill_button")
            ) {
                Text(
                    text = "✨ Tempa & Simpan Skill Kustom",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
