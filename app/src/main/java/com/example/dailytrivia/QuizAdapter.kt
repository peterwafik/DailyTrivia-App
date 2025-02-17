package com.example.dailytrivia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.content.Context
import android.content.Intent
import com.google.android.material.imageview.ShapeableImageView


class QuizAdapter(
    private val context: Context,
    private val quizList: List<Quiz>
) : RecyclerView.Adapter<QuizAdapter.QuizViewHolder>() {

    inner class QuizViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvQuizTitle: TextView = itemView.findViewById(R.id.tvQuizTitle)
        val ivBackgroundImage = itemView.findViewById<ShapeableImageView>(R.id.ivBackgroundImage)

//        val ivBackgroundImage: ImageView = itemView.findViewById(R.id.ivBackgroundImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_quiz, parent, false)
        return QuizViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizViewHolder, position: Int) {
        val quiz = quizList[position]
        holder.tvQuizTitle.text = quiz.title
        holder.ivBackgroundImage.setImageResource(quiz.imageResId)
//        holder.ivBackgroundImage.clipToOutline = true

        // Make the entire item clickable
        holder.itemView.setOnClickListener {
            // Create an intent to navigate to DailyQuizActivity
            val intent = Intent(context, DailyQuizActivity::class.java)
            // Pass the quiz ID to the next screen
            intent.putExtra("QUIZ_ID", quiz.id)
            intent.putExtra("EMAIL", quiz.email)
            // Start the activity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return quizList.size
    }
}
