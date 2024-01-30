package com.example.bookbuddy.profileViewAdmin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.R

class UserAdapter(private val mList: MutableList<UserViewModel>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_view_design, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val UserViewModel = mList[position]
        holder.nameView.text = UserViewModel.name
        holder.surnameView.text = UserViewModel.surname
        holder.mailView.text = UserViewModel.mail

    }

    override fun getItemCount(): Int {
        return mList.size
    }
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val nameView: TextView = itemView.findViewById(R.id.usernameView)
        val mailView: TextView = itemView.findViewById(R.id.usermailView)
        val surnameView: TextView = itemView.findViewById(R.id.usersurnameView)
    }

}