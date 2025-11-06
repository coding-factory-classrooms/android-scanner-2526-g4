package com.example.scanner

import kotlinx.serialization.Serializable


// Besoin d'une class que pour les icons car il peux y avoir plusieurs type d'icons
@Serializable
data class IconUrls(
    val medium: String,
    val evolutionMedium: String? = null
)


@Serializable
data class CardListResponse(
    val items: List<Card>
)

@Serializable
data class Card(
    val name: String,
    val id: Int,
    val maxLevel: Int,
    val elixirCost: Int? = null,
    val iconUrls: IconUrls,
    val rarity: String,
    val isOwned: Boolean = false,
    val count: Int = 0,
    val isFavorite: Boolean = false,
    val acquisitionDate: Long? = null,
)

data class OwnedCard(
    val cardId: Int,
    val count: Int = 0,
    val acquisitionDate: Long? = null,
    val isFavorite: Boolean = false,
)

data class Chest(
    val common: Int,
    val rare: Int,
    val epic: Int,
    val legendary: Int,
    val hero: Int
)

object Chests {
    val Wooden = Chest(
        common = 16,
        rare = 3,
        epic = 1,
        legendary = 0,
        hero = 0
    )

    val Silver = Chest(
        common = 50,
        rare = 30,
        epic = 19,
        legendary = 1,
        hero = 0
    )

    val Gold = Chest(
        common = 30,
        rare = 40,
        epic = 25,
        legendary = 4,
        hero = 1
    )

    val Epic = Chest(
        common = 0,
        rare = 0,
        epic = 1,
        legendary = 0,
        hero = 0
    )

    val Giant = Chest(
        common = 0,
        rare = 10,
        epic = 4,
        legendary = 5,
        hero = 1
    )

    val Magic = Chest(
        common = 0,
        rare = 4,
        epic = 10,
        legendary = 5,
        hero = 1
    )

    val Legendary = Chest(
        common = 0,
        rare = 0,
        epic = 0,
        legendary = 1,
        hero = 0
    )

    val Lucky = Chest(
        common = 1,
        rare = 1,
        epic = 1,
        legendary = 1,
        hero = 1
    )

    val chestMap = mapOf(
        "Wooden" to Wooden,
        "Silver" to Silver,
        "Gold" to Gold,
        "Epic" to Epic,
        "Giant" to Giant,
        "Magic" to Magic,
        "Legendary" to Legendary,
        "Lucky" to Lucky
    )
}