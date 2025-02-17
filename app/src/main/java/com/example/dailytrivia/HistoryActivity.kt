package com.example.dailytrivia

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private var streakCount = 24 // Assume value is fetched from a database or shared preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val email = intent.getStringExtra("EMAIL") // Get the user's email
        val streak = intent.getIntExtra("STREAK", -1) // Get the user's email

        // Display streak count
        val tvStreakCount: TextView = findViewById(R.id.tvStreakCount)
        streakCount = streak
        tvStreakCount.text = streakCount.toString()

        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val columns = arrayOf(
            DatabaseHelper.COLUMN_CATEGORY,
            DatabaseHelper.COLUMN_SCORE,
            DatabaseHelper.COLUMN_QUESTION_NUMBER2
        )

        // Fetch quiz data filtered by user email
        val quizHistoryData = mutableListOf<QuizResult>()

        // Query for daily quizzes
        val cursor = db.query(
            DatabaseHelper.TABLE_NAME2,
            columns,
            "${DatabaseHelper.COLUMN_USER2} = ?",
            arrayOf(email),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            do {
                val COLUMN_CATEGORY_INDEX = cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY)
                val category = cursor.getString(COLUMN_CATEGORY_INDEX)
                val COLUMN_SCORE_INDEX = cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE2)
                val score = cursor.getDouble(COLUMN_SCORE_INDEX)
                val COLUMN_QUESTION_NUMBER2_INDEX = cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_NUMBER3)
                val numberOfQuestions = cursor.getInt(COLUMN_QUESTION_NUMBER2_INDEX)
//                val category = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY))
//                val score = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_SCORE))
//                val numberOfQuestions = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_NUMBER2))

                val quiz = QuizResult(category, score, numberOfQuestions, "Daily Quiz")
                quizHistoryData.add(quiz)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Query for trending quizzes
        val cursor2 = db.query(
            DatabaseHelper.TABLE_NAME3,
            columns,
            "${DatabaseHelper.COLUMN_USER3} = ?",
            arrayOf(email),
            null, null, null
        )

        if (cursor2.moveToFirst()) {
            do {
                val COLUMN_CATEGORY2_INDEX = cursor2.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY2)
                val category = cursor2.getString(COLUMN_CATEGORY2_INDEX)
                val COLUMN_SCORE2_INDEX = cursor2.getColumnIndex(DatabaseHelper.COLUMN_SCORE2)
                val score = cursor2.getDouble(COLUMN_SCORE2_INDEX)
                val COLUMN_QUESTION_NUMBER3_INDEX = cursor2.getColumnIndex(DatabaseHelper.COLUMN_QUESTION_NUMBER3)
                val numberOfQuestions = cursor2.getInt(COLUMN_QUESTION_NUMBER3_INDEX)

                val quiz = QuizResult(category, score, numberOfQuestions, "Trending Quiz")
                quizHistoryData.add(quiz)
            } while (cursor2.moveToNext())
        }
        cursor2.close()

        // Set the adapter
        val rvQuizHistory = findViewById<RecyclerView>(R.id.rvQuizHistory)
        rvQuizHistory.layoutManager = LinearLayoutManager(this)
        rvQuizHistory.adapter = QuizHistoryAdapter(quizHistoryData)
    }

}
