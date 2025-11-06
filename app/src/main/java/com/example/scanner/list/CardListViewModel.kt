package com.example.scanner.list

import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.lifecycle.ViewModel
import com.example.scanner.ApiService
import com.example.scanner.Card
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.MutableStateFlow

sealed class CardListUiState {
    data object Loading : CardListUiState()
    data class Success(val cards: List<Card>) : CardListUiState()
    data class Failure(val message: String) : CardListUiState()
}

class CardListViewModel : ViewModel() {
    val uiStateFlow = MutableStateFlow<CardListUiState>(CardListUiState.Loading)

    val cardsFlow = MutableStateFlow(emptyList<Card>())

    suspend fun loadCards() {

        uiStateFlow.value = CardListUiState.Loading

        val cardListResponse = ApiService.fetchAllCards()
        println(cardListResponse.items)
        uiStateFlow.value = CardListUiState.Success(cardListResponse.items)
    }
}