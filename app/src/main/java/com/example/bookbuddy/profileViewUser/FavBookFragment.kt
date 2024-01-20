package com.example.bookbuddy.profileViewUser

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentFavBinding
import com.example.bookbuddy.profileViewAdmin.UserAdapter
import com.example.bookbuddy.voteView.VotingViewModel
import com.example.bookbuddy.voteView.WinnerInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
    class FavBookFragment : Fragment() {
        private lateinit var bindingFavBookFragment: FragmentFavBinding
        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: FavBookAdapter
        private var firebaseAuth = FirebaseAuth.getInstance()

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            bindingFavBookFragment = FragmentFavBinding.inflate(inflater, container, false)

            bindingFavBookFragment.backBtn.setOnClickListener {
                setCurrentFragment(UserProfileFragment())
            }

            // Inicjalizacja adaptera
            adapter = FavBookAdapter(mutableListOf())

            // Ustawienie adaptera dla recyclerView
            recyclerView = bindingFavBookFragment.recyclerView
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = adapter

            booklist()
            // Inflate the layout for this fragment
            return bindingFavBookFragment.root
        }

        @SuppressLint("NotifyDataSetChanged")
        private fun booklist() {
            val userId = firebaseAuth.currentUser?.uid
            userId?.let {
                val databaseReference = FirebaseDatabase.getInstance().getReference("userInfo/${userId}/favourite")
                databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val data = mutableListOf<FavBookViewModel>()
                        for (childSnapshot in snapshot.children) {
                            val title = childSnapshot.child("title").getValue(String::class.java)
                            val publisher =
                                childSnapshot.child("authors").getValue(String::class.java)
                            val thumbnail =
                                childSnapshot.child("thumbnail").getValue(String::class.java)
                                    .toString()
                            val id = childSnapshot.child("id").getValue(String::class.java)
                            title?.let {
                                data.add(
                                    FavBookViewModel(
                                        publisher,
                                        id,
                                        thumbnail,
                                        title
                                    )
                                )
                            }
                        }
                        adapter.updateList(data)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(
                            ContentValues.TAG,
                            "Error getting documents: ",
                            databaseError.toException()
                        )
                    }
                })
            }
        }

        private fun setCurrentFragment(fragment: Fragment) =
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.flFragment, fragment)
                //  addToBackStack(null)
                commit()
            }
    }
