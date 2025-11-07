package com.example.scanner.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ApiService
import com.example.scanner.Card
import com.example.scanner.DbService
import com.example.scanner.OwnedCard
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class CardListUiState {
    data object Loading : CardListUiState()
    data class Success(val cards: List<Card>) : CardListUiState()
    data class Failure(val message: String) : CardListUiState()
}

class CardListViewModel : ViewModel() {
    val uiStateFlow = MutableStateFlow<CardListUiState>(CardListUiState.Loading)
    val scannedCards = MutableStateFlow<List<Card>>(emptyList()) // pour la popup
    val bShowPopup = MutableStateFlow(false)

    fun loadCards() {
        viewModelScope.launch {
            uiStateFlow.value = CardListUiState.Loading
            val apiCards = ApiService.fetchAllCards().items
            val ownedCards = DbService().getAllCard()

            val finalCards = apiCards.map { card ->
                val owned = ownedCards[card.id]
                card.copy(
                    isOwned = owned != null,
                    count = owned?.count ?: 0,
                    isFavorite = owned?.isFavorite ?: false
                )
            }
            uiStateFlow.value = CardListUiState.Success(finalCards)
        }
    }


    fun handleScannedIds(ids: List<Int>) {
        viewModelScope.launch {
            val db = DbService()
            val cards = mutableListOf<Card>()
            ids.forEach { id ->
                try {
                    val card = ApiService.getCardById(id)
                    db.saveCardId(id)
                    cards.add(card)
                } catch (e: Exception) {
                    println("Carte $id non trouvée : ${e.message}")
                }
            }
            scannedCards.value = cards
            bShowPopup.value = true
            loadCards() // recharge les cartes après l'/les avoir ajoutée(s)
        }
    }
}
