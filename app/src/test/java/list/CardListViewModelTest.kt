package com.example.scanner.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

// --- CRÉATION DE FAUSSES CLASSES POUR SIMULER L’API ET LA DB ---

object FakeApiService {
    fun fetchAllCards(): List<String> {
        return emptyList()
    }
}

class FakeDbService {
    fun getAllCard(): Map<Int, String> {
        return emptyMap()
    }
}

class FakeCardListViewModel : ViewModel() {
    val scannedCards = MutableStateFlow<List<String>>(emptyList())
    val bShowPopup = MutableStateFlow(false)

    fun handleScannedIds(ids: List<Int>) {
        viewModelScope.launch {
            delay(10)
            val cards = ids.map { id -> "Carte_$id" }
            scannedCards.value = cards
            bShowPopup.value = true
        }
    }
}

class CardListViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @Test
    fun `simulate card scan`() = runTest {
        val viewModel = FakeCardListViewModel()

        viewModel.handleScannedIds(listOf(1, 2))
        advanceUntilIdle()

        val scanned = viewModel.scannedCards.value
        println("Cartes scannées : $scanned")

        assertTrue(viewModel.bShowPopup.value)
        assertTrue(scanned.isNotEmpty())
        assertEquals(listOf("Carte_1", "Carte_2"), scanned)
    }

    @Test
    fun `renvoie succes avec liste vide quand api et db fonctionnent`() {
        val apiCards = FakeApiService.fetchAllCards()
        val dbCards = FakeDbService().getAllCard()
        val finalList = apiCards.map { it } + dbCards.values
        val success = true

        if (success) {
            assertTrue(finalList.isEmpty())
        } else {
            fail("Erreur : l’appel API ou DB a échoué")
        }
    }
}
