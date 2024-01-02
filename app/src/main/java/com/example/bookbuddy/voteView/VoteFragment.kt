package com.example.bookbuddy.voteView

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition.ViewAdapter
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentUserListBinding
import com.example.bookbuddy.databinding.FragmentVoteBinding
import com.example.bookbuddy.databinding.FragmentVotingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore


class VoteFragment : Fragment() {
    private lateinit var bindingVoteList: FragmentVotingBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ViewAdapter
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingVoteList = FragmentVotingBinding.inflate(inflater, container, false)
        userList()
        return bindingVoteList.root
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun userList() {
        recyclerView = bindingVoteList.recyclerView

        val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")
        val templist: List<VotingViewModel> = emptyList()
        val data: MutableList<VotingViewModel> = templist.toMutableList()
        adapter = VotingAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter as VotingAdapter

        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

                        userId?.let {
                            FirebaseDatabase.getInstance().getReference("Voting")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            val title = childSnapshot.child("title").getValue(String::class.java)
                                            val publisher = childSnapshot.child("publisher").getValue(String::class.java)
                                            data.add(VotingViewModel(title, publisher))
                                        }
                                        (adapter as VotingAdapter).notifyDataSetChanged()
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                                    }
                                })
                        }
                    }




    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            //  addToBackStack(null)
            commit()
        }




}
