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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SpdColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

val MONSTER_EMOJIS = listOf("👹", "🐉", "💀", "🐺", "🦁", "🕷️", "👿", "🦖", "🗿", "🌋", "🦇", "🦂", "🐍", "🧙‍♂️", "👺")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SpawnEventMonsterScreen(
    viewModel: RpgViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name by viewModel.monsterSpawnName.collectAsStateWithLifecycle()
    val title by viewModel.monsterSpawnTitle.collectAsStateWithLifecycle()
    val emoji by viewModel.monsterSpawnEmoji.collectAsStateWithLifecycle()
    val hp by viewModel.monsterSpawnHp.collectAsStateWithLifecycle()
    val atk by viewModel.monsterSpawnAtk.collectAsStateWithLifecycle()
    val def by viewModel.monsterSpawnDef.collectAsStateWithLifecycle()
    val spd by viewModel.monsterSpawnSpd.collectAsStateWithLifecycle()
    val skillName by viewModel.monsterSpawnSkillName.collectAsStateWithLifecycle()
    val skillDesc by viewModel.monsterSpawnSkillDesc.collectAsStateWithLifecycle()
    val expDrop by viewModel.monsterSpawnExpDrop.collectAsStateWithLifecycle()
    val goldDrop by viewModel.monsterSpawnGoldDrop.collectAsStateWithLifecycle()
    val itemDropName by viewModel.monsterSpawnItemDropName.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Summon Monster Event",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_spawn_monster_button")
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
            // Header card
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WarriorRed.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(WarriorRed.copy(alpha = 0.2f))
                            .border(2.dp, WarriorRed, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 36.sp)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (name.isBlank()) "Nama Monster Event" else name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = if (title.isBlank()) "Gelar / Julukan Bos" else title,
                            fontSize = 13.sp,
                            color = GoldPrimary
                        )
                        Text(
                            text = "HP: $hp • ATK: $atk • DEF: $def • SPD: $spd",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }

            // Pilih Emoji
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ikon & Avatar Monster:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MONSTER_EMOJIS.forEach { em ->
                        val isSelected = em == emoji
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else DarkSurface)
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) GoldPrimary else CardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.monsterSpawnEmoji.value = em },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = em, fontSize = 22.sp)
                        }
                    }
                }
            }

            // Nama & Julukan
            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.monsterSpawnName.value = it },
                label = { Text("Nama Monster (Wajib)") },
                placeholder = { Text("cth: Raja Iblis Nether Azazel") },
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
                    .testTag("monster_name_input")
            )

            OutlinedTextField(
                value = title,
                onValueChange = { viewModel.monsterSpawnTitle.value = it },
                label = { Text("Gelar / Julukan") },
                placeholder = { Text("cth: Penguasa Abyss Api Neraka") },
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
                    .testTag("monster_title_input")
            )

            // Stat Konfigurasi
            Text(
                text = "Pengaturan Statistik Monster:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldPrimary
            )

            StatAdjusterCard(
                label = "Darah Monster (HP)",
                value = hp,
                unit = "HP",
                color = HpColor,
                onDecrement = { viewModel.monsterSpawnHp.value = (hp - 100).coerceAtLeast(100) },
                onIncrement = { viewModel.monsterSpawnHp.value = hp + 100 }
            )

            StatAdjusterCard(
                label = "Daya Serang (ATK)",
                value = atk,
                unit = "ATK",
                color = AtkColor,
                onDecrement = { viewModel.monsterSpawnAtk.value = (atk - 5).coerceAtLeast(10) },
                onIncrement = { viewModel.monsterSpawnAtk.value = atk + 5 }
            )

            StatAdjusterCard(
                label = "Pertahanan (DEF)",
                value = def,
                unit = "DEF",
                color = DefColor,
                onDecrement = { viewModel.monsterSpawnDef.value = (def - 5).coerceAtLeast(5) },
                onIncrement = { viewModel.monsterSpawnDef.value = def + 5 }
            )

            StatAdjusterCard(
                label = "Kecepatan (SPD)",
                value = spd,
                unit = "SPD",
                color = SpdColor,
                onDecrement = { viewModel.monsterSpawnSpd.value = (spd - 5).coerceAtLeast(5) },
                onIncrement = { viewModel.monsterSpawnSpd.value = spd + 5 }
            )

            // Skill Monster
            Text(
                text = "Skill & Jurus Khusus Monster:",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = GoldPrimary
            )

            OutlinedTextField(
                value = skillName,
                onValueChange = { viewModel.monsterSpawnSkillName.value = it },
                label = { Text("Nama Skill Monster") },
                placeholder = { Text("cth: Hantaman Meteor Kegelapan") },
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
                    .testTag("monster_skill_name_input")
            )

            OutlinedTextField(
                value = skillDesc,
                onValueChange = { viewModel.monsterSpawnSkillDesc.value = it },
                label = { Text("Deskripsi Efek Skill") },
                placeholder = { Text("cth: Menghancurkan tanah dan menyemburkan magma ke target.") },
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

            // Drop Hadiah Bebas Sesuai Keinginan
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🎁", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Pengaturan Hadiah Drop Kustom",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldPrimary
                            )
                            Text(
                                text = "Tentukan reward yang jatuh saat monster ini berhasil dikalahkan!",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    OutlinedTextField(
                        value = itemDropName,
                        onValueChange = { viewModel.monsterSpawnItemDropName.value = it },
                        label = { Text("Nama Item / Senjata Drop (Bebas)") },
                        placeholder = { Text("cth: Pedang Bara Api Legendaris") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = GoldPrimary,
                            unfocusedLabelColor = TextSecondary,
                            focusedContainerColor = DarkSurfaceVariant,
                            unfocusedContainerColor = DarkSurfaceVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("monster_item_drop_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expDrop.toString(),
                            onValueChange = {
                                val v = it.filter { ch -> ch.isDigit() }
                                viewModel.monsterSpawnExpDrop.value = v.toIntOrNull() ?: 100
                            },
                            label = { Text("Drop EXP") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = GoldPrimary,
                                unfocusedLabelColor = TextSecondary,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = goldDrop.toString(),
                            onValueChange = {
                                val v = it.filter { ch -> ch.isDigit() }
                                viewModel.monsterSpawnGoldDrop.value = v.toIntOrNull() ?: 100
                            },
                            label = { Text("Drop Gold (G)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedLabelColor = GoldPrimary,
                                unfocusedLabelColor = TextSecondary,
                                focusedContainerColor = DarkSurfaceVariant,
                                unfocusedContainerColor = DarkSurfaceVariant
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Spawn
            Button(
                onClick = {
                    viewModel.spawnEventMonster(onSpawned = onDismiss)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarriorRed,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_spawn_monster_button")
            ) {
                Text(
                    text = "👹 Bangkitkan & Spawn Monster Event!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatAdjusterCard(
    label: String,
    value: Int,
    unit: String,
    color: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = label, fontSize = 12.sp, color = TextSecondary)
                Text(
                    text = "$value $unit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrement,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkSurfaceVariant)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Kurang", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.2f))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = color)
                }
            }
        }
    }
}
