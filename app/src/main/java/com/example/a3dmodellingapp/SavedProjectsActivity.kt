package com.example.a3dmodellingapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.a3dmodellingapp.databinding.ActivitySavedProjectsBinding
import java.io.File

class SavedProjectsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySavedProjectsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySavedProjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePaths = intent.getStringArrayExtra("imagePaths") ?: emptyArray()
        val images = imagePaths.map { File(it) }

        if (images.isNotEmpty()) {
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, images.map { it.name })
            binding.savedProjectsList.adapter = adapter

            binding.savedProjectsList.setOnItemClickListener { _, _, position, _ ->
                val selectedImageFile = images[position]
                val intent = Intent(this, ImageViewerActivity::class.java)
                intent.putExtra("imagePath", selectedImageFile.absolutePath)
                startActivity(intent)
            }
        } else {
            Toast.makeText(this, "No saved projects to display", Toast.LENGTH_LONG).show()
        }
    }
}
