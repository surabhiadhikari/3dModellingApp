package com.example.a3dmodellingapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException

class CaptureActivity : AppCompatActivity() {
    private val images: MutableList<Bitmap> = mutableListOf()
    private lateinit var captureButton: Button
    private lateinit var doneButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture)

        captureButton = findViewById(R.id.captureImageButton)
        doneButton = findViewById(R.id.doneButton)

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA), 1
            )
        } else {
            setupCaptureButton()
        }

        doneButton.setOnClickListener {
            if (images.size < 3) {
                Toast.makeText(this, "Capture at least 3 images to create a 3D model!", Toast.LENGTH_SHORT).show()
            } else {
                generate3DModel()
            }
        }
    }

    private fun setupCaptureButton() {
        val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                images.add(bitmap)
                Toast.makeText(this, "Captured image ${images.size}", Toast.LENGTH_SHORT).show()
            }
        }

        captureButton.setOnClickListener {
            takePictureLauncher.launch(null)
        }
    }

    private fun generate3DModel() {
        Toast.makeText(this, "Building 3D model...", Toast.LENGTH_SHORT).show()
        val client = OkHttpClient()

        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        for ((index, bitmap) in images.withIndex()) {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val byteArray = stream.toByteArray()
            val mediaType = "image/jpeg".toMediaType()
            val requestBody = byteArray.toRequestBody(mediaType)
            builder.addFormDataPart("image$index", "image$index.jpg", requestBody)
        }

        val requestBody = builder.build()
        val request = Request.Builder()
            .url("http://your-server-url/generate-3d-model")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@CaptureActivity, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val modelUrl = response.body?.string()
                    runOnUiThread {
                        exportModel(modelUrl ?: "")
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CaptureActivity, "Error from server!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun exportModel(modelUrl: String) {
        Toast.makeText(this, "3D Model ready at $modelUrl", Toast.LENGTH_LONG).show()
    }
}
