package com.example.dailytrivia

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find views by their IDs
        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signupLink: TextView = findViewById(R.id.signupLink)
        val errorTextView: TextView = findViewById(R.id.errorr_signin)

        // Resize and set the drawable for username
        val usernameDrawable: Drawable? = resources.getDrawable(R.drawable.username, theme)
        usernameDrawable?.setBounds(0, 0, 40, 40)
        usernameEditText.setCompoundDrawables(usernameDrawable, null, null, null)

        // Resize and set the drawable for password
        val passwordDrawable: Drawable? = resources.getDrawable(R.drawable.password, theme)
        passwordDrawable?.setBounds(0, 0, 40, 40)
        passwordEditText.setCompoundDrawables(passwordDrawable, null, null, null)

        // Login button click listener
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                if(email.isEmpty()){
                    errorTextView.text = "Please enter your email"
                }
                if(password.isEmpty()){
                    errorTextView.text = "Please enter your password"
                }
                if(email.isEmpty() && password.isEmpty()){
                    errorTextView.text = "Please enter your email and password"
                }

                errorTextView.visibility = TextView.VISIBLE
            } else {
                // Firebase Authentication logic
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            errorTextView.visibility = TextView.GONE // Hide error if successful
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()
                        } else {
                            errorTextView.text = "Login failed: Invalid credentials"
                            errorTextView.visibility = TextView.VISIBLE
                        }
                    }
            }
        }

        // Sign-up link click listener
        signupLink.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }
}
