package com.example.bookbuddy.voteView
import android.os.Bundle
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
import com.example.bookbuddy.R
import com.example.bookbuddy.searchView.SearchFragment
import com.google.firebase.database.FirebaseDatabase
import java.util.*
class UserVoteAdapter(private var mList: MutableList<VotingViewModel>,
                    private val onItemClick: (VotingViewModel) -> Unit,
                      private val onPositiveVoteClick: (VotingViewModel) -> Unit,
                      private val onNegativeVoteClick: (VotingViewModel) -> Unit) :
    RecyclerView.Adapter<UserVoteAdapter.ViewHolder>() {
    private var selectedItem: VotingViewModel? = null
    private val database = FirebaseDatabase.getInstance()
    private val votesReference = database.reference.child("Votes")


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
                val title = item.title
                val searchFragment = SearchFragment()

                if (itemView.context is AppCompatActivity) {
                    val appCompatActivity = itemView.context as AppCompatActivity

                    val bundle = Bundle()
                    bundle.putString("title", title)
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
        val bookId = item.title
        val votesRef = bookId?.let { votesReference.child(it) }

        // Aktualizacja wartości w bazie danych
        if(voteValue==-1)
        {
            if (votesRef != null) {
                votesRef.child("bookDisikes").setValue(item.bookDislikes + voteValue)
            }
        }else
            if (votesRef != null) {
                votesRef.child("bookLikes").setValue(item.bookLikes + voteValue)
            }

        // Alternatywnie, jeśli używasz Realtime Database i istnieje wartość bookDislikes
        // votesRef.child("bookDislikes").setValue(item.bookDislikes + voteValue)
    }


    fun updateList(newList: List<VotingViewModel>) {
        mList.clear()
        mList.addAll(newList)
        notifyDataSetChanged()
    }
}

