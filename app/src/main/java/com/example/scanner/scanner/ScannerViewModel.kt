package com.example.scanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ScannerViewModel : ViewModel() {

    // Stocke le QR Code scanné
    private val _scannedCode = MutableStateFlow<String?>(null)
    val scannedCode: StateFlow<String?> = _scannedCode

    fun onQrCodeScanned(code: String) {
        _scannedCode.value = code
    }
}
