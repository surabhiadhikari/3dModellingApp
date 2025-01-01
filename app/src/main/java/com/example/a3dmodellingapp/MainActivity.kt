package com.example.a3dmodellingapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check and request permissions
        if (!checkAndRequestPermissions()) {
            // If permissions are missing, show a message
            Toast.makeText(this, "Permissions are required for this app to work", Toast.LENGTH_LONG).show()
        }

        // Initialize UI elements
        initializeUI()
    }

    private fun initializeUI() {
        // Initialize UI elements and set listeners
        val captureButton: Button = findViewById(R.id.captureButton)
        val viewSavedProjectButton: Button = findViewById(R.id.viewSavedProjectButton)

        captureButton.setOnClickListener {
            val intent = Intent(this, CaptureActivity::class.java)
            startActivity(intent)
        }

        viewSavedProjectButton.setOnClickListener {
            val intent = Intent(this, SavedProjectsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkAndRequestPermissions(): Boolean {
        val permissionsNeeded = mutableListOf<String>()

        // Add necessary permissions to the list
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        // If permissions are needed, request them
        return if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), PERMISSION_REQUEST_CODE)
            false
        } else {
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            val deniedPermissions = permissions.zip(grantResults.toTypedArray())
                .filter { it.second != PackageManager.PERMISSION_GRANTED }
                .map { it.first }

            if (deniedPermissions.isNotEmpty()) {
                Toast.makeText(this, "The app is granted permissions.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
