package com.example.pantrypal.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// Bu sınıf kameradan gelen görüntüleri işler
class BarcodeAnalyzer(
    private val onBarcodeDetected: (String) -> Unit // Barkod bulunduğunda tetiklenecek fonksiyon
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // Görüntüyü ML Kit'in anlayacağı formata çeviriyoruz
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Sadece market barkodlarını (EAN-13 ve UPC) tara
            // QR Code gibi diğerlerini tarayıp işlemciyi yormasın
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E
                )
                .build()

            val scanner = BarcodeScanning.getClient(options)

            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Eğer barkod bulduysa:
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { code ->
                            // Bulunan kodu UI'a gönder
                            onBarcodeDetected(code)
                        }
                    }
                }
                .addOnFailureListener {
                    // Hata olursa (Loglayabilirsin ama kullanıcıya gösterme)
                }
                .addOnCompleteListener {
                    // Bir sonraki kareyi işlemek için belleği temizle (Çok Önemli!)
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}