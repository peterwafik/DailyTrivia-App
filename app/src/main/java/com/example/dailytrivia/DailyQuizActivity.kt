package com.example.dailytrivia

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.charset.StandardCharsets

fun Int.toDp(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}

class DailyQuizActivity : AppCompatActivity() {

    private lateinit var tvQuestion: TextView
    private lateinit var radioGroupOptions: RadioGroup
    private lateinit var btnSubmit: Button

    private var currentQuestionIndex = 0
    private var score = 0

    // Define 'questions' as a mutable list to store questions dynamically fetched from the API
    private var questions: List<Question> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_daily_quiz)

        val email = intent.getStringExtra("EMAIL")
        val quizId = intent.getIntExtra("QUIZ_ID", -1)

        tvQuestion = findViewById(R.id.question)
        radioGroupOptions = findViewById(R.id.rgoptions)
        btnSubmit = findViewById(R.id.btnsubmit)

        if(email != null){
            loadQuestions(quizId, email) // Fetch trivia questions from the API
        }

        btnSubmit.setOnClickListener {
            if(email != null){
                checkAnswer(quizId, email)
            }
        }
    }

    private fun loadQuestions(quizId: Int, email: String) {

        Log.d(
            "in",
            "loadQuestions")

        if (quizId == -1) {
            // Retrieve the stored customization settings from the database
            val dbHelper = DatabaseHelper(this)
            val db = dbHelper.readableDatabase
//            val cursor = db.query(
//                DatabaseHelper.TABLE_NAME,
//                null, null, null, null, null, null
//            )

            val cursor = db.rawQuery(
                "SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_USER} = ?",
                arrayOf(email) // Pass the email as a parameter
            )

//            val cursor = db.rawQuery("SELECT * FROM ${DatabaseHelper.TABLE_NAME} WHERE ${DatabaseHelper.COLUMN_USER} = ${email}", null)
            val columnNames = cursor.columnNames
            columnNames.forEach { Log.d("Column Name", it) }

            if (cursor.moveToFirst()) {       // change the elseeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee
                var categoryId = 9
                var questionType = "multiple"
                var difficultyLevel = "easy"
                var numberOfQuestions = 5
                var user_email = ""
                // Safely get column indices, avoiding errors if a column is not found
                val categoryIdIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID)
                val questionTypeIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_TYPE)
                val difficultyLevelIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_DIFFICULTY_LEVEL)
                val numberOfQuestionsIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_NUMBER)

                if (categoryIdIndex != -1 && questionTypeIndex != -1 && difficultyLevelIndex != -1 && numberOfQuestionsIndex != -1) {
                    categoryId = cursor.getInt(categoryIdIndex)
                    questionType = cursor.getString(questionTypeIndex)
                    difficultyLevel = cursor.getString(difficultyLevelIndex)
                    numberOfQuestions = cursor.getInt(numberOfQuestionsIndex)
                    // Use the retrieved values here
                    if(categoryId == null) {
                        categoryId = 9
                    }
                    if(questionType == null) {
                        questionType = "multiple"
                    }
                    if(difficultyLevel == null) {
                        difficultyLevel = "easy"
                    }
                    if(numberOfQuestions == null) {
                        numberOfQuestions = 5
                    }
                } else {
                    // Handle the case where one or more columns were not found
                    Log.e("CursorError", "One or more columns were not found in the cursor.")
                }

                columnNames.forEach { Log.d("numberOfQuestions", numberOfQuestions.toString()) }
                columnNames.forEach { Log.d("categoryId", categoryId.toString()) }
                columnNames.forEach { Log.d("Column Name", questionType) }
                columnNames.forEach { Log.d("Column Name", difficultyLevel) }

                val apiService = ApiClient.retrofitInstance.create(TriviaApi::class.java)
                val call = apiService.getTriviaQuestions(
                    numberOfQuestions,
                    categoryId,
                    difficultyLevel,
                    questionType,
                    "base64"
                )

                call.enqueue(object : Callback<TriviaResponseQuestions> {
                    override fun onResponse(call: Call<TriviaResponseQuestions>, response: Response<TriviaResponseQuestions>) {
                        if (response.isSuccessful) {
                            response.body()?.results?.let { encodedQuestions ->
                                val decodedQuestions = encodedQuestions.map { question ->
                                    question.copy(
                                        question = String(Base64.decode(question.question, Base64.DEFAULT), StandardCharsets.UTF_8),
                                        correct_answer = String(Base64.decode(question.correct_answer, Base64.DEFAULT), StandardCharsets.UTF_8),
                                        incorrect_answers = question.incorrect_answers.map { encodedAnswer ->
                                            String(Base64.decode(encodedAnswer, Base64.DEFAULT), StandardCharsets.UTF_8)
                                        }
                                    )
                                }
                                questions = decodedQuestions
                                columnNames.forEach { Log.d("questions", questions.toString()) }
                                loadQuestion()
                            }
                        } else {
                            Toast.makeText(this@DailyQuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<TriviaResponseQuestions>, t: Throwable) {
                        Toast.makeText(this@DailyQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                val categoryId = 9
                val questionType = "multiple"
                val difficultyLevel = "easy"
                val numberOfQuestions = 5

                val apiService = ApiClient.retrofitInstance.create(TriviaApi::class.java)
                val call = apiService.getTriviaQuestions(
                    numberOfQuestions,
                    categoryId,
                    difficultyLevel,
                    questionType,
                    "base64"
                )

                call.enqueue(object : Callback<TriviaResponseQuestions> {
                    override fun onResponse(call: Call<TriviaResponseQuestions>, response: Response<TriviaResponseQuestions>) {
                        if (response.isSuccessful) {
                            response.body()?.results?.let { encodedQuestions ->
                                val decodedQuestions = encodedQuestions.map { question ->
                                    question.copy(
                                        question = String(Base64.decode(question.question, Base64.DEFAULT), StandardCharsets.UTF_8),
                                        correct_answer = String(Base64.decode(question.correct_answer, Base64.DEFAULT), StandardCharsets.UTF_8),
                                        incorrect_answers = question.incorrect_answers.map { encodedAnswer ->
                                            String(Base64.decode(encodedAnswer, Base64.DEFAULT), StandardCharsets.UTF_8)
                                        }
                                    )
                                }
                                questions = decodedQuestions
                                columnNames.forEach { Log.d("questions", questions.toString()) }
                                loadQuestion()
                            }
                        } else {
                            Toast.makeText(this@DailyQuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<TriviaResponseQuestions>, t: Throwable) {
                        Toast.makeText(this@DailyQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            cursor.close()
        } else {

            Log.d(
                "heloooooo",
                "heloooooooooooo")

            val apiService = ApiClient.retrofitInstance.create(TriviaApi::class.java)

            val call = apiService.getTriviaQuestions(
                10,
                quizId,
                "hard",
                "multiple",
                "base64"
            )
            call.enqueue(object : Callback<TriviaResponseQuestions> {
                override fun onResponse(call: Call<TriviaResponseQuestions>, response: Response<TriviaResponseQuestions>) {
                    if (response.isSuccessful) {
                        response.body()?.results?.let { encodedQuestions ->
                            val decodedQuestions = encodedQuestions.map { question ->
                                question.copy(
                                    question = String(Base64.decode(question.question, Base64.DEFAULT), StandardCharsets.UTF_8),
                                    correct_answer = String(Base64.decode(question.correct_answer, Base64.DEFAULT), StandardCharsets.UTF_8),
                                    incorrect_answers = question.incorrect_answers.map { encodedAnswer ->
                                        String(Base64.decode(encodedAnswer, Base64.DEFAULT), StandardCharsets.UTF_8)
                                    }
                                )
                            }
                            questions = decodedQuestions
                            loadQuestion()
                        }
                    } else {
                        Toast.makeText(this@DailyQuizActivity, "Failed to load questions", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<TriviaResponseQuestions>, t: Throwable) {
                    Toast.makeText(this@DailyQuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun loadQuestion() {
        if (questions.isEmpty()) return // Ensure there are questions to display

        val question = questions[currentQuestionIndex]
        tvQuestion.text = question.question // Set the question text

        // Clear any existing radio buttons in the RadioGroup
        radioGroupOptions.removeAllViews()

        // Populate the options
        val options = mutableListOf<String>().apply {
            add(question.correct_answer)
            addAll(question.incorrect_answers)
        }

        options.shuffle() // Shuffle the options to randomize their order

        // Create new RadioButtons for each option
        for (option in options) {
            val radioButton = RadioButton(this).apply {
                text = option
                layoutParams = RadioGroup.LayoutParams(
                    250.toDp(this@DailyQuizActivity), // Convert 250dp to pixels
                    RadioGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 0, 8.toDp(this@DailyQuizActivity)) // Convert 8dp to pixels
                }
                setTextColor(ContextCompat.getColor(this@DailyQuizActivity, R.color.white)) // Set text color
                textSize = 16f // Set text size
                buttonTintList = ContextCompat.getColorStateList(this@DailyQuizActivity, R.color.white) // Set button tint color
                background = ContextCompat.getDrawable(this@DailyQuizActivity, R.drawable.quiz_banner_background) // Set background drawable
            }
            radioGroupOptions.addView(radioButton)
        }


    }

    private fun checkAnswer(quizId: Int, email: String) {
        val selectedOptionIndex = radioGroupOptions.indexOfChild(findViewById(radioGroupOptions.checkedRadioButtonId))

        if (selectedOptionIndex == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }

        // Get the selected RadioButton's text
        val selectedAnswer = (radioGroupOptions.getChildAt(selectedOptionIndex) as RadioButton).text.toString()

        // Find the correct answer
        val question = questions[currentQuestionIndex]
        val isCorrect = selectedAnswer == question.correct_answer
        if (isCorrect) score++
        val funFacts = listOf(
            "Did you know? Honey never spoils. Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible!",
            "Did you know? Octopuses have three hearts and blue blood.",
            "Did you know? Bananas are berries, but strawberries aren’t.",
            "Did you know? A day on Venus is longer than a year on Venus.",
            "Did you know? Wombat poop is cube-shaped!"
        )
        val randomFunFact = funFacts.random()
        showFeedbackPopup(isCorrect, question.correct_answer, randomFunFact, quizId, email) // Example fun fact
    }

    private fun showFeedbackPopup(isCorrect: Boolean, correctAnswer: String, funFact: String, quizId: Int, email: String) {
        val feedbackView = LayoutInflater.from(this).inflate(R.layout.popup_feedback, null)
        val dialog = AlertDialog.Builder(this)
            .setView(feedbackView)
            .setCancelable(false)
            .create()

        val tvFeedback = feedbackView.findViewById<TextView>(R.id.feedback)
        val tvCorrectAnswer = feedbackView.findViewById<TextView>(R.id.correctanswer)
        val tvFunFact = feedbackView.findViewById<TextView>(R.id.FunFact)
        val btnNextQuestion = feedbackView.findViewById<Button>(R.id.btnnextQuestion)

        tvFeedback.text = if (isCorrect) "Correct" else "Wrong"
        tvCorrectAnswer.text = "Correct Answer: $correctAnswer"
        tvFunFact.text = funFact

        btnNextQuestion.setOnClickListener {
            dialog.dismiss()
            currentQuestionIndex++
            if (currentQuestionIndex < questions.size) {
                loadQuestion()
            } else {
                showFinalResultPopup(quizId, email)
            }
        }

        dialog.show()
    }

    private fun showFinalResultPopup(quizId: Int, email: String) {
        val resultView = LayoutInflater.from(this).inflate(R.layout.popup_quiz_result, null)
        val dialog = AlertDialog.Builder(this)
            .setView(resultView)
            .setCancelable(false)
            .create()

        val tvFinalScore = resultView.findViewById<TextView>(R.id.tvFinalScore)
        val btnReturnHome = resultView.findViewById<Button>(R.id.btnReturnHome)

        tvFinalScore.text = "Your Score: $score/${questions.size}"


        val scoreee = score.toDouble()/questions.size * 100

        val dbHelper = DatabaseHelper(this)
        if(quizId == -1) {

            // Get the category and number of questions from the database
            val db = dbHelper.readableDatabase
            val cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                null, null, null, null, null, null
            )

            if (cursor.moveToFirst()) {
                val COLUMN_CATEGORY_ID_INDEX =
                    cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY_ID)
                val categoryId = cursor.getInt(COLUMN_CATEGORY_ID_INDEX)
                val category = getCategoryName(categoryId)  // Retrieve category name by ID
                val COLUMN_QUESTION_NUMBER_INDEX =
                    cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_NUMBER)
                val numberOfQuestions = cursor.getInt(COLUMN_QUESTION_NUMBER_INDEX)

                // Insert data into the Quiz table
                val currentTime = System.currentTimeMillis() // Timestamp of when the quiz was taken
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_CATEGORY, category)
                    put(DatabaseHelper.COLUMN_SCORE, scoreee)
                    put(DatabaseHelper.COLUMN_QUESTION_NUMBER2, numberOfQuestions)
                    put(DatabaseHelper.COLUMN_TIMESTAMP, currentTime)  // Store timestamp
                    put(DatabaseHelper.COLUMN_USER2, email)  // Store timestamp
                }


                // Insert into Quiz table
                val quizDb = dbHelper.writableDatabase
                quizDb.insert(DatabaseHelper.TABLE_NAME2, null, values)

                // Log data inserted for verification
                Log.d(
                    "QuizData",
                    "Category: $category, Number of Questions: $numberOfQuestions, Score: $score, Timestamp: $currentTime"
                )
            } else {
                // Insert data into the Quiz table
                val currentTime = System.currentTimeMillis() // Timestamp of when the quiz was taken
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_CATEGORY, "General Knowledge")
                    put(DatabaseHelper.COLUMN_SCORE, scoreee)
                    put(DatabaseHelper.COLUMN_QUESTION_NUMBER2, 5)
                    put(DatabaseHelper.COLUMN_TIMESTAMP, currentTime)  // Store timestamp
                    put(DatabaseHelper.COLUMN_USER2, email)  // Store timestamp
                }


                // Insert into Quiz table
                val quizDb = dbHelper.writableDatabase
                quizDb.insert(DatabaseHelper.TABLE_NAME2, null, values)
            }

            cursor.close()
        }
        else{
            // Insert data into the Trending Quiz table
            val currentTime = System.currentTimeMillis() // Timestamp of when the quiz was taken
            val cat = getCategoryName(quizId)
            val values = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CATEGORY2, cat)
                put(DatabaseHelper.COLUMN_SCORE2, scoreee)
                put(DatabaseHelper.COLUMN_QUESTION_NUMBER3, 10)
                put(DatabaseHelper.COLUMN_TIMESTAMP2, currentTime)  // Store timestamp
                put(DatabaseHelper.COLUMN_USER3, email)  // Store timestamp
            }


            // Insert into Quiz table
            val quizDb = dbHelper.writableDatabase
            quizDb.insert(DatabaseHelper.TABLE_NAME3, null, values)

        }

        btnReturnHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            if(email != null) {
                intent.putExtra("EMAIL", email)
            }
            startActivity(intent)
            finish()
        }

        dialog.show()
    }


    // Helper function to retrieve the category name by its ID
    private fun getCategoryName(categoryId: Int): String {
        val categories = mapOf(
            9 to "General Knowledge",
            10 to "Entertainment: Books",
            11 to "Entertainment: Film",
            12 to "Entertainment: Music",
            13 to "Entertainment: Musicals & Theatres",
            14 to "Entertainment: Television",
            15 to "Entertainment: Video Games",
            16 to "Entertainment: Board Games",
            17 to "Science & Nature",
            18 to "Science: Computers",
            19 to "Science: Mathematics",
            20 to "Mythology",
            21 to "Sports",
            22 to "Geography",
            23 to "History",
            24 to "Politics",
            25 to "Art",
            26 to "Celebrities",
            27 to "Animals",
            28 to "Vehicles",
            29 to "Entertainment: Comics",
            30 to "Science: Gadgets",
            31 to "Entertainment: Japanese Anime & Manga",
            32 to "Entertainment: Cartoon & Animations"
        )

        return categories[categoryId] ?: "Unknown"  // Default to "Unknown" if not found
    }

}
