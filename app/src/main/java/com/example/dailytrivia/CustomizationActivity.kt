package com.example.dailytrivia

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomizationActivity : AppCompatActivity() {

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var radioGroupQuestionType: RadioGroup
    private lateinit var etNumberOfQuestions: EditText
    private lateinit var tvErrorNumberOfQuestions: TextView
    private lateinit var tvErrorQuestionType: TextView
    private var categories = listOf<Category>() // List to store categories fetched from the API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customization)

        spinnerCategory = findViewById(R.id.spinnerCategory)
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty)
        radioGroupQuestionType = findViewById(R.id.radioGroupQuestionType)
        etNumberOfQuestions = findViewById(R.id.etNumberOfQuestions)
        tvErrorNumberOfQuestions = findViewById(R.id.tvErrorNumberOfQuestions)
        tvErrorQuestionType = findViewById(R.id.tvErrorQuestionType)

        val email = intent.getStringExtra("EMAIL")
        // Load categories from the API
        loadCategories()

        // Button to save customization settings
        val btnSaveCustomization = findViewById<Button>(R.id.btnSaveCustomization)
        val btnLogOut = findViewById<Button>(R.id.bottomButton)
        btnLogOut.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
        }

        btnSaveCustomization.setOnClickListener {
            // Validate number of questions input
            val numberOfQuestions = etNumberOfQuestions.text.toString().toIntOrNull() ?: 0
            val isQuestionTypeSelected = radioGroupQuestionType.checkedRadioButtonId != -1

            var isValid = true

            // Check number of questions
            if (numberOfQuestions < 1 || numberOfQuestions > 99) {
                tvErrorNumberOfQuestions.visibility = View.VISIBLE
                isValid = false
            } else {
                tvErrorNumberOfQuestions.visibility = View.GONE
            }

            // Check if question type is selected
            if (!isQuestionTypeSelected) {
                tvErrorQuestionType.visibility = View.VISIBLE
                isValid = false
            } else {
                tvErrorQuestionType.visibility = View.GONE
            }

            // If both inputs are valid, save the settings to the database
            if (isValid) {
                val dbHelper = DatabaseHelper(this)

                val db = dbHelper.writableDatabase

            // Check if the record exists
                val cursor = db.query(
                    DatabaseHelper.TABLE_NAME, // Replace with your actual table name
                    arrayOf(DatabaseHelper.COLUMN_USER), // Replace with your actual column name
                    "${DatabaseHelper.COLUMN_USER} = ?", // Where clause
                    arrayOf(email), // Where clause arguments
                    null, // Group By
                    null, // Having
                    null // Order By
                )

                if (cursor.moveToFirst()) {
                    // Record exists, delete it
                    val rowsDeleted = db.delete(
                        DatabaseHelper.TABLE_NAME,
                        "${DatabaseHelper.COLUMN_USER} = ?",
                        arrayOf(email)
                    )
                }

                // Insert the new customization settings
                if(email != null) {
                    saveCustomizationSettings(dbHelper, email)
                }

                // Navigate back to the Home Activity
                val intent = Intent(this, HomeActivity::class.java)
                if(email != null) {
                    intent.putExtra("EMAIL", email)
                }
                startActivity(intent)
            }
        }
    }

    private fun loadCategories() {
        val apiService = ApiClient.retrofitInstance.create(TriviaApi::class.java)

        val call = apiService.getTriviaCategories()
        call.enqueue(object : Callback<TriviaResponseCategories> {
            override fun onResponse(call: Call<TriviaResponseCategories>, response: Response<TriviaResponseCategories>) {
                if (response.isSuccessful) {
                    response.body()?.trivia_categories?.let {
                        categories = it // Assign categories from API response
                        setupSpinner(categories)
                    }
                } else {
                    Toast.makeText(this@CustomizationActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TriviaResponseCategories>, t: Throwable) {
                Toast.makeText(this@CustomizationActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupSpinner(categories: List<Category>) {
        val categoryNames = categories.map { it.name } // Extract category names
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter
    }

    private fun saveCustomizationSettings(dbHelper: DatabaseHelper, email: String?) {
        if (email.isNullOrEmpty()) {
            Toast.makeText(this, "Failed to save settings: User email is missing", Toast.LENGTH_SHORT).show()
            return
        }

        // Get selected category
        val selectedCategoryIndex = spinnerCategory.selectedItemPosition
        val selectedCategoryId = if (selectedCategoryIndex != -1) categories[selectedCategoryIndex].id else -1

        // Get selected question type
        val selectedQuestionType = when (radioGroupQuestionType.checkedRadioButtonId) {
            R.id.radioMultipleChoice -> "multiple"
            R.id.radioTrueFalse -> "boolean"
            else -> ""
        }

        // Get selected difficulty level
        val selectedDifficulty = spinnerDifficulty.selectedItem.toString().lowercase()

        // Get number of questions
        val numberOfQuestions = etNumberOfQuestions.text.toString().toIntOrNull() ?: 0

        // Check validity of inputs
        if (selectedCategoryId == -1 || selectedQuestionType.isEmpty() || numberOfQuestions <= 0) {
            Toast.makeText(this, "Failed to save settings: Invalid inputs", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert data into the database using DatabaseHelper
        val db = dbHelper.writableDatabase

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CATEGORY_ID, selectedCategoryId)
            put(DatabaseHelper.COLUMN_QUESTION_TYPE, selectedQuestionType)
            put(DatabaseHelper.COLUMN_DIFFICULTY_LEVEL, selectedDifficulty)
            put(DatabaseHelper.COLUMN_QUESTION_NUMBER, numberOfQuestions)
            put(DatabaseHelper.COLUMN_USER, email)
        }

        // Delete existing record for the user
        db.delete(DatabaseHelper.TABLE_NAME, "${DatabaseHelper.COLUMN_USER} = ?", arrayOf(email))

        Log.d(
            "Settingssss",
            "CategoryId: $selectedCategoryId, Number of Questions: $numberOfQuestions, selectedQuestionType: $selectedQuestionType, selectedDifficulty: $selectedDifficulty"
        )

        // Insert the new record
        val newRowId = db.insert(DatabaseHelper.TABLE_NAME, null, values)
        if (newRowId != -1L) {
            Toast.makeText(this, "Settings Saved Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to Save Settings", Toast.LENGTH_SHORT).show()
        }
    }
}
