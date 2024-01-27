package com.example.bookbuddy.profileViewAdmin

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
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentUserListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserListFragment : Fragment() {
    private lateinit var bindingUserList: FragmentUserListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingUserList = FragmentUserListBinding.inflate(inflater, container, false)
        val adminFragment= AdminFragment()
            //    val db = FirebaseFirestore.getInstance()
      //  val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("userInfo")
                .child(userId)

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userCity = dataSnapshot.child("city").getValue(String::class.java)
                        bindingUserList.userList.text = "User List of $userCity"
                    } else {
                        // Handle situation where data doesn't exist
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }

        bindingUserList.backBtn.setOnClickListener {
            setCurrentFragment(adminFragment)
        }

        userList()
        // Inflate the layout for this fragment
        return bindingUserList.root
        //return inflater.inflate(R.layout.fragment_user_list, container, false)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun userList() {
        recyclerView = bindingUserList.recyclerView
        val databaseReference = FirebaseDatabase.getInstance().getReference("userInfo")
        val templist: List<UserViewModel> = emptyList()
        val data: MutableList<UserViewModel> = templist.toMutableList()
        adapter = UserAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            databaseReference.child(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userCity = dataSnapshot.child("city")
                            .getValue(String::class.java)
                        userCity?.let {
                            FirebaseDatabase.getInstance().getReference("userInfo")
                                .orderByChild("city")
                                .equalTo(userCity)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (childSnapshot in snapshot.children) {
                                            val id = childSnapshot.child("userId")
                                                .getValue(String::class.java)
                                            val name = childSnapshot.child("name")
                                                .getValue(String::class.java)
                                            val surname = childSnapshot.child("surname")
                                                .getValue(String::class.java)
                                            val mail = childSnapshot.child("mail")
                                                .getValue(String::class.java)
                                            data.add(UserViewModel(id, name, surname, mail))
                                        }
                                        adapter.notifyDataSetChanged()
                                    }

                                    override fun onCancelled(databaseError: DatabaseError) {
                                        Log.w(ContentValues.TAG, "Error getting documents: ", databaseError.toException())
                                    }
                                })
                        }
                    } else {
                        Log.d(ContentValues.TAG, "No such document")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w(ContentValues.TAG, "Error getting document: ", databaseError.toException())
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
