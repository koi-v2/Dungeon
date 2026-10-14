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
import com.example.ui.components.GuildEmblemBadge
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.AssassinPurple
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MageBlue
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateGuildScreen(
    viewModel: RpgViewModel,
    onDismiss: () -> Unit
) {
    val name by viewModel.guildCreateName.collectAsStateWithLifecycle()
    val motto by viewModel.guildCreateMotto.collectAsStateWithLifecycle()
    val description by viewModel.guildCreateDescription.collectAsStateWithLifecycle()
    val guildType by viewModel.guildCreateType.collectAsStateWithLifecycle()
    val emblemColorHex by viewModel.guildCreateEmblemColor.collectAsStateWithLifecycle()
    val emblemIcon by viewModel.guildCreateEmblemIcon.collectAsStateWithLifecycle()

    val availableIcons = listOf("🛡️", "⚔️", "🐉", "👑", "🏰", "🔮", "⚡", "🏹", "🦅", "🐺")
    val availableColors = listOf(
        0xFFFFB703, // Gold
        0xFFE63946, // Red
        0xFF48CAE4, // Blue
        0xFF9D4EDD, // Purple
        0xFF2A9D8F, // Green
        0xFFFF758F  // Rose
    )
    val availableTypes = listOf(
        "Ksatria Garda Depan",
        "Perkumpulan Sihir Kuno",
        "Sindikat Bayangan",
        "Pemburu Harta Karun",
        "Aliansi Penjelajah Bebas"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Bentuk Guild Baru",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_create_guild_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Tutup", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            viewModel.saveNewGuild(onSaved = onDismiss)
                        },
                        enabled = name.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = ObsidianBg
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("save_guild_button")
                    ) {
                        Text("Bentuk", fontWeight = FontWeight.Bold)
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
            // Preview Badge Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GuildEmblemBadge(
                        emblemIcon = emblemIcon,
                        colorHex = emblemColorHex,
                        size = 64.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (name.isBlank()) "Nama Guild Baru" else name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(emblemColorHex)
                        )
                        Text(
                            text = if (motto.isBlank()) "\"Motto kehormatan guild...\"" else "\"$motto\"",
                            fontSize = 12.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = "Tipe: $guildType",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guild Details
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Identitas Guild",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { viewModel.guildCreateName.value = it },
                        label = { Text("Nama Guild") },
                        placeholder = { Text("Contoh: Ordo Bintang Kejora") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guild_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = motto,
                        onValueChange = { viewModel.guildCreateMotto.value = it },
                        label = { Text("Motto / Semboyan Guild") },
                        placeholder = { Text("Contoh: Bersatu Kita Menaklukkan Kegelapan!") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guild_motto_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = description,
                        onValueChange = { viewModel.guildCreateDescription.value = it },
                        label = { Text("Deskripsi & Sejarah Guild") },
                        placeholder = { Text("Tuliskan riwayat pendirian dan visi misi perkumpulan guild...") },
                        minLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("guild_description_input"),
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

            // Guild Type
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Tipe & Aliran Guild",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableTypes.forEach { type ->
                            val isSelected = guildType == type
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSelected) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) GoldPrimary else CardBorder
                                ),
                                modifier = Modifier.clickable { viewModel.guildCreateType.value = type }
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) GoldPrimary else TextSecondary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Emblem Icon & Color
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Lambang & Warna Panji",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Pilih Ikon Lambang:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        availableIcons.forEach { icon ->
                            val isSelected = emblemIcon == icon
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(emblemColorHex).copy(alpha = 0.25f) else DarkSurfaceVariant)
                                    .border(
                                        if (isSelected) 2.dp else 1.dp,
                                        if (isSelected) Color(emblemColorHex) else CardBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.guildCreateEmblemIcon.value = icon },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icon, fontSize = 20.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Pilih Warna Panji:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        availableColors.forEach { colorVal ->
                            val isSelected = emblemColorHex == colorVal
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(colorVal))
                                    .border(
                                        if (isSelected) 3.dp else 1.dp,
                                        if (isSelected) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { viewModel.guildCreateEmblemColor.value = colorVal }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.saveNewGuild(onSaved = onDismiss)
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
                    .testTag("submit_create_guild_button")
            ) {
                Text("🛡️ Resmikan Pendirian Guild", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
