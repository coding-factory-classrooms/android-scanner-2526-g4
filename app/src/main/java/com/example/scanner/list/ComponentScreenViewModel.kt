package com.example.scanner.list


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ComponentScreenViewModel : ViewModel(){

    // Création d'une boite qui va stocker le qr code et prévient l'écran quand la donnée changera
     val scannedQrCode = mutableStateOf<String?>(null)
    // Lecture ui du component sans la modifier
    val scannedQrCodeUi: State<String?> = scannedQrCode


    fun simulateQrCodeScan() {
        scannedQrCode.value = "Simulate_QR_CODE"
    }
}