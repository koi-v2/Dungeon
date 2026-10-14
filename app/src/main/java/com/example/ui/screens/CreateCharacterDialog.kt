package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.model.CharacterClass
import com.example.ui.RpgViewModel
import com.example.ui.components.CharacterAvatar
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.AssassinPurple
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CritColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateCharacterScreen(
    viewModel: RpgViewModel,
    onDismiss: () -> Unit
) {
    val name by viewModel.createName.collectAsStateWithLifecycle()
    val selectedClass by viewModel.createClass.collectAsStateWithLifecycle()
    val hp by viewModel.createHp.collectAsStateWithLifecycle()
    val mp by viewModel.createMp.collectAsStateWithLifecycle()
    val atk by viewModel.createAtk.collectAsStateWithLifecycle()
    val def by viewModel.createDef.collectAsStateWithLifecycle()
    val spd by viewModel.createSpd.collectAsStateWithLifecycle()
    val crit by viewModel.createCrit.collectAsStateWithLifecycle()
    val bio by viewModel.createBio.collectAsStateWithLifecycle()
    val guildId by viewModel.createGuildId.collectAsStateWithLifecycle()
    val avatarUri by viewModel.createAvatarUri.collectAsStateWithLifecycle()

    val guilds by viewModel.guilds.collectAsStateWithLifecycle()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.onAvatarPicked(uri)
        }
    }

    var isGuildDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Buat Karakter Baru",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_character_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveNewCharacter(onSaved = onDismiss)
                        },
                        enabled = name.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBg
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_character_button")
                    ) {
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = ObsidianBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Section 1: Nama & Avatar Upload
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Profil & Gambar Karakter",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar Preview
                        Box(contentAlignment = Alignment.BottomEnd) {
                            CharacterAvatar(
                                avatarUri = avatarUri,
                                characterClass = selectedClass,
                                size = 76.dp
                            )
                            if (avatarUri != null) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(WarriorRed)
                                        .clickable { viewModel.createAvatarUri.value = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Hapus Gambar",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedButton(
                                onClick = {
                                    photoPickerLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("upload_character_image_button")
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (avatarUri != null) "Ganti Gambar" else "Unggah Gambar", fontSize = 13.sp)
                            }
                            Text(
                                text = "Bisa pakai foto galeri atau ikon kelas.",
                                fontSize = 11.sp,
                                color = TextMuted,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.createName.value = it },
                        label = { Text("Nama Karakter") },
                        placeholder = { Text("Contoh: Arthur sang Pembantai") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("character_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 2: Pilihan Kelas (Warrior, Mage, Archer, Assassin)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "2. Pilihan Kelas Karakter",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${selectedClass.iconEmoji} ${selectedClass.title}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = selectedClass.themeColor
                        )
                    }
                    Text(
                        text = "Pilih kelas dasar untuk peran tempur dan gaya skill.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    // 4 Class Selection Cards
                    val classes = listOf(
                        CharacterClass.WARRIOR,
                        CharacterClass.MAGE,
                        CharacterClass.ARCHER,
                        CharacterClass.ASSASSIN
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        classes.forEach { cls ->
                            val isSelected = selectedClass == cls
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) cls.themeColor.copy(alpha = 0.18f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) cls.themeColor else CardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.setCreateClass(cls) }
                                    .testTag("class_choice_${cls.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = cls.iconEmoji, fontSize = 28.sp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = cls.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (isSelected) cls.themeColor else TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "• ${cls.role}",
                                                fontSize = 11.sp,
                                                color = TextSecondary
                                            )
                                        }
                                        Text(
                                            text = cls.description,
                                            fontSize = 11.sp,
                                            color = TextMuted,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                        Text(
                                            text = "Skill Khas: ${cls.skillName} (${cls.skillMpCost} MP)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = cls.themeColor.copy(alpha = 0.9f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 3: Pengaturan Stat Kustom (HP, MP, ATK, DEF, SPD, CRIT)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "3. Atur Stat Karakter",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary
                            )
                            Text(
                                text = "Sesuaikan HP, ATK, DEF, MP, SPD, dan CRIT sesuai seleramu.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(
                            onClick = {
                                viewModel.setCreateClass(selectedClass)
                            },
                            modifier = Modifier.testTag("reset_stats_preset_button")
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Reset Preset Kelas",
                                tint = GoldPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stat Sliders & Steppers
                    StatAdjuster(
                        label = "HP (Darah/Health)",
                        icon = "❤️",
                        value = hp,
                        min = 50,
                        max = 1000,
                        step = 25,
                        color = HpColor,
                        onValueChange = { viewModel.createHp.value = it }
                    )

                    StatAdjuster(
                        label = "MP (Mana/Sihir)",
                        icon = "💧",
                        value = mp,
                        min = 20,
                        max = 600,
                        step = 10,
                        color = MpColor,
                        onValueChange = { viewModel.createMp.value = it }
                    )

                    StatAdjuster(
                        label = "ATK (Kekuatan Serang)",
                        icon = "⚔️",
                        value = atk,
                        min = 10,
                        max = 250,
                        step = 5,
                        color = AtkColor,
                        onValueChange = { viewModel.createAtk.value = it }
                    )

                    StatAdjuster(
                        label = "DEF (Pertahanan Baja)",
                        icon = "🛡️",
                        value = def,
                        min = 5,
                        max = 150,
                        step = 5,
                        color = DefColor,
                        onValueChange = { viewModel.createDef.value = it }
                    )

                    StatAdjuster(
                        label = "SPD (Kelincahan Gerak)",
                        icon = "⚡",
                        value = spd,
                        min = 5,
                        max = 120,
                        step = 5,
                        color = SpdColor,
                        onValueChange = { viewModel.createSpd.value = it }
                    )

                    StatAdjuster(
                        label = "CRIT (Peluang Serangan Kritis %)",
                        icon = "🎯",
                        value = crit,
                        min = 5,
                        max = 60,
                        step = 5,
                        color = CritColor,
                        suffix = "%",
                        onValueChange = { viewModel.createCrit.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 4: Tentang Karakter (Bio / Lore)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "4. Tentang Karakter (Latar Belakang & Bio)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "Cerita, kepribadian, senjata andalan, atau riwayat masa lalu karakter.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = bio,
                        onValueChange = { viewModel.createBio.value = it },
                        placeholder = { Text("Tuliskan riwayat, kepribadian, atau tekad karaktermu di sini...") },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("character_bio_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section 5: Gabung ke Guild
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "5. Keanggotaan Guild",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "Pilih guild tempat karakter ini bernaung.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    ExposedDropdownMenuBox(
                        expanded = isGuildDropdownExpanded,
                        onExpandedChange = { isGuildDropdownExpanded = !isGuildDropdownExpanded }
                    ) {
                        val currentGuild = guilds.find { it.id == guildId }
                        val displayText = currentGuild?.let { "${it.emblemIcon} ${it.name} (Lv.${it.level})" } ?: "Tanpa Guild / Petualang Bebas"

                        OutlinedTextField(
                            value = displayText,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGuildDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_guild_dropdown"),
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
                                    viewModel.createGuildId.value = null
                                    isGuildDropdownExpanded = false
                                }
                            )
                            guilds.forEach { g ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(g.emblemIcon, fontSize = 16.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(g.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                                Text("Lv. ${g.level} • ${g.guildType}", fontSize = 11.sp, color = TextSecondary)
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.createGuildId.value = g.id
                                        isGuildDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {
                    viewModel.saveNewCharacter(onSaved = onDismiss)
                },
                enabled = name.trim().isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("submit_create_character_button")
            ) {
                Text("🔥 Buat & Daftarkan Karakter", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
fun StatAdjuster(
    label: String,
    icon: String,
    value: Int,
    min: Int,
    max: Int,
    step: Int,
    color: Color,
    suffix: String = "",
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$value$suffix",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Decrement button
            IconButton(
                onClick = { onValueChange((value - step).coerceAtLeast(min)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Kurang", tint = TextSecondary, modifier = Modifier.size(16.dp))
            }

            Slider(
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                valueRange = min.toFloat()..max.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = color,
                    activeTrackColor = color,
                    inactiveTrackColor = color.copy(alpha = 0.2f)
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
            )

            // Increment button
            IconButton(
                onClick = { onValueChange((value + step).coerceAtMost(max)) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah", tint = TextSecondary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
