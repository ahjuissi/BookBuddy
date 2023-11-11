package com.example.bookbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookbuddy.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var bindingSignup: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignup = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)

        bindingSignup.btnSignupSubmit.setOnClickListener {

        }
        bindingSignup.loginRedirectText.setOnClickListener {
            val signupIntent = Intent(this, MainActivity::class.java)
            startActivity(signupIntent)
        }
    }
}