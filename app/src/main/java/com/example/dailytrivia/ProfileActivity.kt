package com.example.dailytrivia;


import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.dailytrivia.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

//        val profileImage: ImageView = findViewById(R.id.profileImage)
        val profileName: TextView = findViewById(R.id.profileName)
        val profileEmail: TextView = findViewById(R.id.profileEmail)
        val profileStreak: TextView = findViewById(R.id.profileStreak)

        val email = intent.getStringExtra("EMAIL")
//        val friendImageResId = intent.getIntExtra("FRIEND_IMAGE", R.drawable.p1)
        val streak = intent.getIntExtra("STREAK", -1)

        if(email != null){

            profileName.text = email.substringBefore("@").replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }

        }
//        profileImage.setImageResource(friendImageResId)
        profileEmail.text = email
        profileStreak.text = "Current streak: $streak 🔥"
    }
}
