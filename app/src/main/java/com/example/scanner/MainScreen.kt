package com.example.scanner

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val context = LocalContext.current
    var card by remember { mutableStateOf<Card?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val intent = Intent(context, ScannerActivity::class.java)
                    context.startActivity(intent)
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                error != null -> Text(
                    text = "Erreur: $error",
                    color = MaterialTheme.colorScheme.error
                )
                card != null -> {
                    Spacer(Modifier.height(32.dp))
                    Text(card!!.name)
                    Spacer(Modifier.height(32.dp))
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