package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

// import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_signup)
        setContentView(R.layout.activity_main)

        val homeFragment=HomeFragment()
        val voteFragment=VoteFragment()
        val searchFragment=SearchFragment()
        val profileFragment=ProfileFragment()

        setCurrentFragment(homeFragment)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item->
            when(item.itemId){
                R.id.home->setCurrentFragment(homeFragment)
                R.id.vote->setCurrentFragment(voteFragment)
                R.id.search->setCurrentFragment(searchFragment)
                R.id.profile->setCurrentFragment(profileFragment)
            }
            true
        }
    }
    private fun setCurrentFragment(fragment:Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

}