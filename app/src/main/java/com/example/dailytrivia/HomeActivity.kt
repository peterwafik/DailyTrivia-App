package com.example.dailytrivia

// Import statement for the intent functionality
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeActivity : AppCompatActivity() {
    private var flag = true;
    private var streak = 0;
    private var categories: List<Category> = listOf() // Categories list to store the fetched categories

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val email = intent.getStringExtra("EMAIL")

        val rvFriendsList = findViewById<RecyclerView>(R.id.rvFriendsList)

        // Create dummy data for the list of friends
        val friendsList = listOf(
            Friend("Alice", R.drawable.p1),
            Friend("Bob", R.drawable.p2),
            Friend("Charlie", R.drawable.p3),
            Friend("Daisy", R.drawable.p4),
            Friend("Edward", R.drawable.p5)
        )

        // Set up RecyclerView with a horizontal layout manager
        rvFriendsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Attach the FriendsAdapter to the RecyclerView with the dummy data
        rvFriendsList.adapter = FriendsAdapter(friendsList)

        // Find the RecyclerView for trending quizzes
        val rvTrendingQuizzes = findViewById<RecyclerView>(R.id.rvTrendingQuizzes)

        val streakTextView = findViewById<TextView>(R.id.streakText)
        if(email != null){
            updateStreakText(streakTextView, email)
        }

        // Initialize Retrofit and make the API call to fetch categories
        val apiService = ApiClient.retrofitInstance.create(TriviaApi::class.java)
        val call = apiService.getTriviaCategories()

        call.enqueue(object : Callback<TriviaResponseCategories> {
            override fun onResponse(call: Call<TriviaResponseCategories>, response: Response<TriviaResponseCategories>) {
                if (response.isSuccessful) {
                    response.body()?.trivia_categories?.let {
                        categories = it // Populate categories list with the fetched categories

                        // After categories are fetched, proceed to display them
                        val quizList = categories.mapIndexed { index, category ->
                            // Dynamically assign a drawable image based on the index
                            val imageResId = when (index % 24) { // Assuming you have 5 images: image1, image2, image3, image4, image5
                                0 -> R.drawable.generalknowledge
                                1 -> R.drawable.general
                                2 -> R.drawable.films
                                3 -> R.drawable.musical
                                4 -> R.drawable.theatree
                                5 -> R.drawable.television
                                6 -> R.drawable.games
                                7 -> R.drawable.board
                                8 -> R.drawable.science
                                9 -> R.drawable.computer
                                10 -> R.drawable.math
                                11 -> R.drawable.mythology
                                12 -> R.drawable.sports
                                13 -> R.drawable.geography
                                14 -> R.drawable.history
                                15 -> R.drawable.politics
                                16 -> R.drawable.art
                                17 -> R.drawable.celebrities
                                18 -> R.drawable.animals
                                19 -> R.drawable.vehicles
                                20 -> R.drawable.comics
                                21 -> R.drawable.gadgets
                                22 -> R.drawable.anime
                                23 -> R.drawable.cartoon


                                else -> R.drawable.image1 // Default case, just in case
                            }
                            Quiz(category.id, category.name, imageResId,email ?: "")

                        }

                        // Set up RecyclerView with a linear layout manager and the QuizAdapter
                        rvTrendingQuizzes.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.HORIZONTAL, false)
                        rvTrendingQuizzes.adapter = QuizAdapter(this@HomeActivity, quizList) // Use QuizAdapter with the dynamically created quiz data
                    }
                } else {
                    Toast.makeText(this@HomeActivity, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TriviaResponseCategories>, t: Throwable) {
                Toast.makeText(this@HomeActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })


        // Handle permissions and other setup
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        NotificationUtils.createNotificationChannel(this)
        scheduleDailyNotification()

        // Button to navigate to Quiz Customization Screen
        val btnSettings = findViewById<ImageView>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, CustomizationActivity::class.java)
            if(email != null) {
                intent.putExtra("EMAIL", email)
            }
                startActivity(intent)
        }

        // Button to navigate to Quiz Customization Screen
        val btnProfile = findViewById<ImageView>(R.id.ivNotificationIcon)
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            if(email != null) {
                intent.putExtra("EMAIL", email)
            }
            intent.putExtra("STREAK", streak)
            startActivity(intent)
        }

        // Button to navigate to Quiz Customization Screen
        val btnQuiz = findViewById<Button>(R.id.btnStartQuiz)
        if(email != null){
            updateQuizButtonAppearance(email)
        }

//            btnQuiz.background = R.drawable.rounded_rectangle // Pale grey      hellllllllooooooooooooooooooooooo

        if(!flag){
            btnQuiz.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray_2)
        }
        else{
            btnQuiz.backgroundTintList = ContextCompat.getColorStateList(this, R.color.purple_500)
        }
        btnQuiz.setOnClickListener {
            if(flag){
                val intent = Intent(this, DailyQuizActivity::class.java)
                // Pass the quiz ID to the next screen
                intent.putExtra("QUIZ_ID", -1)
                if(email != null) {
                    intent.putExtra("EMAIL", email)
                }
                startActivity(intent)
            }
            else{
                Toast.makeText(this@HomeActivity, "You have already taken your daily quiz", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to navigate to Quiz Customization Screen
        val btnStreak = findViewById<Button>(R.id.btnHistory)
        btnStreak.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            intent.putExtra("STREAK", streak)
            intent.putExtra("EMAIL", email)
            startActivity(intent)
        }

        val btnLeaderboard = findViewById<ImageView>(R.id.btnLeaderboard)
        btnLeaderboard.setOnClickListener {
            val intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun scheduleDailyNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
    private fun updateQuizButtonAppearance(email: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_TIMESTAMP} FROM ${DatabaseHelper.TABLE_NAME2} WHERE ${DatabaseHelper.COLUMN_USER3} = ? ORDER BY ${DatabaseHelper.COLUMN_TIMESTAMP} DESC",
            arrayOf(email) // Pass the email as a parameter
        )

        if (cursor.moveToFirst()) {
            val COLUMN_TIMESTAMP_INDEX = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)
            val lastTimestamp = cursor.getLong(COLUMN_TIMESTAMP_INDEX)
            val currentDay = getDayStartTimestamp()

            if (lastTimestamp >= currentDay) {
                // Quiz already taken today
                flag = false
            } else {
                flag = true
            }
        } else {
            // No quiz record for today
            flag = true
        }
        cursor.close()
    }

    private fun getDayStartTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun updateStreakText(textView: TextView, email: String) {
        val dbHelper = DatabaseHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT ${DatabaseHelper.COLUMN_TIMESTAMP} FROM ${DatabaseHelper.TABLE_NAME2} WHERE ${DatabaseHelper.COLUMN_USER3} = ? ORDER BY ${DatabaseHelper.COLUMN_TIMESTAMP} DESC",
            arrayOf(email)
        )

        streak = 0
        if (cursor.moveToFirst()) {
            val COLUMN_TIMESTAMP_INDEX = cursor.getColumnIndex(DatabaseHelper.COLUMN_TIMESTAMP)
            var lastTimestamp = cursor.getLong(COLUMN_TIMESTAMP_INDEX)
            streak = 1 // Start streak with the most recent quiz day

            while (cursor.moveToNext()) {
                val currentTimestamp = cursor.getLong(COLUMN_TIMESTAMP_INDEX)

                if (isConsecutiveDay(currentTimestamp, lastTimestamp)) {
                    streak++
                    lastTimestamp = currentTimestamp // Update for the next iteration
                } else {
                    break // Stop counting streak if there's a gap
                }
            }
        }
        cursor.close()

        // Update the TextView with the calculated streak
        textView.text = "Current streak: $streak 🔥"
    }

    private fun isConsecutiveDay(currentTimestamp: Long, lastTimestamp: Long): Boolean {
        val oneDayInMillis = 24 * 60 * 60 * 1000
        val difference = lastTimestamp - currentTimestamp
        return difference in 1..oneDayInMillis
    }
    override fun onBackPressed() {
        // Show a Toast or disable the back button
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
    }

}
