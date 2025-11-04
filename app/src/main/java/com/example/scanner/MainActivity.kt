package com.example.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.scanner.ui.theme.ScannerTheme
import coil3.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScannerTheme {
                // 26000001 c'est l'id d'une des cartes, oui c'est chelou me demandez pas pk
                ApiScreen(cardId = 26000001)
            }
        }
    }
}


// Faudra passer tout ça dans un ViewModel et refaire bien les UiStates comme dans le cours
// là j'ai fait ça rapidement juste pour voir si ça fonctionnait

@Composable
fun ApiScreen(cardId: Int) {
    var card by remember { mutableStateOf<Card?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect exécute l'appel API une seule fois au démarrage du composable, sinn ça fait des requetes à l'infini askip (j'ai pas tout compris la dessus)
    LaunchedEffect(Unit) {
        try {
            // Appel API
            val fetchedCard = ApiService.getCardById(cardId)
            card = fetchedCard
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
                    Text("Card ID: ${card!!.id}")
                    Spacer(Modifier.height(height = 32.dp))
                    Text(card!!.name)
                    Spacer(Modifier.height(height = 32.dp))
                    // Pour l'instant l'image ne charge pas jsp pourquoi, ptet essayer une autre lib -> picasso
                    AsyncImage(
                        model = card!!.iconUrls.medium,
                        contentDescription = card!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                            .background(Color.Gray),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}