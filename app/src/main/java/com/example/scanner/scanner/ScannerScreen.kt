package com.example.scanner

import android.content.Intent
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.PreviewView
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer
import java.util.concurrent.Executors

//CameraX ouvre la caméra (grâce à bindToLifecycle)
//CameraX produit le flux vidéo → chaque frame devient un ImageProxy
//CameraX appelle QrCodeAnalyzer.analyze(image: ImageProxy)
//Dans analyze(), on décode la frame avec ZXing
//On ferme l’image (image.close()) pour libérer la mémoire
//On répète ce processus pour la frame suivante, automatiquement

@Composable
fun ScannerScreen(vm: ScannerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current // Cycle de vie pour gérer la caméra
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) } // Fournisseur de caméra
    val executor = remember { Executors.newSingleThreadExecutor() } // Le QrCodeAnalyzer.analyze() tourne sur ce thread

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { ctx ->
            val previewView = PreviewView(ctx) // Vue de la caméra
            val cameraProvider = cameraProviderFuture.get()

            // Construction du flux de la caméra
            val preview = Preview.Builder()
                .build()
                .also { preview ->
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                }
            // Construction de l'analyseur d'images
            val analyzer = ImageAnalysis.Builder()
                .build()
                .also { analyzer ->
                    analyzer.setAnalyzer(executor, QrCodeAnalyzer { code ->
                        vm.onQrCodeScanned(code)
                        val intent = Intent().apply { putExtra("cardId", code) }
                        //setResult(Activity.RESULT_OK, intent)
                    })
                }
            // Choix de la caméra (arrière par défaut)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analyzer)
            } catch (e: Exception) { e.printStackTrace() }

            previewView
        }, modifier = Modifier.fillMaxSize())
    }
}

// Fonction pour convertir ImageProxy (YUV_420_888) en NV21
fun ImageProxy.toNv21(): ByteArray {
    val yBuffer = planes[0].buffer // Y
    val uBuffer = planes[1].buffer // U
    val vBuffer = planes[2].buffer // V

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}

// ZXing analyzer
class QrCodeAnalyzer(private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(image: ImageProxy) {
        val nv21 = image.toNv21()
        val source = PlanarYUVLuminanceSource(nv21, image.width, image.height, 0, 0, image.width, image.height, false)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = reader.decode(bitmap)
            onQrCodeDetected(result.text)
        } catch (e: NotFoundException) {
            // rien trouvé, pas grave
        } finally {
            image.close()
        }
    }
}
