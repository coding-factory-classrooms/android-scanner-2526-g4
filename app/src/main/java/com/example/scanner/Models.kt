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
    val isUnlock: Boolean? = false
)