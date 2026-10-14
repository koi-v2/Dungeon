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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.CharacterClass
import com.example.ui.RpgViewModel
import com.example.ui.theme.AtkColor
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CritColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DefColor
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.HpColor
import com.example.ui.theme.MpColor
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SpdColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddWeaponScreen(
    viewModel: RpgViewModel,
    onDismiss: () -> Unit
) {
    val name by viewModel.weaponCreateName.collectAsStateWithLifecycle()
    val compatibleClass by viewModel.weaponCreateClass.collectAsStateWithLifecycle()
    val iconEmoji by viewModel.weaponCreateIcon.collectAsStateWithLifecycle()
    val price by viewModel.weaponCreatePrice.collectAsStateWithLifecycle()
    val bonusAtk by viewModel.weaponCreateAtk.collectAsStateWithLifecycle()
    val bonusDef by viewModel.weaponCreateDef.collectAsStateWithLifecycle()
    val bonusHp by viewModel.weaponCreateHp.collectAsStateWithLifecycle()
    val bonusMp by viewModel.weaponCreateMp.collectAsStateWithLifecycle()
    val bonusSpd by viewModel.weaponCreateSpd.collectAsStateWithLifecycle()
    val bonusCrit by viewModel.weaponCreateCrit.collectAsStateWithLifecycle()
    val desc by viewModel.weaponCreateDesc.collectAsStateWithLifecycle()

    val availableIcons = listOf("⚔️", "🗡️", "🏹", "🔮", "🪓", "🪄", "🔱", "🛡️", "⚡", "🪃")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tempa Senjata Toko Baru",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_add_weapon_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveNewWeapon(onSaved = onDismiss)
                        },
                        enabled = name.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBg
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_weapon_button")
                    ) {
                        Text("Jual di Toko", fontWeight = FontWeight.Bold)
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
            // Weapon Identity
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "1. Nama & Ikon Senjata",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurfaceVariant)
                                .border(1.5.dp, GoldPrimary, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = iconEmoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { viewModel.weaponCreateName.value = it },
                            label = { Text("Nama Senjata") },
                            placeholder = { Text("Contoh: Pedang Petir Excalibur") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("weapon_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Pilih Ikon Senjata:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableIcons.forEach { icon ->
                            val isSelected = iconEmoji == icon
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) GoldPrimary.copy(alpha = 0.25f) else DarkSurfaceVariant)
                                    .border(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) GoldPrimary else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.weaponCreateIcon.value = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Compatible Class
                    Text("Kelas yang Cocok:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val isAllSelected = compatibleClass == null
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isAllSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isAllSelected) GoldPrimary else CardBorder),
                            modifier = Modifier.clickable { viewModel.weaponCreateClass.value = null }
                        ) {
                            Text(
                                text = "Semua Kelas",
                                fontSize = 11.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isAllSelected) GoldPrimary else TextSecondary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        CharacterClass.entries.forEach { cls ->
                            val isSelected = compatibleClass == cls
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) cls.themeColor.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) cls.themeColor else CardBorder),
                                modifier = Modifier.clickable { viewModel.weaponCreateClass.value = cls }
                            ) {
                                Text(
                                    text = "${cls.iconEmoji} ${cls.title}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) cls.themeColor else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stat Modifiers
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "2. Atur Bonus Stat Senjata",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Text(
                        text = "Pahlawan yang membeli senjata ini akan memperoleh peningkatan stat ini.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )

                    StatAdjuster(
                        label = "Bonus ATK (Kekuatan Serang)",
                        icon = "⚔️",
                        value = bonusAtk,
                        min = 0,
                        max = 120,
                        step = 5,
                        color = AtkColor,
                        onValueChange = { viewModel.weaponCreateAtk.value = it }
                    )

                    StatAdjuster(
                        label = "Bonus DEF (Pertahanan)",
                        icon = "🛡️",
                        value = bonusDef,
                        min = 0,
                        max = 80,
                        step = 5,
                        color = DefColor,
                        onValueChange = { viewModel.weaponCreateDef.value = it }
                    )

                    StatAdjuster(
                        label = "Bonus HP Tambahan",
                        icon = "❤️",
                        value = bonusHp,
                        min = 0,
                        max = 300,
                        step = 25,
                        color = HpColor,
                        onValueChange = { viewModel.weaponCreateHp.value = it }
                    )

                    StatAdjuster(
                        label = "Bonus MP Tambahan",
                        icon = "💧",
                        value = bonusMp,
                        min = 0,
                        max = 200,
                        step = 10,
                        color = MpColor,
                        onValueChange = { viewModel.weaponCreateMp.value = it }
                    )

                    StatAdjuster(
                        label = "Bonus SPD (Kelincahan)",
                        icon = "⚡",
                        value = bonusSpd,
                        min = 0,
                        max = 40,
                        step = 2,
                        color = SpdColor,
                        onValueChange = { viewModel.weaponCreateSpd.value = it }
                    )

                    StatAdjuster(
                        label = "Bonus Peluang CRIT (%)",
                        icon = "🎯",
                        value = bonusCrit,
                        min = 0,
                        max = 30,
                        step = 2,
                        color = CritColor,
                        suffix = "%",
                        onValueChange = { viewModel.weaponCreateCrit.value = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price & Lore
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "3. Harga & Kisah Senjata",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    StatAdjuster(
                        label = "Harga Jual di Toko (Koin Emas)",
                        icon = "💰",
                        value = price,
                        min = 20,
                        max = 800,
                        step = 10,
                        color = GoldPrimary,
                        suffix = " G",
                        onValueChange = { viewModel.weaponCreatePrice.value = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { viewModel.weaponCreateDesc.value = it },
                        label = { Text("Deskripsi / Kisah Senjata") },
                        placeholder = { Text("Tuliskan riwayat penempaan atau kekuatan legendaris senjata...") },
                        minLines = 2,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("weapon_desc_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveNewWeapon(onSaved = onDismiss)
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
                    .testTag("submit_create_weapon_button")
            ) {
                Text("🔥 Pajang Senjata di Toko", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
