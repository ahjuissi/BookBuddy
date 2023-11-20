package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.ActivityLoginBinding

// import kotlinx.android.synthetic.main.activity_main.*

import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding

    private lateinit var bindingMain: ActivityMainBinding

    private fun setCurrentFragment(fragment: Fragment)=
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingMain = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindingMain.root)
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

//        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(bindingLogin.root)

//        setContentView(R.layout.activity_login)
//        bindingLogin.btnLoginSubmit.setOnClickListener {}
//        bindingLogin.signupRedirectText.setOnClickListener {
//            val signupIntent = Intent(this, SignupActivity::class.java)
//            startActivity(signupIntent)
//        }
    }
}