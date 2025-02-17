package com.example.dailytrivia

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.google.firebase.FirebaseApp

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        FirebaseApp.initializeApp(this)

        // Reference the "Get started" button
        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

        // Set up a click listener for the button to navigate to the next screen (e.g., MainMenuActivity)
        btnGetStarted.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        // Show a Toast or disable the back button
        finishAffinity()
    }
}
