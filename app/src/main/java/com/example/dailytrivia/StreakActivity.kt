package com.example.dailytrivia

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class StreakActivity : AppCompatActivity() {

    private var streakCount = 24 // Assume value is fetched from a database or shared preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streak)

        // Display streak count
        val tvStreakCount: TextView = findViewById(R.id.tvStreakCount)
        tvStreakCount.text = streakCount.toString()

        // Display weekly progress
        val dayViews = listOf(
            findViewById<TextView>(R.id.tvSunday),
            findViewById<TextView>(R.id.tvMonday),
            findViewById<TextView>(R.id.tvTuesday),
            findViewById<TextView>(R.id.tvWednesday),
            findViewById<TextView>(R.id.tvThursday),
            findViewById<TextView>(R.id.tvFriday),
            findViewById<TextView>(R.id.tvSaturday)
        )

        val takenDays = listOf("Su", "Mo", "Tu") // Assume these values are fetched based on quizzes taken

        // Highlight current day of the week
        val currentDay = SimpleDateFormat("EE", Locale.getDefault()).format(Date())

        dayViews.forEach { dayView ->
            val dayLabel = dayView.text.toString()
            if (dayLabel == currentDay) {
                // Highlight current day
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(0xFFFFD700.toInt(), 0xFFFFA500.toInt())
                )
                gradientDrawable.cornerRadius = 16f
                dayView.background = gradientDrawable
            }
            if (takenDays.contains(dayLabel)) {
                dayView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmark, 0)
            }
        }
    }
}
