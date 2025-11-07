package com.example.scanner.list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

