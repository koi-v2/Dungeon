package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.CharacterEntity
import com.example.data.model.ShopItemEntity
import com.example.ui.RpgViewModel
import com.example.ui.components.CharacterAvatar
import com.example.ui.theme.ArcherGreen
import com.example.ui.theme.CardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    viewModel: RpgViewModel,
    onNavigateToAddWeapon: () -> Unit,
    onNavigateToCharacters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shopItems by viewModel.shopItems.collectAsStateWithLifecycle()
    val characters by viewModel.characters.collectAsStateWithLifecycle()
    val selectedCharacter by viewModel.selectedCharacter.collectAsStateWithLifecycle()
    val isAutonomousThinking by viewModel.isAutonomousThinking.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var isHeroDropdownExpanded by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<ShopItemEntity?>(null) }

    val fixedPotions = shopItems.filter { it.isFixed }
    val weaponItems = shopItems.filter { !it.isFixed }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddWeapon,
                containerColor = GoldPrimary,
                contentColor = ObsidianBg,
                modifier = Modifier.testTag("add_custom_weapon_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Senjata")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tempa Senjata Baru", fontWeight = FontWeight.Bold)
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
            // Header: Toko Banner & Active Buyer Selector
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🏪 Toko Senjata & Alkimia",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Beli ramuan, tempa senjata kustom, atau biarkan pahlawan belanja mandiri!",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedCharacter == null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = DarkSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚠️ Belum ada hero aktif", fontSize = 12.sp, color = GoldLight)
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = onNavigateToCharacters) {
                                    Text("Pilih Hero", color = GoldPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        val activeHero = selectedCharacter!!

                        // Active Hero Status Card in Shop
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CharacterAvatar(
                                        avatarUri = activeHero.avatarUri,
                                        characterClass = activeHero.characterClass,
                                        size = 46.dp
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = activeHero.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TextPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "Lv.${activeHero.level} ${activeHero.characterClass.title}",
                                                fontSize = 10.sp,
                                                color = activeHero.characterClass.themeColor,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Text(
                                            text = "Senjata: ${activeHero.equippedWeaponIcon ?: "⚔️"} ${activeHero.equippedWeaponName ?: "Bawaan Dasar"}",
                                            fontSize = 11.sp,
                                            color = GoldLight,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = "Stok: 🧪 ${activeHero.potions} HP • 💧 ${activeHero.manaPotions} MP",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    // Saldo Emas
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = GoldPrimary.copy(alpha = 0.2f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = "💰 ${activeHero.gold} G",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = GoldPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Tombol Karakter Mikir Mau Beli Apa
                                Button(
                                    onClick = {
                                        viewModel.triggerAutonomousShopping(activeHero.id) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("🧠 ${activeHero.name} telah selesai menimbang dan berbelanja mandiri di Toko!")
                                            }
                                        }
                                    },
                                    enabled = !isAutonomousThinking,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = ObsidianBg
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(42.dp)
                                        .testTag("trigger_autonomous_shopping_button")
                                ) {
                                    if (isAutonomousThinking) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = ObsidianBg
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Sedang Menimbang Pilihan di Toko...", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("🧠 Suruh ${activeHero.name} Mikir Sendiri Mau Beli Apa", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // List of Shop Items: Fixed Potions + Custom Weapons
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 14.dp, bottom = 85.dp)
            ) {
                // Section 1: Fixed Potions (Permanen / Tidak Bisa Dihapus)
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🧪 Ramuan Alkimia Resmi Toko",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = DarkSurfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Permanen", fontSize = 9.sp, color = TextMuted)
                            }
                        }
                    }
                }

                items(fixedPotions, key = { it.id }) { potion ->
                    val canAfford = selectedCharacter != null && selectedCharacter!!.gold >= potion.price
                    ShopPotionCard(
                        item = potion,
                        canAfford = canAfford,
                        onBuy = {
                            selectedCharacter?.let { hero ->
                                viewModel.buyShopItem(hero.id, potion.id) { success ->
                                    if (success) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Berhasil membeli ${potion.name}!")
                                        }
                                    }
                                }
                            }
                        }
                    )
                }

                // Section 2: Weapons Section
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚔️ Etalase Senjata (${weaponItems.size})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldPrimary
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "+ Tempa Senjata",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldLight,
                            modifier = Modifier.clickable { onNavigateToAddWeapon() }
                        )
                    }
                }

                if (weaponItems.isEmpty()) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🗡️", fontSize = 32.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Belum Ada Senjata Kustom",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Tekan tombol Tempa Senjata untuk menambahkan senjata kustom impianmu bersama statnya!",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    items(weaponItems, key = { it.id }) { weapon ->
                        val hero = selectedCharacter
                        val isEquipped = hero?.equippedWeaponName == weapon.name
                        val canAfford = hero != null && hero.gold >= weapon.price
                        val isCompatible = hero == null || weapon.compatibleClass == null || weapon.compatibleClass == hero.characterClass

                        ShopWeaponCard(
                            weapon = weapon,
                            isEquipped = isEquipped,
                            canAfford = canAfford,
                            isCompatible = isCompatible,
                            onBuy = {
                                hero?.let {
                                    viewModel.buyShopItem(it.id, weapon.id) { success ->
                                        if (success) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Berhasil membeli dan melengkapi ${weapon.name}!")
                                            }
                                        }
                                    }
                                }
                            },
                            onDelete = { itemToDelete = weapon }
                        )
                    }
                }
            }
        }
    }

    // Delete Custom Weapon Dialog
    itemToDelete?.let { weapon ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Hapus Senjata dari Toko?", fontWeight = FontWeight.Bold) },
            text = { Text("Senjata \"${weapon.name}\" akan dihapus dari etalase toko. Pahlawan tidak akan bisa membelinya lagi.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCustomShopItem(weapon.id)
                        itemToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarriorRed)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Batal")
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
fun ShopPotionCard(
    item: ShopItemEntity,
    canAfford: Boolean,
    onBuy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(item.iconEmoji, fontSize = 24.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = "Item Toko Resmi",
                        tint = GoldLight.copy(alpha = 0.7f),
                        modifier = Modifier.size(12.dp)
                    )
                }
                Text(
                    text = item.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onBuy,
                enabled = canAfford,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = ObsidianBg
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("buy_potion_${item.id}")
            ) {
                Text(
                    text = "💰 ${item.price} G",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun ShopWeaponCard(
    weapon: ShopItemEntity,
    isEquipped: Boolean,
    canAfford: Boolean,
    isCompatible: Boolean,
    onBuy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(
            if (isEquipped) 2.dp else 1.dp,
            if (isEquipped) GoldPrimary else CardBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Icon, Name, Class Badge, Delete button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, CardBorder, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(weapon.iconEmoji, fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = weapon.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )

                        if (isEquipped) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldPrimary
                            ) {
                                Text(
                                    text = "DIPAKAI",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = ObsidianBg,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val cls = weapon.compatibleClass
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = cls?.themeColor?.copy(alpha = 0.2f) ?: GoldLight.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = cls?.let { "${it.iconEmoji} Khusus ${it.title}" } ?: "🌟 Semua Kelas",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = cls?.themeColor ?: GoldLight,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                            )
                        }

                        if (!isCompatible) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "(Kelas tidak cocok)",
                                fontSize = 10.sp,
                                color = WarriorRed
                            )
                        }
                    }
                }

                // Delete custom weapon option
                if (!weapon.isFixed) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Hapus Senjata",
                            tint = TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stat Summary Chip
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = DarkSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚡ Bonus: ${weapon.statsSummary}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldLight,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            if (weapon.description.isNotBlank()) {
                Text(
                    text = weapon.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer: Price and Buy Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Harga: 💰 ${weapon.price} Koin Emas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GoldPrimary
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = onBuy,
                    enabled = canAfford && isCompatible && !isEquipped,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = ObsidianBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("buy_weapon_${weapon.id}")
                ) {
                    if (isEquipped) {
                        Text("Terpasang", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("Beli & Lengkapi", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
