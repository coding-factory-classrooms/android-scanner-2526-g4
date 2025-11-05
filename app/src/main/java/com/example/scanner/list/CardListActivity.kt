package com.example.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.scanner.list.CardListScreen
import com.example.scanner.ui.theme.ScannerTheme
import io.paperdb.Paper


class CardListActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Paper.init(this)
        setContent {
            ScannerTheme {
                CardListScreen()
            }
        }
    }
}

