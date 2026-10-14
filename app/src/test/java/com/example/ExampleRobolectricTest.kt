package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CharacterActionLogEntity
import com.example.data.model.CharacterClass
import com.example.data.model.CharacterEntity
import com.example.data.model.ShopItemEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Text RPG Guilds", appName)
  }

  @Test
  fun `verify character class stats and combat power`() {
    val warrior = CharacterEntity(
        name = "Ksatria Uji",
        characterClass = CharacterClass.WARRIOR,
        hp = 350,
        maxHp = 350,
        mp = 60,
        maxMp = 60,
        atk = 48,
        def = 35,
        spd = 22,
        crit = 12,
        bio = "Bio pengujian"
    )

    assertTrue(warrior.combatPower > 0)
    assertEquals(CharacterClass.WARRIOR, warrior.characterClass)
    assertEquals("Warrior", warrior.characterClass.title)
  }

  @Test
  fun `verify character action log structure`() {
    val log = CharacterActionLogEntity(
        characterId = 1L,
        actionType = "HUNT",
        location = "Hutan Goblin Lumut",
        title = "Inisiatif Berburu Mandiri",
        decisionReason = "Merasa fit dan ingin berlatih.",
        narrative = "Mengalahkan Goblin Pengintai.",
        expGained = 45,
        goldGained = 50,
        isLevelUp = true
    )

    assertEquals("HUNT", log.actionType)
    assertEquals(1L, log.characterId)
    assertTrue(log.isLevelUp)
    assertEquals(45, log.expGained)
  }

  @Test
  fun `verify shop item entity and fixed items`() {
    val hpPotion = ShopItemEntity(
        name = "Potion Darah Merah",
        itemType = "POTION_HP",
        iconEmoji = "🧪",
        price = 35,
        bonusHp = 150,
        description = "Ramuan penyembuh.",
        isFixed = true
    )

    val customSword = ShopItemEntity(
        name = "Pedang Api Sakti",
        itemType = "WEAPON",
        compatibleClass = CharacterClass.WARRIOR,
        iconEmoji = "⚔️",
        price = 150,
        bonusAtk = 25,
        bonusDef = 10,
        description = "Pedang tempaan khusus.",
        isFixed = false
    )

    assertTrue(hpPotion.isFixed)
    assertEquals("POTION_HP", hpPotion.itemType)
    assertEquals(35, hpPotion.price)

    assertEquals(false, customSword.isFixed)
    assertEquals(25, customSword.bonusAtk)
    assertTrue(customSword.totalPowerBonus > 0)
  }
}
