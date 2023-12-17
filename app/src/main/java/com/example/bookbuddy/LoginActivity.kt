package com.example.bookbuddy


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookbuddy.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.shashank.sony.fancytoastlib.FancyToast


class LoginActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding
    private lateinit var firebaseAuth:FirebaseAuth



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)
        firebaseAuth=FirebaseAuth.getInstance()
        //test NavBar
        // val NavIntent = Intent(this, NavActivity::class.java)
        //startActivity(NavIntent)

        
        //    setContentView(R.layout.activity_login)
        bindingLogin.btnLoginSubmit.setOnClickListener {
            val email = bindingLogin.etUserName.text.toString() //mail
            val password = bindingLogin.etPassword.text.toString() // haslo
            if(email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        if (firebaseAuth.currentUser?.isEmailVerified == true) {
                            FancyToast.makeText(
                                this@LoginActivity,
                                "Login Successful",
                                FancyToast.LENGTH_SHORT,
                                FancyToast.SUCCESS,
                                false
                            ).show()
                            val intent = Intent(this, NavActivity::class.java)
                            startActivity(intent)
                        } else {

                            FancyToast.makeText(
                                this@LoginActivity,
                                "Please verify your email address",
                                FancyToast.LENGTH_LONG,
                                FancyToast.ERROR,
                                false
                            ).show()
                        }
                    }else {

                        FancyToast.makeText(
                            this@LoginActivity,
                            "Email or password are incorrect",
                            FancyToast.LENGTH_LONG,
                            FancyToast.ERROR,
                            false
                        ).show()
                    }
                }
            }else{
                FancyToast.makeText(
                    this@LoginActivity,
                    "Please insert all the data",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            }



        }
        bindingLogin.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

}