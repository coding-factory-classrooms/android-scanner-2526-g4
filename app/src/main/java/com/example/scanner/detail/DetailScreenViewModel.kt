package com.example.scanner.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ApiService
import com.example.scanner.ApiService.fetchAllCards
import com.example.scanner.Card
import com.example.scanner.DbService
import com.example.scanner.OwnedCard
import io.paperdb.Paper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val card: Card) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

private const val DB_KEY = "cards"

class DetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadCard(cardId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val apiCard = ApiService.getCardById(cardId)
            val ownedCards = Paper.book().read(DB_KEY, emptyMap<Int, OwnedCard>())!!.toMutableMap()

            val ownedCard = ownedCards[cardId]

            // Fusionner les infos API + DB (surtout pour les fav)
            val mergedCard = apiCard.copy(
                isFavorite = ownedCard?.isFavorite,
                isOwned = ownedCard != null,
                count = ownedCard?.count ?: 0,
                acquisitionDate = ownedCard?.acquisitionDate
            )

            _uiState.value = DetailUiState.Success(mergedCard)
        }
    }


    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is DetailUiState.Success) {
            val card = currentState.card
            println(card)
            val newFavorite = !(card.isFavorite ?: false)

            println(newFavorite)

            viewModelScope.launch {
                // dbService.setFavorite(card.id, newFavorite)
                val ownedCards = Paper.book().read(DB_KEY, emptyMap<Int, OwnedCard>())!!.toMutableMap()

                val cardToEdit = ownedCards[card.id]
                println(cardToEdit)
                val updatedCard = cardToEdit!!.copy(isFavorite = newFavorite)
                println(updatedCard)

                ownedCards[card.id] = updatedCard

                Paper.book().write(DB_KEY, ownedCards)

                println(card.isFavorite)

                loadCard(card.id)
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            DbService().deleteOwnedCard(cardId)
        }
    }
}