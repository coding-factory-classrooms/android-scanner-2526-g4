package com.example.scanner.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import coil3.compose.AsyncImage
import com.example.scanner.Card


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(vm: CardListViewModel= viewModel()) {
    val cards by vm.cardsFlow.collectAsState()
    val uiState by vm.uiStateFlow.collectAsState()

    LaunchedEffect(Unit) {
        vm.loadCards()
    }
    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.primary,
            ),
            title = {
                Text("Collection")
            }
        )
    },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            CardListBody(uiState)
        }
    }
}

@Composable
fun CardListBody(state: CardListUiState) {
    when(state) {
        is CardListUiState.Failure -> Text(state.message)
        CardListUiState.Loading -> CircularProgressIndicator()
        is CardListUiState.Success -> LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
        ) {
            items(state.cards) { card ->
                CardItems(card)
            }
        }
    }
}

@Composable
fun CardItems(card: Card) {
    val grayScaleMatrix = ColorMatrix().apply {
        setToSaturation(0f)
    }
    val grayScaleColorFilter = ColorFilter.colorMatrix(grayScaleMatrix)

    AsyncImage(
        model = card.iconUrls.medium,
        contentDescription = card.name,
        colorFilter = grayScaleColorFilter
    )
}