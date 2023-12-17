package com.example.bookbuddy

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.random.Random
import com.google.firebase.Firebase
import com.shashank.sony.fancytoastlib.FancyToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class NavActivity : AppCompatActivity() ,SwipeRefreshLayout.OnRefreshListener{
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
        firebaseAuth=FirebaseAuth.getInstance()
        val role:String = "admin"
        fetchUserInfo()
        val homeFragment=HomeFragment()
        val voteFragment=VoteFragment()
        val searchFragment=SearchFragment()
        val adminFragment=AdminFragment()
//        val profileFragment=ProfileFragment()
        swipeRefreshLayout=findViewById(R.id.swipe_ly)
        swipeRefreshLayout.setOnRefreshListener(this)
        setCurrentFragment(homeFragment)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.vote->setCurrentFragment(voteFragment)
                R.id.search->setCurrentFragment(searchFragment)
                if(role=="admin")
                    R.id.profile else TODO() ->setCurrentFragment(adminFragment)
           //     else
                   // R.id.profile->setCurrentFragment(userFragment)

//                R.id.profile->setCurrentFragment(profileFragment)
            }
            true
        }

        generateAndDisplayRandomNumber()
    }


        private fun generateAndDisplayRandomNumber() {
            // Generuj losową liczbę w zakresie od 1 do 100 (możesz dostosować zakres według potrzeb)
            val randomNumber = Random.nextInt(1, 101)

            // Znajdź TextView za pomocą jego ID
            println(randomNumber)
            // Ustaw wygenerowaną liczbę jako tekst w TextView
        }


    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing=false
    }
    private fun fetchUserInfo()
    {
        val userId= firebaseAuth.currentUser?.uid
        if (userId != null) {
            db.collection("userInfo").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        // Retrieve the role from the document data
                        val roleFromDatabase = document.getString("role")

                        // Check if the role is "admin"
                        if (roleFromDatabase == "Admin") {
                            // The user has admin role
                            Log.d(TAG, "User has admin role")
                            // You can perform actions specific to admin users here
                        } else {
                            // The user does not have admin role
                            Log.d(TAG, "User does not have admin role")
                            // You can perform actions specific to non-admin users here
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }
}

