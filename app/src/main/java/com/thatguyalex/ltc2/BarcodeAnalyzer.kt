package com.thatguyalex.ltc2

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalyzer(
    val callback: (barcode: String) -> Unit
) : ImageAnalysis.Analyzer {

    private var scanner : BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ITF)
            .build()
    )

    private var lastDetectedCode = ""

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        if (barcode.rawValue != null && lastDetectedCode != barcode.rawValue) {
                            lastDetectedCode = barcode.rawValue!!
                            callback(barcode.rawValue!!)
                        }
                    }
                }
                .addOnFailureListener {
                    Log.w("TAG2", "FOUND NOTHING")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}