package com.example.a3dmodellingapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.a3dmodellingapp.databinding.ActivityCaptureBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CaptureActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaptureBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService
    private var isCapturing = false
    private val capturedImages = mutableListOf<File>()
    private var captureTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCaptureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        setupCamera()

        binding.captureImageButton.setOnClickListener {
            if (!isCapturing) {
                isCapturing = true
                captureImages()
            }
        }

        binding.stopCaptureButton.setOnClickListener {
            if (isCapturing) {
                stopCapturing()
            }
        }

        binding.doneButton.setOnClickListener {
            if (capturedImages.isNotEmpty()) {
                processImagesFor3DModel()
                val imagePaths = capturedImages.map { it.absolutePath }.toTypedArray()
                val intent = Intent(this, SavedProjectsActivity::class.java)
                intent.putExtra("imagePaths", imagePaths)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No images captured", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                // Get the camera provider
                val cameraProvider = cameraProviderFuture.get()

                // Preview use case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }

                // Image capture use case
                imageCapture = ImageCapture.Builder().build()

                // Select back camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Unbind all use cases before rebinding
                cameraProvider.unbindAll()

                // Bind the use cases to the lifecycle of this activity
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to set up camera: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun captureImages() {
        val outputDir = File(filesDir, "CapturedImages").apply { mkdirs() }
        captureTimer = Timer()
        captureTimer?.schedule(object : TimerTask() {
            override fun run() {
                if (!isCapturing) {
                    stopCapturing()
                    return
                }
                takePhoto(outputDir)
            }
        }, 0, 1500)
    }

    private fun stopCapturing() {
        isCapturing = false
        captureTimer?.cancel()
        captureTimer = null
        Toast.makeText(this, "Stopped capturing images", Toast.LENGTH_SHORT).show()
    }

    private fun takePhoto(outputDir: File) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val file = File(outputDir, "IMG_$timeStamp.jpg")

        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    capturedImages.add(file)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@CaptureActivity, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun processImagesFor3DModel() {
        val modelOutputDir = File(filesDir, "3DModel").apply { mkdirs() }
        val modelFile = File(modelOutputDir, "model.obj").apply { writeText("3D model generated from images") }
        Toast.makeText(this, "3D model saved to ${modelFile.absolutePath}", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        stopCapturing()
    }
}
