package com.example.dailytrivia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuizHistoryAdapter(private val quizHistoryList: List<QuizResult>) :
    RecyclerView.Adapter<QuizHistoryAdapter.QuizHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_quiz_result, parent, false)
        return QuizHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: QuizHistoryViewHolder, position: Int) {
        val quizResult = quizHistoryList[position]
        holder.bind(quizResult)
    }

    override fun getItemCount() = quizHistoryList.size

    class QuizHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        private val tvBadge: TextView = itemView.findViewById(R.id.tvBadge)
        private val tvType: TextView = itemView.findViewById(R.id.tvType)

        fun bind(quizResult: QuizResult) {
            tvCategory.text = quizResult.category
            tvScore.text = "Score: %.2f%%".format(quizResult.score)
            tvBadge.text = "Number of Questions: ${quizResult.numberOfQuestions.toString()}"
            tvType.text = "Type: ${quizResult.type}"
        }
    }
}
