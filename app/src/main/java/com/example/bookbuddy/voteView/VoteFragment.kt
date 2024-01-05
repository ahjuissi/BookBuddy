package com.example.bookbuddy.voteView

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.databinding.FragmentUserVotingBinding
import com.example.bookbuddy.databinding.FragmentVotingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VoteFragment : Fragment() {
    private lateinit var bindingVoteList: FragmentVotingBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VotingAdapter
    lateinit var delete: Button
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingVoteList = FragmentVotingBinding.inflate(inflater, container, false)
        return bindingVoteList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchVoteList()
    }

    private fun setupRecyclerView() {
        recyclerView = bindingVoteList.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val templist: List<VotingViewModel> = emptyList()
        val data: MutableList<VotingViewModel> = templist.toMutableList()
        adapter = VotingAdapter(data,
            onItemClick = { selectedItem -> /* Obsługa kliknięcia elementu */ },
            onDeleteClick = { selectedItem -> deleteItemFromDatabase(selectedItem) } // Usunięcie elementu z listy
        )
        recyclerView.adapter = adapter
    }

    private fun fetchVoteList() {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = mutableListOf<VotingViewModel>()
                    for (childSnapshot in snapshot.children) {
                        val title = childSnapshot.child("title").getValue(String::class.java)
                        val publisher = childSnapshot.child("authors").getValue(String::class.java)
                        title?.let { data.add(VotingViewModel(it, publisher,0,0,"")) }
                    }
                    adapter.updateList(data)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                }
            })
        }
    }

    private fun deleteItemFromDatabase(title: VotingViewModel) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")
        val titleToDelete = title.title // Pobranie tytułu do usunięcia
        databaseReference.orderByChild("title").equalTo(titleToDelete)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (childSnapshot in snapshot.children) {
                        childSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "Error deleting document: ", error.toException())
                }
            })
    }
}

