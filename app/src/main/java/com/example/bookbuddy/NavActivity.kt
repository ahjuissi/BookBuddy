package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.random.Random

class NavActivity : AppCompatActivity() ,SwipeRefreshLayout.OnRefreshListener{
    private lateinit var bindingMain: ActivityMainBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
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
                R.id.profile->setCurrentFragment(adminFragment)
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
        Toast.makeText(this,"Refreshed",Toast.LENGTH_SHORT).show()
        swipeRefreshLayout.isRefreshing=false
        generateAndDisplayRandomNumber()
    }
}