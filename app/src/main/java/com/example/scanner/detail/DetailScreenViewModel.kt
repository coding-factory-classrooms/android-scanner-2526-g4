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
            println("🔄 [DetailViewModel] Chargement de la carte ID=$cardId ...")
            _uiState.value = DetailUiState.Loading

            val card = ApiService.getCardById(cardId)

            if (card != null) {
                println("🔄 [DetailViewModel]=$cardId")
                _uiState.value = DetailUiState.Success(card)
            } else {
                println("🔄 [DetailViewModel] Chargement de la carte ID=$cardId ...")
                _uiState.value = DetailUiState.Error("Carte non trouvée")
            }
        }
    }
}