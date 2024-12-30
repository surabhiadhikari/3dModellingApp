package com.example.a3dmodellingapp
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
