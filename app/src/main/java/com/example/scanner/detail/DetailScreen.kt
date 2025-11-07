package com.example.scanner.detail

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.scanner.Card
import com.example.scanner.list.CardListActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(cardId: Int, vm: DetailViewModel = viewModel()) {

    val context = LocalContext.current
    val uiState by vm.uiState.collectAsState()

    // Charger la carte une seule fois
    LaunchedEffect(cardId) {
        vm.loadCard(cardId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = { Text("Détail de la carte") },
                actions = {
                    val uiState = vm.uiState.collectAsState().value
                    if (uiState is DetailUiState.Success && uiState.card.isOwned == true) {
                        IconButton(onClick = {
                            vm.deleteCard(uiState.card.id)
                            Toast.makeText(
                                context,
                                "Carte supprimée",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(context, CardListActivity::class.java)
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Supprimer",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (uiState) {

                is DetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DetailUiState.Error -> {
                    Text(
                        text = (uiState as DetailUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is DetailUiState.Success -> {
                    val card = (uiState as DetailUiState.Success).card
                    CardDetailContent(card)
                }
            }
        }
    }
}

@Composable
fun CardDetailContent(card: Card, vm: DetailViewModel = viewModel()) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = card.iconUrls.medium,
            contentDescription = card.name,
            modifier = Modifier.size(300.dp)
        )
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { vm.toggleFavorite() }) {
                    Icon(
                        imageVector = if (card.isFavorite == true) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.Red
                    )
                }
            }
            Text(
                text = "Level 48 Super Rare",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(4) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Damage")
                        Text("9827398")
                    }
                }
            }
        }
    }
}