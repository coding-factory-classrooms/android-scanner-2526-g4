package com.example.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.paperdb.Paper


@Composable
fun ApiScreen(cardId: Int) {
    val paperKey = "card_${cardId}"

    var card by remember { mutableStateOf<Card?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }


    // LaunchedEffect exécute l'appel API une seule fois au démarrage du composable, sinn ça fait des requetes à l'infini askip (j'ai pas tout compris la dessus)
    LaunchedEffect(Unit) {
        try {
            // Appel API
            val fetchedCard = ApiService.getCardById(cardId)
            card = fetchedCard
            Paper.book().write(paperKey, fetchedCard)
            println(Paper.book().read(paperKey))
            println("REPERE")


            card = Paper.book().read(paperKey)


        } catch (e: Exception) {
            error = "Erreur de chargement: ${e.localizedMessage}"
        } finally {
            isLoading = false
        }
    }



    // ça j'explique pas c'est ce qu'on fait depuis 2 jours
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }
                error != null -> {
                    Text(
                        text = "Erreur: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                card != null -> {
                    //Text("Card ID: ${card!!.id}")
                    Spacer(Modifier.height(height = 32.dp))
                    Text(card!!.name)
                    Spacer(Modifier.height(height = 32.dp))
                    AsyncImage(
                        model = card!!.iconUrls.medium,
                        contentDescription = card!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}