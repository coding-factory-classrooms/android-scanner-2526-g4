package com.example.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scanner.ui.theme.ScannerTheme
import io.paperdb.Paper


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Paper.init(this)
        setContent {
            ScannerTheme {
                // 26000001 c'est l'id d'une des cartes, oui c'est chelou me demandez pas pk
                ApiScreen(cardId = 26000001)
            }
        }
    }
}

