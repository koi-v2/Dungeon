package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CharacterEntity
import com.example.data.model.GuildEntity
import com.example.ui.GuildRaidState
import com.example.ui.RpgViewModel
import com.example.ui.components.CharacterAvatar
import com.example.ui.components.GuildEmblemBadge
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarriorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuildsScreen(
    viewModel: RpgViewModel,
    onNavigateToCreateGuild: () -> Unit,
    modifier: Modifier = Modifier
) {
    val guilds by viewModel.guilds.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val raidState by viewModel.guildRaidState.collectAsStateWithLifecycle()

    var inspectedGuild by remember { mutableStateOf<GuildEntity?>(null) }
    var guildToDelete by remember { mutableStateOf<GuildEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateGuild,
                containerColor = GoldPrimary,
                contentColor = ObsidianBg,
                modifier = Modifier.testTag("add_guild_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Guild")
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
            // Guild Hall Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_guild_hall),
                    contentDescription = "Guild Hall Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, ObsidianBg)
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Aula Perkumpulan Guild",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = "Bentuk aliansi para pahlawan untuk menaklukkan ekspedisi raid purba!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }

            if (guilds.isEmpty()) {
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
                            text = "Belum Ada Guild yang Didirikan",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Dirikan guild pertama dan masukkan karakter pahlawanmu!",
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
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 10.dp, bottom = 72.dp)
                ) {
                    items(guilds, key = { it.id }) { guild ->
                        val guildMembers = characters.filter { it.guildId == guild.id }
                        val totalPower = guildMembers.sumOf { it.combatPower }

                        GuildCard(
                            guild = guild,
                            members = guildMembers,
                            totalPower = totalPower,
                            onClick = { inspectedGuild = guild }
                        )
                    }
                }
            }
        }
    }

    // Guild Detail & Raid Sheet
    inspectedGuild?.let { guild ->
        val members = characters.filter { it.guildId == guild.id }
        GuildDetailBottomSheet(
            guild = guild,
            members = members,
            onDismiss = { inspectedGuild = null },
            onStartRaid = {
                viewModel.startGuildRaid(guild)
            },
            onDeleteGuild = {
                guildToDelete = guild
                inspectedGuild = null
            }
        )
    }

    // Guild Raid Simulation Dialog
    raidState?.let { raid ->
        GuildRaidDialog(
            raidState = raid,
            onClose = { viewModel.closeGuildRaid() }
        )
    }

    // Delete Confirmation
    guildToDelete?.let { guild ->
        AlertDialog(
            onDismissRequest = { guildToDelete = null },
            title = { Text("Bubarkan Guild?", fontWeight = FontWeight.Bold) },
            text = { Text("Guild \"${guild.name}\" akan dibubarkan. Semua anggota akan kembali menjadi petualang bebas.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGuild(guild)
                        guildToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                ) {
                    Text("Bubarkan", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { guildToDelete = null }) {
                    Text("Batal")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun GuildCard(
    guild: GuildEntity,
    members: List<CharacterEntity>,
    totalPower: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("guild_card_${guild.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                GuildEmblemBadge(
                    emblemIcon = guild.emblemIcon,
                    colorHex = guild.emblemColorHex,
                    size = 54.dp
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = guild.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color(guild.emblemColorHex)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GoldPrimary.copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "Lv.${guild.level}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "\"${guild.motto}\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Text(
                        text = "Tipe: ${guild.guildType}",
                        fontSize = 11.sp,
                        color = TextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guild Members avatars preview
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (members.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        members.take(4).forEach { m ->
                            CharacterAvatar(
                                avatarUri = m.avatarUri,
                                characterClass = m.characterClass,
                                size = 30.dp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "${members.size} Anggota",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                } else {
                    Text(
                        text = "Belum ada anggota yang bergabung",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "⚔️ $totalPower PWR",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuildDetailBottomSheet(
    guild: GuildEntity,
    members: List<CharacterEntity>,
    onDismiss: () -> Unit,
    onStartRaid: () -> Unit,
    onDeleteGuild: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                GuildEmblemBadge(
                    emblemIcon = guild.emblemIcon,
                    colorHex = guild.emblemColorHex,
                    size = 62.dp
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guild.name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(guild.emblemColorHex)
                    )
                    Text(
                        text = "\"${guild.motto}\"",
                        fontSize = 12.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextSecondary
                    )
                    Text(
                        text = "Level ${guild.level} • ${guild.raidPoints} Poin Raid",
                        fontSize = 11.sp,
                        color = GoldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Guild Description
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Visi & Sejarah Perkumpulan",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (guild.description.isBlank()) "Tidak ada catatan deskripsi." else guild.description,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Members List
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Daftar Anggota Pahlawan (${members.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Total PWR: ${members.sumOf { it.combatPower }}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (members.isEmpty()) {
                Text(
                    text = "Belum ada anggota. Kamu bisa memasukkan karakter ke guild ini saat membuat karakter atau di menu Karakter.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    members.forEach { m ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CharacterAvatar(
                                    avatarUri = m.avatarUri,
                                    characterClass = m.characterClass,
                                    size = 40.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = m.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${m.characterClass.iconEmoji} ${m.characterClass.title} • Lv.${m.level}",
                                        fontSize = 11.sp,
                                        color = m.characterClass.themeColor
                                    )
                                }
                                Text(
                                    text = "⚔️ ${m.combatPower}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Raid Action Button
            Button(
                onClick = onStartRaid,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("start_guild_raid_button")
            ) {
                Text("🔥 Mulai Ekspedisi Raid Boss Guild", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDeleteGuild,
                colors = ButtonDefaults.textButtonColors(contentColor = WarriorRed),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bubarkan Guild Ini", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GuildRaidDialog(
    raidState: GuildRaidState,
    onClose: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(raidState.boss.iconEmoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Ekspedisi Raid Guild", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                    Text(raidState.boss.name, fontSize = 12.sp, color = WarriorRed)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = raidState.boss.description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Boss Status
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = DarkSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Sisa HP Boss:", fontSize = 12.sp, color = TextSecondary)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                "${raidState.boss.currentHp} / ${raidState.boss.totalHp}",
                                fontWeight = FontWeight.Bold,
                                color = WarriorRed,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("📜 Catatan Tempur Anggota Guild:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GoldPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                raidState.logs.forEach { log ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant.copy(alpha = 0.6f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(log.characterClass.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    log.characterName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = log.characterClass.themeColor
                                )
                                Text(
                                    log.actionText,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                            if (log.damageDealt > 0) {
                                Text(
                                    "-${log.damageDealt} DMG",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.sp,
                                    color = WarriorRed
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (raidState.isVictory) GoldPrimary.copy(alpha = 0.2f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (raidState.isVictory) GoldPrimary else CardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (raidState.isVictory) "🏆 SERANGAN RAID BERHASIL!" else "⚔️ RAID SELESAI DENGAN PERLAWANAN SENGSARA!",
                            fontWeight = FontWeight.Bold,
                            color = if (raidState.isVictory) GoldPrimary else TextPrimary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Memperoleh +${raidState.totalRaidPointsGained} Poin Raid untuk ${raidState.guild.name}!",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianBg)
            ) {
                Text("Tutup", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = DarkSurface
    )
}
