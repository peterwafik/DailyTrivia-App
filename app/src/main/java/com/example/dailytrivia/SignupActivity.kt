package com.example.dailytrivia

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Find views by their IDs
        val emailEditText: EditText = findViewById(R.id.username_sign_up)
        val passwordEditText: EditText = findViewById(R.id.password_sign_up)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password)
        val signupButton: Button = findViewById(R.id.signupButton)
        val loginLink: TextView = findViewById(R.id.loginLink)
        val errorTextView: TextView = findViewById(R.id.errorr_signup)

        // Adjust drawable dimensions for email EditText
        val emailDrawable: Drawable? = resources.getDrawable(R.drawable.username, theme)
        emailDrawable?.setBounds(0, 0, 40, 40) // Width and height adjusted to fit the text field
        emailEditText.setCompoundDrawables(emailDrawable, null, null, null)

        // Adjust drawable dimensions for password EditText
        val passwordDrawable: Drawable? = resources.getDrawable(R.drawable.password, theme)
        passwordDrawable?.setBounds(0, 0, 40, 40) // Width and height adjusted to fit the text field
        passwordEditText.setCompoundDrawables(passwordDrawable, null, null, null)

        // Adjust drawable dimensions for confirm password EditText
        val confirmPasswordDrawable: Drawable? = resources.getDrawable(R.drawable.password, theme)
        confirmPasswordDrawable?.setBounds(0, 0, 40, 40) // Width and height adjusted to fit the text field
        confirmPasswordEditText.setCompoundDrawables(confirmPasswordDrawable, null, null, null)

        // Sign-up button click listener
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorTextView.text = "Please fill out all fields"
                errorTextView.visibility = TextView.VISIBLE
            } else if (password != confirmPassword) {
                errorTextView.text = "Passwords do not match"
                errorTextView.visibility = TextView.VISIBLE
            } else {
                // Firebase Authentication logic for creating a new user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            errorTextView.visibility = TextView.GONE // Hide error if successful
                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("EMAIL", email)
                            startActivity(intent)
                            finish()
                        } else {
                            errorTextView.text = "Sign-up failed: ${task.exception?.message}"
                            errorTextView.visibility = TextView.VISIBLE
                        }
                    }
            }
        }

        // Login link click listener
        loginLink.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }
    }
}
