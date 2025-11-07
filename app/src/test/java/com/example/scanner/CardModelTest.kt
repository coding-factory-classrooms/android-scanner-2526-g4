package com.example.scanner

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class CardModelTest {

@Test
fun `création de Card correct`(){
    val icon = IconUrls(medium = "url_medium.png", evolutionMedium = "url_evo.png")

// CRÉATION DE LA CARTE
    val card = Card(
        name = "Chevalier",
        id = 1,
        maxLevel = 14,
        elixirCost = 3,
        iconUrls = icon,
        rarity = "Common"
    )

//ON VA VÉRIFIER SI LES DONNÉES CORRESPONDE

    assertEquals("Chevalier", card.name)
    assertEquals(1, card.id)
    assertEquals(14, card.maxLevel)
    assertEquals(3, card.elixirCost)
    assertEquals("Common", card.rarity)
    assertEquals("url_medium.png", card.iconUrls.medium)
    assertEquals("url_evo.png", card.iconUrls.evolutionMedium)

    assertFalse(card.isOwned ?: true)
    assertEquals(0, card.count)
    assertFalse(card.isFavorite ?: true)
    assertNull(card.acquisitionDate)
    }
}

