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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //test NavBar
        val NavIntent = Intent(this, NavActivity::class.java)
        startActivity(NavIntent)



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