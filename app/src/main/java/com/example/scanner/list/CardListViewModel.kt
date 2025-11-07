package com.example.scanner.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ApiService.fetchAllCards
import com.example.scanner.ApiService.getCardById
import com.example.scanner.Card
import com.example.scanner.Chest
import com.example.scanner.Chests
import com.example.scanner.DbService
import com.example.scanner.OwnedCard
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.collections.map

sealed class CardListUiState {
    data object Loading : CardListUiState()
    data class Success(
        val cards: List<Card>,
        val totalCount: Int,
        val ownedCount: Int
    ) : CardListUiState()
    data class Failure(val message: String) : CardListUiState()
}

private const val DB_KEY = "cards"

class CardListViewModel : ViewModel() {
    val uiStateFlow = MutableStateFlow<CardListUiState>(CardListUiState.Loading)
    val scannedCards = MutableStateFlow<List<Card>>(emptyList()) // pour la popup
    val bShowPopup = MutableStateFlow(false)

    fun loadCards() {
        viewModelScope.launch {
            uiStateFlow.value = CardListUiState.Loading
            val apiCards = fetchAllCards().items
            val ownedCards = getOwnedCardsFromDb()

            val totalCount = apiCards.size
            val ownedCount = ownedCards.size

            val finalCards = apiCards.map { card ->
                val owned = ownedCards[card.id]
                card.copy(
                    isOwned = owned != null,
                    count = owned?.count ?: 0,
                    isFavorite = owned?.isFavorite ?: false
                )
            }
            uiStateFlow.value = CardListUiState.Success(finalCards, totalCount, ownedCount)
        }
    }

    private fun getOwnedCardsFromDb(): MutableMap<Int, OwnedCard> {
        return Paper.book().read(DB_KEY, emptyMap<Int, OwnedCard>())!!.toMutableMap()
    }

    suspend fun handleScanned(chest: String) {
        createFakeCard(chest)
    }

    suspend fun createFakeCard(chest: String) {
        val randomIdCard = getRandomCards(Chests.chestMap[chest])

        val fakeScannedCard = getCardById(randomIdCard)
        val currentCardsMap = Paper.book().read<MutableMap<Int, OwnedCard>>(DB_KEY) ?: mutableMapOf()

        val existingCard = currentCardsMap[fakeScannedCard.id]

        println(fakeScannedCard)

        if (existingCard != null) {
            val updatedCard = existingCard.copy(
                count = existingCard.count?.plus(1)
            )
            currentCardsMap[fakeScannedCard.id] = updatedCard
        } else {
            val newCard = OwnedCard(
                cardId = fakeScannedCard.id,
                count = 1,
                acquisitionDate = Date().time,
                isFavorite = false
            )
            currentCardsMap[fakeScannedCard.id] = newCard
        }
        Paper.book().write(DB_KEY, currentCardsMap)
        scannedCards.value = emptyList()
        scannedCards.value += fakeScannedCard
        bShowPopup.value = true
        loadCards()
    }

    suspend fun getRandomCards(chest: Chest?): Int {
        val allCards = fetchAllCards()

        val commonCards = allCards.items.filter { card -> card.rarity == "common" }
        val rareCards = allCards.items.filter { card -> card.rarity == "rare" }
        val epicCards = allCards.items.filter { card -> card.rarity == "epic" }
        val legendaryCards = allCards.items.filter { card -> card.rarity == "legendary" }
        val championCards = allCards.items.filter { card -> card.rarity == "champion" }

        val chest = buildList {
            repeat(chest!!.common) { add(commonCards.random()) }
            repeat(chest.rare) { add(rareCards.random()) }
            repeat(chest.epic) { add(epicCards.random()) }
            repeat(chest.legendary) { add(legendaryCards.random()) }
            repeat(chest.champion) { add(championCards.random()) }
        }

        val card = chest.random()

        return card.id
    }
}