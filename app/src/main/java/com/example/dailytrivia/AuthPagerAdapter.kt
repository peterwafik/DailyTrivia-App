package com.example.dailytrivia

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AuthPagerAdapter(private val activity: AuthActivity) : RecyclerView.Adapter<AuthPagerAdapter.AuthViewHolder>() {
    private val layouts = listOf(R.layout.activity_signin, R.layout.activity_signup)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layouts[viewType], parent, false)
        return AuthViewHolder(view)
    }

    override fun getItemCount() = layouts.size

    override fun onBindViewHolder(holder: AuthViewHolder, position: Int) {
        // No binding logic needed
    }

    override fun getItemViewType(position: Int) = position

    inner class AuthViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
