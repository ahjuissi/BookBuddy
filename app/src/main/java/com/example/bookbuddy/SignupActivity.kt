package com.example.bookbuddy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.bookbuddy.databinding.ActivitySignupBinding
import java.sql.Connection
import com.example.bookbuddy.connection





class SignupActivity : AppCompatActivity() {
    internal var conn: Connection? = null
    private lateinit var bindingSignup: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignup = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)

//        bindingSignup.btnSignupSubmit.setOnClickListener {
//        val name = bindingSignup.etUserName.text.toString()
//        val surname = bindingSignup.etUserSurname.text.toString()
//        val mail = bindingSignup.etUserMail.text.toString()
//        val password = bindingSignup.etUserMail.text.toString()
//        val password2 = bindingSignup.etUserMail.text.toString()
//        val city = bindingSignup.spinnerCity.toString()
//        val role = bindingSignup.spinnerRole.toString()
//            val connection=connection()
//        }
//        bindingSignup.loginRedirectText.setOnClickListener {
//            val signupIntent = Intent(this, MainActivity::class.java)
//            startActivity(signupIntent)
//        }
    }
}


