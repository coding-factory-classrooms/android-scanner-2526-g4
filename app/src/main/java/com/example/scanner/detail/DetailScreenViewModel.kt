package com.example.scanner.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanner.ApiService
import com.example.scanner.Card
import com.example.scanner.DbService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DetailUiState {
    object Loading : DetailUiState()
    data class Success(val card: Card) : DetailUiState()
    data class Error(val message: String) : DetailUiState()
}

class DetailViewModel : ViewModel() {

    private val dbService = DbService()

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState

    fun loadCard(cardId: Int) {
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val apiCard = ApiService.getCardById(cardId)
            val ownedCard = dbService.getOwnedCardId(cardId)

            // Fusionner les infos API + DB (surtout pour les fav)
            val mergedCard = apiCard.copy(
                isFavorite = ownedCard?.isFavorite ?: false,
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
            val newFavorite = !(card.isFavorite ?: false)

            viewModelScope.launch {
                dbService.setFavorite(card.id, newFavorite)

                // Recharge la carte pour l’UI
                val updatedCard = card.copy(isFavorite = newFavorite)
                _uiState.value = DetailUiState.Success(updatedCard)
            }
        }
    }

    fun deleteCard(cardId: Int) {
        viewModelScope.launch {
            DbService().deleteOwnedCard(cardId)
        }
    }
}