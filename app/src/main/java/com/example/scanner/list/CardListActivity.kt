package com.example.scanner.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scanner.OwnedCard
import com.example.scanner.ui.theme.ScannerTheme
import io.paperdb.Paper
import java.util.Date

private const val DB_KEY = "cards"

class CardListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Paper.init(this)
        // initializeTestData()
        setContent {
            ScannerTheme {
                CardListScreen()
            }
        }
    }
    private fun initializeTestData() {
        val currentCardsMap = Paper.book().read<MutableMap<Int, OwnedCard>>(DB_KEY) ?: mutableMapOf()

        // N'ajouter les données de test que si la DB est vide
        if (currentCardsMap.isEmpty()) {
            val knightId = 26000000 // Knight
            val archersId = 26000001 // Archers

            val ownedKnight = OwnedCard(
                cardId = knightId,
                count = 5,
                acquisitionDate = Date().time,
                isFavorite = true
            )
            currentCardsMap[knightId] = ownedKnight

            val ownedArchers = OwnedCard(
                cardId = archersId,
                count = 2,
                acquisitionDate = Date().time,
                isFavorite = false
            )
            currentCardsMap[archersId] = ownedArchers

            Paper.book().write(DB_KEY, currentCardsMap)
        }
    }
}

