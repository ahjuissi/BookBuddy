package com.example.bookbuddy

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User

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
        holder.nameView.text = UserViewModel.name
        holder.surnameView.text = UserViewModel.surname
        holder.mailView.text = UserViewModel.mail
        holder.deleteButton.setOnClickListener{
            val userId = UserViewModel.userId // Załóżmy, że masz pole userId w UserViewModel
            val db = FirebaseFirestore.getInstance()
            // Sprawdź, czy identyfikator użytkownika nie jest null
            if (userId != null) {


                // Usuń użytkownika z bazy danych Firestore
                db.collection("userInfo")
                    .document(userId)
                    .delete()
                    .addOnSuccessListener {
                        // Usunięcie użytkownika z listy i poinformowanie adaptera
                        mList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, mList.size)
                        // Możesz również dodać komunikat potwierdzający usunięcie
                        Toast.makeText(holder.itemView.context, "Użytkownik został usunięty", Toast.LENGTH_SHORT).show()

                    }
                    .addOnFailureListener { exception ->
                        Log.e(ContentValues.TAG, "Błąd podczas usuwania użytkownika z bazy danych Firestore", exception)
                    }
            }
        }

        // sets the text to the textview from our itemHolder class
        holder.nameView.text = UserViewModel.name
        holder.surnameView.text = UserViewModel.surname
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
        val surnameView: TextView = itemView.findViewById(R.id.usersurnameView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }

}