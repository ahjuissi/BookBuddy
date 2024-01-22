package com.example.bookbuddy.voteView

import android.content.ContentValues
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentUserVotingBinding
import com.example.bookbuddy.databinding.UserVotingViewDesignBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserVoteFragment : Fragment() {
    private lateinit var bindingUserVoting: FragmentUserVotingBinding
    private lateinit var bindingVoteDesigne: UserVotingViewDesignBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserVoteAdapter
    private var firebaseAuth = FirebaseAuth.getInstance()
    private  val bookIds = mutableListOf<String>()
    private var winnerTitle: TextView? = null
    private var winnerVotes: TextView? = null
    private var winnerCover: ImageView? = null

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingPB: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        println("onCreateView")
        // Initialize loading indicator
        loadingDialog = createLoadingDialog()
        bindingUserVoting = FragmentUserVotingBinding.inflate(inflater, container, false)
        bindingVoteDesigne = UserVotingViewDesignBinding.inflate(inflater, container, false)

        loadingPB = bindingUserVoting.idLoadingPB

        return bindingUserVoting.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")
        showLoadingDialog() // Show loading indicator before making the database query

        val winnerReference = FirebaseDatabase.getInstance().getReference("Winner")
        winnerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dismissLoadingDialog() // Hide loading indicator once the data is retrieved
                if (snapshot.exists()) {
                    bindingUserVoting.userVotingWinner.visibility = View.VISIBLE
                    fetchWinner()
                } else {
                    setupRecyclerView()
                    fetchAllBookIdsFromVoting()
                    fetchVoteList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error checking Winner branch: ${error.message}")
            }
        })

        dismissLoadingDialog() // Hide loading indicator once the logic is completed
    }

    // Helper method to create loading indicator dialog
    private fun createLoadingDialog(): AlertDialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(R.layout.loading_indicator)
        builder.setCancelable(false)
        return builder.create()
    }

    // Helper method to show loading indicator
    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    // Helper method to dismiss loading indicator
    private fun dismissLoadingDialog() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }


    private fun checkWinnerBranchExists() {
        val winnerReference = FirebaseDatabase.getInstance().getReference("Winner")

        winnerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    fetchWinner()
                } else {
                    // Gałąź "Winner" nie istnieje, pobierz dane z głosowania

                    setupRecyclerView()
                    fetchVoteList()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error checking Winner branch: ${error.message}")
            }
        })
    }

    private fun fetchWinner() {
        val winnerReference = FirebaseDatabase.getInstance().getReference("Winner")
        winnerTitle = bindingUserVoting.voteFragmentWinner
        winnerVotes = bindingUserVoting.votesNumber
        winnerCover = bindingUserVoting.idWinnerIVBook
        val titleView = bindingVoteDesigne.titleView

        winnerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val data = mutableListOf<WinnerInfo>()

                for (childSnapshot in snapshot.children) {
                    val bookTitle = childSnapshot.child("bookTitle").getValue(String::class.java)
                    val numberOfVotes = childSnapshot.child("totalVotes").getValue(Int::class.java)
                    var thumbnail = childSnapshot.child("thumbnail").getValue(String::class.java).toString()
                    winnerTitle?.text = bookTitle
                    bookTitle?.let { title ->
                        numberOfVotes?.let { votes ->
                            thumbnail?.let { thumbnail ->
                                println(votes)
                                winnerVotes?.text = votes.toString()
                                Glide.with(this@UserVoteFragment).load(thumbnail).into(winnerCover!!)

                                val winner = WinnerInfo(title, votes.toString())
                                loadingPB.visibility = View.GONE
                                data.add(winner)
                            }
                        }
                    }
                }

                // Aktualizacja interfejsu użytkownika:
                if (data.isNotEmpty()) {
                    // Use bindingUserVoting instead of bindingVoteDesigne
                    titleView.text = data[0].bookTitle
                    titleView.append("\nTotal Votes: ${data[0].totalVotes.toString()}")
                    loadingPB.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching Winner data: ${error.message}")
                loadingPB.visibility = View.GONE
            }
        })
    }
    private fun setupRecyclerView() {
        recyclerView = bindingUserVoting.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val templist: List<VotingViewModel> = emptyList()
        val data: MutableList<VotingViewModel> = templist.toMutableList()
        adapter = UserVoteAdapter(data,requireContext(),
            onItemClick = { selectedItem -> /* Obsługa kliknięcia elementu */ },
            onNegativeVoteClick = { selectedItem -> /* Obsługa kliknięcia negatywnego głosu */ },
            onPositiveVoteClick = { selectedItem -> /* Obsługa kliknięcia pozytywnego głosu */ }
        )
        recyclerView.adapter = adapter
    }
    private fun fetchAllBookIdsFromVoting() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val bookId = childSnapshot.key
                    // bookId to nazwa (klucz) dziecka w węźle "Voting"
                    Log.d("ChildKey", bookId ?: "Key is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Error fetching children: ${error.message}")
            }
        })
    }
    private fun fetchVoteList() {
        val userId = firebaseAuth.currentUser?.uid
        userId?.let { uid ->
            val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")
            val userVotesReference = FirebaseDatabase.getInstance().getReference("Voting")

            userVotesReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(userVotesSnapshot: DataSnapshot) {
                    val data = mutableListOf<VotingViewModel>()

                    // Pobranie wszystkich książek dostępnych do głosowania
                    databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (childSnapshot in snapshot.children) {
                                val bookId = childSnapshot.key ?: ""

                                // Sprawdzenie czy użytkownik zagłosował na tę konkretną książkę
                                val userVotedSnapshot = userVotesSnapshot.child(bookId)
                                val userVoted = userVotedSnapshot.child("usersVoted").child(uid).getValue(String::class.java)
                                println(userVoted)
                                if (userVoted!="true") {
                                    val title = childSnapshot.child("title").getValue(String::class.java)
                                    val authors = childSnapshot.child("authors").getValue(String::class.java)
                                    val thumbnail=childSnapshot.child("thumbnail").getValue(String::class.java).toString()
                                    title?.let { safeTitle ->
                                        authors?.let { safeAuthors ->
                                            data.add(VotingViewModel(safeTitle, safeAuthors, 0, 0, bookId,thumbnail))
                                        }
                                    }
                                }
                            }
                            adapter.updateList(data)
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                        }
                    })
                }

                override fun onCancelled(userVotesError: DatabaseError) {
                    Log.e(ContentValues.TAG, "Error getting user votes: ", userVotesError.toException())
                }
            })
        }
    }


}