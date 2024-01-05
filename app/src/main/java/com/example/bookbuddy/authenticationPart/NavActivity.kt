package com.example.bookbuddy.authenticationPart

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bookbuddy.profileViewAdmin.AdminFragment
import com.example.bookbuddy.R
import com.example.bookbuddy.searchView.SearchFragment
import com.example.bookbuddy.profileViewUser.UserProfileFragment
import com.example.bookbuddy.voteView.VoteFragment
import com.example.bookbuddy.homeView.HomeFragment
import com.example.bookbuddy.voteView.UserVoteFragment
import kotlin.random.Random
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore

class NavActivity : AppCompatActivity(){
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var role:String
    private var db= Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        firebaseAuth=FirebaseAuth.getInstance()
        fetchUserInfo()
        val homeFragment= HomeFragment()
        val voteFragment= UserVoteFragment()
        val searchFragment= SearchFragment()
        val adminFragment= AdminFragment()
        val userFragment= UserProfileFragment()
//        val profileFragment=ProfileFragment()
        setCurrentFragment(homeFragment)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.vote -> setCurrentFragment(voteFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.profile -> {
                    if (role == "Admin") {
                        setCurrentFragment(adminFragment)
                    } else {
                        setCurrentFragment(userFragment)
                    }
                }

//                R.id.profile->setCurrentFragment(profileFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    private fun fetchUserInfo() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("userInfo").child(userId)
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val fetchedRole = dataSnapshot.child("role").getValue(String::class.java)
                        fetchedRole?.let {
                            role = it // Przypisanie pobranej roli do zmiennej globalnej
                            // Tutaj możesz wykorzystać zmienną role według potrzeb
                        } }else {
                        Log.d(TAG, "No such document")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d(TAG, "get failed with ", databaseError.toException())
                }
            })
        }
    }
}

