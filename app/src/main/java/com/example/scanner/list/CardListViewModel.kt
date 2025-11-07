package com.example.scanner.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ApiService
import com.example.scanner.ApiService.fetchAllCards
import com.example.scanner.ApiService.getCardById
import com.example.scanner.Card
import com.example.scanner.Chest
import com.example.scanner.Chests
import com.example.scanner.OwnedCard
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

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

    private fun getOwnedCardsFromDb(): MutableMap<Int, OwnedCard> {
        return Paper.book().read(DB_KEY, emptyMap<Int, OwnedCard>())!!.toMutableMap()
    }

    fun loadCards() {
         viewModelScope.launch {
             uiStateFlow.value = CardListUiState.Loading

             val cardListResponse = ApiService.fetchAllCards().items
             val ownedCards = getOwnedCardsFromDb()

             val totalCount = cardListResponse.size
             val ownedCount = ownedCards.size

             val finalCards = cardListResponse.map { apiCard ->
                 val ownedCard = ownedCards[apiCard.id]

                 if (ownedCard != null) {
                     apiCard.copy(
                         isOwned = true,
                         count = ownedCard.count,
                         isFavorite = ownedCard.isFavorite
                     )
                 } else {
                     apiCard.copy(
                         isOwned = false,
                         count = 0,
                         isFavorite = false
                     )
                 }
             }
             uiStateFlow.value = CardListUiState.Success(finalCards, totalCount, ownedCount)
         }
     }

    suspend fun createFakeCard(chest: String) {
        val randomIdCard = getRandomCards(Chests.chestMap[chest])

        val fakeScannedCard = getCardById(randomIdCard)
        val currentCardsMap = Paper.book().read<MutableMap<Int, OwnedCard>>(DB_KEY) ?: mutableMapOf()

        val existingCard = currentCardsMap[fakeScannedCard.id]

        println(fakeScannedCard)

        if (existingCard != null) {
            val updatedCard = existingCard.copy(
                count = existingCard.count + 1
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
        //Paper.book().write(DB_KEY, currentCardsMap)
        //loadCards()
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