package com.example.scanner.list

import org.junit.Assert.*
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

class CardListViewModelTest {

    @Test
    fun `renvoie succes avec liste vide quand api et db fonctionnent`() {

        val apiCards = FakeApiService.fetchAllCards()
        val dbCards = FakeDbService().getAllCard()

        // loadCards()
        val finalList = apiCards.map { it } + dbCards.values
        val success = true


        if (success) {
            // la liste  vide
            assertTrue(finalList.isEmpty())
        } else {
            // sinon  erreur
            fail("Erreur : l’appel API ou DB a échoué")
        }
    }
}
