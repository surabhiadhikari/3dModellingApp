package com.example.a3dmodellingapp

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a3dmodellingapp.databinding.ActivityImageViewerBinding
import java.io.File

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root) // Set the content to the root view

        // Set the toolbar title
        setSupportActionBar(binding.toolbar)

        // Retrieve the image path from the Intent
        val imagePath = intent.getStringExtra("imagePath")

        if (imagePath != null) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                // Load and display the image
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                binding.imageView.setImageBitmap(bitmap)
            } else {
                // Show error icon and a toast if the image file doesn't exist
                binding.imageView.setImageResource(android.R.drawable.ic_dialog_alert)
                Toast.makeText(this, "Image file does not exist!", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Show error icon and a toast if no image path is provided
            binding.imageView.setImageResource(android.R.drawable.ic_dialog_alert)
            Toast.makeText(this, "No image path provided!", Toast.LENGTH_SHORT).show()
        }
    }
}
