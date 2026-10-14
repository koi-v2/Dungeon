package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RpgViewModel
import com.example.ui.screens.AddWeaponScreen
import com.example.ui.screens.AdventureScreen
import com.example.ui.screens.CharactersScreen
import com.example.ui.screens.CreateCustomSkillScreen
import com.example.ui.screens.CreateGuildScreen
import com.example.ui.screens.CreateCharacterScreen
import com.example.ui.screens.EventScreen
import com.example.ui.screens.GuildsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SpawnEventMonsterScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class RpgTab(val title: String, val icon: @Composable () -> Unit) {
    ADVENTURE(
        title = "Petualang",
        icon = { Icon(Icons.Default.Shield, contentDescription = "Petualang") }
    ),
    CHARACTERS(
        title = "Karakter",
        icon = { Icon(Icons.Default.Person, contentDescription = "Karakter") }
    ),
    SHOP(
        title = "Toko",
        icon = { Icon(Icons.Default.Storefront, contentDescription = "Toko") }
    ),
    EVENT(
        title = "Event & Bos",
        icon = { Icon(Icons.Default.FlashOn, contentDescription = "Event & Bos") }
    ),
    GUILDS(
        title = "Guild",
        icon = { Icon(Icons.Default.Group, contentDescription = "Guild") }
    )
}

class MainActivity : ComponentActivity() {
    private val viewModel: RpgViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(viewModel: RpgViewModel) {
    var currentTab by rememberSaveable { mutableStateOf(RpgTab.ADVENTURE) }
    var isCreatingCharacter by rememberSaveable { mutableStateOf(false) }
    var isCreatingGuild by rememberSaveable { mutableStateOf(false) }
    var isAddingWeapon by rememberSaveable { mutableStateOf(false) }
    var isSpawningMonster by rememberSaveable { mutableStateOf(false) }
    var isCreatingSkill by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = isCreatingCharacter || isCreatingGuild || isAddingWeapon || isSpawningMonster || isCreatingSkill || currentTab != RpgTab.ADVENTURE) {
        when {
            isCreatingCharacter -> isCreatingCharacter = false
            isCreatingGuild -> isCreatingGuild = false
            isAddingWeapon -> isAddingWeapon = false
            isSpawningMonster -> isSpawningMonster = false
            isCreatingSkill -> isCreatingSkill = false
            currentTab != RpgTab.ADVENTURE -> currentTab = RpgTab.ADVENTURE
        }
    }

    if (isCreatingCharacter) {
        CreateCharacterScreen(
            viewModel = viewModel,
            onDismiss = { isCreatingCharacter = false }
        )
    } else if (isCreatingGuild) {
        CreateGuildScreen(
            viewModel = viewModel,
            onDismiss = { isCreatingGuild = false }
        )
    } else if (isAddingWeapon) {
        AddWeaponScreen(
            viewModel = viewModel,
            onDismiss = { isAddingWeapon = false }
        )
    } else if (isSpawningMonster) {
        SpawnEventMonsterScreen(
            viewModel = viewModel,
            onDismiss = { isSpawningMonster = false }
        )
    } else if (isCreatingSkill) {
        CreateCustomSkillScreen(
            viewModel = viewModel,
            onDismiss = { isCreatingSkill = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentTab) {
                                RpgTab.ADVENTURE -> "⚔️ Petualangan Teks"
                                RpgTab.CHARACTERS -> "👤 Kelola Karakter"
                                RpgTab.SHOP -> "🏪 Toko Senjata & Alkimia"
                                RpgTab.EVENT -> "🔥 Event Bos & Bengkel Skill"
                                RpgTab.GUILDS -> "🛡️ Markas Besar Guild"
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            color = GoldPrimary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = DarkSurface,
                        titleContentColor = GoldPrimary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 2.dp
                ) {
                    RpgTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = tab.icon,
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ObsidianBg,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = GoldPrimary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = GoldPrimary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            },
            containerColor = ObsidianBg
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentTab) {
                    RpgTab.ADVENTURE -> {
                        AdventureScreen(
                            viewModel = viewModel,
                            onNavigateToCharacters = { currentTab = RpgTab.CHARACTERS }
                        )
                    }

                    RpgTab.CHARACTERS -> {
                        CharactersScreen(
                            viewModel = viewModel,
                            onNavigateToCreate = {
                                viewModel.resetCharacterForm()
                                isCreatingCharacter = true
                            }
                        )
                    }

                    RpgTab.SHOP -> {
                        ShopScreen(
                            viewModel = viewModel,
                            onNavigateToAddWeapon = {
                                viewModel.resetWeaponForm()
                                isAddingWeapon = true
                            },
                            onNavigateToCharacters = { currentTab = RpgTab.CHARACTERS }
                        )
                    }

                    RpgTab.EVENT -> {
                        EventScreen(
                            viewModel = viewModel,
                            onNavigateToSpawnMonster = {
                                viewModel.resetMonsterSpawnForm()
                                isSpawningMonster = true
                            },
                            onNavigateToCreateSkill = {
                                viewModel.resetSkillCreateForm()
                                isCreatingSkill = true
                            },
                            onNavigateToAdventure = { currentTab = RpgTab.ADVENTURE }
                        )
                    }

                    RpgTab.GUILDS -> {
                        GuildsScreen(
                            viewModel = viewModel,
                            onNavigateToCreateGuild = {
                                viewModel.resetGuildForm()
                                isCreatingGuild = true
                            }
                        )
                    }
                }
            }
        }
    }
}
