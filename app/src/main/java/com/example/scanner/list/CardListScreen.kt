package com.example.scanner.list

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.example.scanner.Card
import com.example.scanner.detail.DetailActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(vm: CardListViewModel = viewModel()) {
    val uiState by vm.uiStateFlow.collectAsState()
    val scannedCards by vm.scannedCards.collectAsState()
    val bShowPopup by vm.bShowPopup.collectAsState()
    val context = LocalContext.current

    val qrCodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            val ids = result.contents.split(",").mapNotNull { it.toIntOrNull() }
            vm.handleScannedIds(ids) // déléguer au ViewModel
            Toast.makeText(context, "Scan réussi", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Annulé", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        vm.loadCards()
    }

    Scaffold(
        topBar = { TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Collection")
            }
        ) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                val options = ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Scan un QR Code")
                    setBeepEnabled(true)
                    setBarcodeImageEnabled(true)
                    setOrientationLocked(false)
                }
                qrCodeLauncher.launch(options)
            }) { Icon(Icons.Default.Add, contentDescription = "Ajouter") }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CardListBody(uiState)
        }
    }

    if (bShowPopup) {
        AlertDialog(
            onDismissRequest = { vm.bShowPopup.value = false },
            title = { Text("Carte débloquée") },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    scannedCards.forEach { card ->
                        Text(card.name)
                        AsyncImage(
                            model = card.iconUrls.medium,
                            contentDescription = card.name,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { vm.bShowPopup.value = false }) {
                    Text("Fermer")
                }
            }
        )
    }
}

@Composable
fun CardListBody(state: CardListUiState) {
    var query by remember { mutableStateOf("") }
    var onlyFavorites by remember { mutableStateOf(false) }

    when (state) {
        is CardListUiState.Failure -> Text(state.message)
        CardListUiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        is CardListUiState.Success -> {
            val filteredCards = state.cards.filter {
                it.name.contains(query, ignoreCase = true) &&
                        (!onlyFavorites || it.isFavorite == true)
            }

            Column(modifier = Modifier.fillMaxSize()) {
                // Barre de recherche + cœur
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("Rechercher une carte") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { onlyFavorites = !onlyFavorites }) {
                        Icon(
                            imageVector = if (onlyFavorites) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(80.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    items(filteredCards) { card ->
                        CardItems(card)
                    }
                }
            }
        }
    }
}


@Composable
fun CardItems(card: Card) {
    // a changer de place stp (au moi de ce soir)
    val context = LocalContext.current

    val colorFilter = if (card.isOwned == true) {
        null
    } else {
        val grayScaleMatrix = ColorMatrix().apply {
            setToSaturation(0f)
        }
        ColorFilter.colorMatrix(grayScaleMatrix)
    }

    AsyncImage(
        model = card.iconUrls.medium,
        contentDescription = card.name,
        colorFilter = colorFilter,
        modifier = Modifier
            .padding(4.dp)
            .size(100.dp)
            .clickable {
                if(card.isOwned == true){
                    val intent = Intent(context, DetailActivity::class.java)
                    intent.putExtra("card_id", card.id)
                    context.startActivity(intent)
                }else {
                    Toast.makeText(context, "Débloque la carte avant stp enft", Toast.LENGTH_LONG).show()
                }
            }
    )
}