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

     fun loadCards() {
         viewModelScope.launch {
             try {
                 uiStateFlow.value = CardListUiState.Loading

                 val db = DbService()
                 val cardListResponse = ApiService.fetchAllCards().items
                 val ownedCards = db.getAllCard()

                 val finalCards = cardListResponse.map { apiCard ->
                 val ownedCard = ownedCards[apiCard.id]

                     if (ownedCard != null) {
                         apiCard.copy(
                             isOwned = true,
                             count = 1,
                             isFavorite = false
                         )
                     } else {
                         apiCard.copy(
                             isOwned = false,
                             count = 0,
                             isFavorite = false
                         )
                     }
                 }
                 uiStateFlow.value = CardListUiState.Success(finalCards)

             } catch (e: Exception) {
                 println("ERROR: ${e.message}")
                 e.printStackTrace()
                 uiStateFlow.value = CardListUiState.Failure(e.message ?: "Erreur inconnue")
             }
         }
     }
}