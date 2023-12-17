package com.example.bookbuddy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val mList: MutableList<UserViewModel>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_view_design, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view ::  , payloads: MutableList<Any>
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val UserViewModel = mList[position]
        holder.deleteButton.setOnClickListener{
            //val documentId = mList[position].documentId
            //Log.w("Test: ", documentId)
            //deleteEvent(documentId)
            mList.removeAt(position)
            notifyDataSetChanged()
        }

        // sets the text to the textview from our itemHolder class
        holder.nameView.text = UserViewModel.name
        holder.mailView.text = UserViewModel.mail
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val nameView: TextView = itemView.findViewById(R.id.usernameView)
        val mailView: TextView = itemView.findViewById(R.id.usermailView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

}