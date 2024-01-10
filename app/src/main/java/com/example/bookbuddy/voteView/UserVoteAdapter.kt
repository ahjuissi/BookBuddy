package com.example.bookbuddy.voteView
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.R
import com.example.bookbuddy.searchView.BookDetailsActivity
import com.example.bookbuddy.searchView.SearchFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.util.*
class UserVoteAdapter(private var mList: MutableList<VotingViewModel>,
                    private val onItemClick: (VotingViewModel) -> Unit,
                      private val onPositiveVoteClick: (VotingViewModel) -> Unit,
                      private val onNegativeVoteClick: (VotingViewModel) -> Unit) :
    RecyclerView.Adapter<UserVoteAdapter.ViewHolder>() {
    private var selectedItem: VotingViewModel? = null
    private val database = FirebaseDatabase.getInstance()
    private val votesReference = database.reference.child("Voting")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_vote_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val votingItem = mList[position]
        holder.bind(votingItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val idIVBook: ImageView = itemView.findViewById(R.id.idIVBook)

        init {
                // Ustawienie nasłuchiwania kliknięć na przycisku imageViewNegative
                itemView.findViewById<ImageView>(R.id.imageViewNegative).setOnClickListener {
                    handleVoteButtonClick(adapterPosition, -1)
                }

                // Ustawienie nasłuchiwania kliknięć na przycisku imageViewPositive
                itemView.findViewById<ImageView>(R.id.imageViewPositive).setOnClickListener {
                    handleVoteButtonClick(adapterPosition, 1)
                }
            itemView.findViewById<LinearLayout>(R.id.Book).setOnClickListener {
                val item = mList[position]
                val id = item.id
                val searchFragment = SearchFragment()

                if (itemView.context is AppCompatActivity) {
                    val appCompatActivity = itemView.context as AppCompatActivity

                    val bundle = Bundle()
                    bundle.putString("id", id)
                    searchFragment.arguments = bundle

                    appCompatActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, searchFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }

        }
        fun bind(item: VotingViewModel) {
            titleView.text = item.title

            itemView.setOnClickListener {
                // Zaznacz wybrany element
                selectedItem = item
                onItemClick(item)
            }
            val thumbnailRef = FirebaseDatabase.getInstance().getReference("Voting/${item.id}/thumbnail")
            thumbnailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val thumbnail = snapshot.getValue(String::class.java)
                    thumbnail?.let {
                        Glide.with(itemView.context)
                            .load(it)
                            .into(idIVBook)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching thumbnail: ${error.message}")
                }
            })
        }

    }
    private fun handleVoteButtonClick(position: Int, voteValue: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val item = mList[position]
            updateVotesInDatabase(item, voteValue)
            mList.removeAt(position) // Usunięcie elementu z listy
            notifyDataSetChanged() // Odświeżenie widoku RecyclerView
            if(voteValue==-1)
            {
                onNegativeVoteClick.invoke(item) // Wywołanie odpowiedniej akcji
            }else
                onPositiveVoteClick.invoke(item) // Wywołanie odpowiedniej akcji
        }
    }

    private fun updateVotesInDatabase(item: VotingViewModel, voteValue: Int) {
        val bookId = item.id
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val votesReference = FirebaseDatabase.getInstance().getReference("Voting").child(bookId)

            // Aktualizacja wartości w bazie danych dla pola bookLikes lub bookDislikes
            votesReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookLikes = snapshot.child("bookLikes").getValue(Int::class.java) ?: 0
                    val bookDislikes = snapshot.child("bookDislikes").getValue(Int::class.java) ?: 0

                    val updatedLikes = if (voteValue == 1) bookLikes + 1 else bookLikes
                    val updatedDislikes = if (voteValue == -1) bookDislikes + 1 else bookDislikes

                    // Aktualizacja pola bookLikes lub bookDislikes
                    votesReference.child("bookLikes").setValue(updatedLikes)
                    votesReference.child("bookDislikes").setValue(updatedDislikes)

                    // Dodanie informacji o użytkowniku oddającym głos
                    votesReference.child("usersVoted").child(uid).setValue("true")
                }

                override fun onCancelled(error: DatabaseError) {
                    // Obsługa błędu pobierania danych z bazy danych
                    Log.e(TAG, "Error updating votes in database: ", error.toException())
                }
            })
        }
    }


    fun updateList(newList: List<VotingViewModel>) {
        mList.clear()
        mList.addAll(newList)
        notifyDataSetChanged()
    }
}

