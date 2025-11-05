package com.example.myapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanner.list.ComponentScreenViewModel

@Composable
fun ScreenComponent(viewModel: ComponentScreenViewModel = viewModel()) {

    val scannedQrCode by viewModel.scannedQrCode

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            viewModel.simulateQrCodeScan()
        }) {
            Text(text = "Simuler le scan")
        }
        if (scannedQrCode != null) {
            Text(text = "QR simulé : ${scannedQrCode}")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ScreenComponentPreview() {
    ScreenComponent()
}