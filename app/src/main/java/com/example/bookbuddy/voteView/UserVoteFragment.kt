package com.example.bookbuddy.voteView

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.databinding.FragmentUserVotingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserVoteFragment : Fragment() {
    private lateinit var bindingVote: FragmentUserVotingBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserVoteAdapter
    private var firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        bindingVote = FragmentUserVotingBinding.inflate(inflater, container, false)
        return bindingVote.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchVoteList()
    }
    private fun setupRecyclerView() {
        recyclerView = bindingVote.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val templist: List<VotingViewModel> = emptyList()
        val data: MutableList<VotingViewModel> = templist.toMutableList()
        adapter = UserVoteAdapter(data,
            onItemClick = { selectedItem -> /* Obsługa kliknięcia elementu */ },
            onNegativeVoteClick = { selectedItem -> /* Obsługa kliknięcia negatywnego głosu */ },
            onPositiveVoteClick = { selectedItem -> /* Obsługa kliknięcia pozytywnego głosu */ }
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
                        val publisher = childSnapshot.child("publisher").getValue(String::class.java)
                        title?.let { data.add(VotingViewModel(it, publisher,0,0)) }
                    }
                    adapter.updateList(data)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                }
            })
        }
    }

}