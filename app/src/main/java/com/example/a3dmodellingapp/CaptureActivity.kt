package com.example.a3dmodellingapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.camera.view.PreviewView
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CaptureActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var previewView: PreviewView

    // Handler to trigger photo capture every 700ms
    private val handler = Handler(Looper.getMainLooper())
    private val photoInterval: Long = 700 // 700ms interval for photo capture
    private var isCapturing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        previewView = findViewById(R.id.cameraPreview) // Assuming previewView is in your layout

        // Set up camera
        startCamera()

        // Start capturing photos at regular intervals
        startPhotoCaptureLoop()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Set up Preview use case
            val preview = androidx.camera.core.Preview.Builder().build()
            preview.surfaceProvider = previewView.surfaceProvider

            // Set up ImageCapture use case
            imageCapture = ImageCapture.Builder().build()

            // Bind use cases to lifecycle
            val cameraSelector = androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(this))
    }

    private fun startPhotoCaptureLoop() {
        if (!isCapturing) {
            isCapturing = true

            // Start capturing photos every 700ms
            handler.post(object : Runnable {
                override fun run() {
                    // Capture a photo
                    capturePhoto()

                    // Schedule the next capture
                    handler.postDelayed(this, photoInterval)
                }
            })
        }
    }

    private fun capturePhoto() {
        // Ensure imageCapture is initialized before using it
        if (::imageCapture.isInitialized) {
            val file = createFile() // Create a file to save the image
            val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

            imageCapture.takePicture(
                outputOptions,
                cameraExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Handle the successful image capture
                        val savedUri = outputFileResults.savedUri
                        if (savedUri != null) {
                            // Use savedUri if it's not null
                            // For example, you can show the captured image or perform other actions
                            Log.d("CaptureActivity", "Image saved at: $savedUri")
                        } else {
                            // Handle the case where savedUri is null
                            Log.e("CaptureActivity", "Image save failed: Uri is null")
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Handle any errors during capture
                        Log.e("CaptureActivity", "Error capturing image: ${exception.message}")
                    }
                })
        } else {
            // Handle the case where imageCapture is not initialized
            Log.e("CaptureActivity", "ImageCapture not initialized")
        }
    }

    private fun createFile(): File {
        // Create a file to save the image, e.g., in a specific directory
        val directory = File(filesDir, "captured_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val timestamp = System.currentTimeMillis()
        return File(directory, "IMG_$timestamp.jpg")
    }

    override fun onStart() {
        super.onStart()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onStop() {
        super.onStop()
        // Shutdown the camera executor to release resources
        cameraExecutor.shutdown()
        isCapturing = false
        handler.removeCallbacksAndMessages(null) // Stop the photo capture loop
    }
}
