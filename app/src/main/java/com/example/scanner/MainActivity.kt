package com.example.scanner

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import com.example.scanner.list.CardListScreen
import com.example.scanner.ui.theme.ScannerTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import io.paperdb.Paper


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // bdd init
        Paper.init(this)
        // perm cam
//       val qrCodeLauncher = registerForActivityResult<ScanOptions?, ScanIntentResult?>(
//            ScanContract(),
//            ActivityResultCallback { result: ScanIntentResult? ->
//                println("Résultat reçu HABITOX: ${result?.contents}")
//                if (result!!.getContents() == null) {
//                    Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(
//                        this@MainActivity,
//                        "Scanned: " + result.getContents(),
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            }
//        )
//
//        val options = ScanOptions()
//        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
//        options.setPrompt("Scan a barcode")
//        options.setCameraId(0)
//        options.setBeepEnabled(true)
//        options.setBarcodeImageEnabled(true)
//        qrCodeLauncher.launch(options)
        setContent {
            ScannerTheme {
                CardListScreen()
            }
        }
    }
}

