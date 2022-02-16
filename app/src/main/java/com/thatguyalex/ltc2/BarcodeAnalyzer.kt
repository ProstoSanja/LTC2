package com.thatguyalex.ltc2

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicReference

class BarcodeAnalyzer(
    val callback: (barcode: String, reportError: Boolean) -> Unit
) : ImageAnalysis.Analyzer {

    private var scanner : BarcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ITF)
            .build()
    )

    private var lastDetectedCode = AtomicReference("")

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.size > 0) {
                        val barcode = barcodes[0]
                        if (barcode.rawValue != null && barcode.rawValue!!.length == 14) {
                            val barcodeValue = barcode.rawValue!!
                            if (lastDetectedCode.getAndSet(barcodeValue) != barcodeValue) {
                                Log.w("TAG3", "found new barcode " + barcode.rawValue!!)
                                callback(barcode.rawValue!!, false)
                            }
                        }
                    }
                }
                .addOnFailureListener {
//                    Log.w("TAG2", "FOUND NOTHING")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
            Thread.sleep(100)
        }
    }
}