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
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentVotingListBinding
import com.example.bookbuddy.profileViewAdmin.AdminFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VoteFragment : Fragment() {
    private lateinit var bindingVoteList: FragmentVotingListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VotingAdapter
    lateinit var delete: Button
    private var firebaseAuth = FirebaseAuth.getInstance()
    lateinit var userCity:String
    private lateinit var bookTitle:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingVoteList = FragmentVotingListBinding.inflate(inflater, container, false)
        delete = bindingVoteList.winner
        recyclerView = bindingVoteList.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return bindingVoteList.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            val userInfoReference = FirebaseDatabase.getInstance().getReference("userInfo").child(userId)
            userInfoReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userCity = snapshot.child("city").getValue(String::class.java).toString()
                    delete.setOnClickListener {
                        checkWinnerTableExistence()
                        setCurrentFragment(AdminFragment())
                    }
                    val adminFragment= AdminFragment()
                    bindingVoteList.backBtn.setOnClickListener {
                        setCurrentFragment(adminFragment)
                    }
                    setupRecyclerView()
                    fetchVoteList()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("VoteFragment", "Error fetching user city: ", databaseError.toException())
                }
            })
        }

    }

    private fun checkWinnerTableExistence() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Winner").child(userCity)
        val databaseVoting = FirebaseDatabase.getInstance().getReference("Voting").child(userCity)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Log.d("VoteFragment", "Winner table does not exist.")
                    val votingReference = FirebaseDatabase.getInstance().getReference("Voting").child(userCity)

                    votingReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(votingSnapshot: DataSnapshot) {
                            var maxLikes = 0
                            var thumbnail=""
                            var winnerBookId = ""
                            val userId = firebaseAuth.currentUser?.uid ?: ""
                            val winnerData = mutableListOf<WinnerInfo>()

                            for (votingChildSnapshot in votingSnapshot.children) {
                                val bookId = votingChildSnapshot.child("id").getValue(String::class.java)
                                    bookTitle = votingChildSnapshot.child("title").getValue(String::class.java)
                                        .toString()
                                 thumbnail = votingChildSnapshot.child("thumbnail").getValue(String::class.java).toString()
                                var totalLikes = 0
                                var totalDislikes = 0

                                votingChildSnapshot.child("bookLikes").getValue(Int::class.java)?.let {
                                    totalLikes += it
                                }

                                votingChildSnapshot.child("bookDislikes").getValue(Int::class.java)?.let {
                                    totalDislikes += it
                                }

                                if (totalLikes > maxLikes) {
                                    maxLikes = totalLikes
                                    winnerBookId = bookId ?: ""
                                }
                            }
                            databaseVoting.removeValue()
                            winnerBookId.takeIf { it.isNotEmpty() }?.let {
                                val winner = WinnerInfo(userId, userCity, it, maxLikes, bookTitle, thumbnail)
                                winnerData.add(winner)
                                saveWinnerInfoToDatabase(winnerData)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                        }
                    })
                } else {
                    databaseReference.removeValue()
                        .addOnSuccessListener {
                            Log.d("VoteFragment", "Winner table deleted successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e("VoteFragment", "Error deleting Winner table: $e")
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("VoteFragment", "Error checking Winner table existence: ", databaseError.toException())
            }
        })
    }
    private fun saveWinnerInfoToDatabase(winnerData: List<WinnerInfo>) {
        for (winner in winnerData) {
            val userId = winner.userId
            val bookId = winner.bookId
            val totalLikes = winner.totalVotes
            val city = winner.city

            val winnerReference = FirebaseDatabase.getInstance().getReference("Winner").child(city).child(bookId.toString())

            winnerReference.setValue(winner)
                .addOnSuccessListener {
                    Log.d("VoteFragment", "Winner info for book $bookId saved successfully.")
                }
                .addOnFailureListener { e ->
                    Log.e("VoteFragment", "Error saving winner info for book $bookId: $e")
                }
        }
    }

    private fun setupRecyclerView() {
        val templist: List<VotingViewModel> = emptyList()
        val data: MutableList<VotingViewModel> = templist.toMutableList()
        adapter = VotingAdapter(data,
            onItemClick = { selectedItem ->},
            onDeleteClick = { selectedItem -> deleteItemFromDatabase(selectedItem) }
        )
        recyclerView.adapter = adapter
    }

    private fun fetchVoteList() {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Voting").child(userCity)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val data = mutableListOf<VotingViewModel>()
                    for (childSnapshot in snapshot.children) {
                        val title = childSnapshot.child("title").getValue(String::class.java)
                        val publisher = childSnapshot.child("authors").getValue(String::class.java)
                        val thumbnail=childSnapshot.child("thumbnail").getValue(String::class.java).toString()
                        val id=childSnapshot.child("id").getValue(String::class.java)
                        title?.let { data.add(VotingViewModel(it, publisher,0,0, id.toString(),thumbnail,userCity)) }
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
        val databaseReference = FirebaseDatabase.getInstance().getReference("Voting").child(userCity)
        val titleToDelete = title.title
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
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }


}

