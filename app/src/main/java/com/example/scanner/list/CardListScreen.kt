package com.example.scanner.list

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import com.example.scanner.Card
import com.example.scanner.DbService
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.runBlocking


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(vm: CardListViewModel= viewModel()) {
    val uiState by vm.uiStateFlow.collectAsState()
    val context = LocalContext.current
    val dbService = DbService();

    val qrCodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            runBlocking {
                dbService.saveCardId(result.contents.toInt())
            }
            Toast.makeText(context, "Scann Réussi", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Annulé", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        vm.loadCards()
    }

    val titleText = when (val state = uiState) {
        is CardListUiState.Success -> {
            "Collection (${state.ownedCount}/${state.totalCount})"
        }
        else -> "Collection"
    }

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text(titleText)
            }
        )
    },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                FloatingActionButton(
                    onClick = {
                        runBlocking {
                            vm.createFakeCard("Legendary")
                        }
                    },
                    containerColor = Color.Red,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Deuxième Action")
                }

                FloatingActionButton(
                    onClick = {
                        val options = ScanOptions()
                        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                        options.setPrompt("Scan a QRCode")
                        options.setCameraId(0)
                        options.setBeepEnabled(true)
                        options.setBarcodeImageEnabled(true)
                        qrCodeLauncher.launch(ScanOptions())
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Ajouter")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End

    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CardListBody(uiState)
        }
    }
}

@Composable
fun CardListBody(state: CardListUiState) {
    var query by remember {mutableStateOf("")}
    when(state) {
        is CardListUiState.Failure -> Text(state.message)
        CardListUiState.Loading -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        is CardListUiState.Success -> {
            Column(modifier = Modifier.fillMaxSize()) {

                // Barre de recherche
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it }, // met à jour query
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = {Text("Rechercher une carte")},
                    singleLine = true
                )

                // filtre des cartes
                val filteredCards = state.cards.filter {
                    it.name.contains(query, ignoreCase = true)
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(80.dp),
                    modifier = Modifier.padding(4.dp)
                ) {
                    items(filteredCards) { card ->
                        CardItems(card) }
                }
            }
        }
    }
}

@Composable
fun CardItems(card: Card) {
    val colorFilter = if (card.isOwned) {
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
        colorFilter = colorFilter
    )
}