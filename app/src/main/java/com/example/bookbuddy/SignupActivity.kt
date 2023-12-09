package com.example.bookbuddy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Spinner
import android.widget.Toast
import com.example.bookbuddy.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.shashank.sony.fancytoastlib.FancyToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import java.sql.Connection
//import com.example.bookbuddy.connection





class SignupActivity : AppCompatActivity() {
    private lateinit var bindingSignup: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingSignup = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)
        firebaseAuth = FirebaseAuth.getInstance()
        bindingSignup.btnSignupSubmit.setOnClickListener {
            val name = bindingSignup.etUserName.text.toString()
            val surname = bindingSignup.etUserSurname.text.toString()
            val mail = bindingSignup.etUserMail.text.toString()
            val password = bindingSignup.etUserMail.text.toString()
            val password2 = bindingSignup.etUserMail.text.toString()
            val spinner1: Spinner = bindingSignup.spinnerCity
            val city: String = spinner1.selectedItem.toString()
            val spinner2: Spinner = bindingSignup.spinnerRole
            val role: String = spinner2.selectedItem.toString()

            if (name.isEmpty() || surname.isEmpty() || mail.isEmpty() || password.isEmpty() || city.isEmpty() || role.isEmpty()) {
                FancyToast.makeText(
                    this@SignupActivity,
                    "Please insert all the data",
                    FancyToast.LENGTH_SHORT,
                    FancyToast.ERROR,
                    false
                ).show()
            } else {
                if (password != password2) {
                    FancyToast.makeText(
                        this,
                        "Your passwords must be the same",
                        FancyToast.LENGTH_SHORT,
                        FancyToast.ERROR,
                        false
                    ).show()
                } else {

                    firebaseAuth.createUserWithEmailAndPassword(mail, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                val userId=firebaseAuth.currentUser?.uid
                                val userMap= hashMapOf(
                                    "userId" to userId,
                                    "name" to name,
                                    "surname" to surname,
                                    "city" to city,
                                    "role" to role

                                )
                                db.collection("userInfo").document().set(userMap)
                                var intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                FancyToast.makeText(
                                    this,
                                    it.exception.toString(),
                                    FancyToast.LENGTH_SHORT,
                                    FancyToast.ERROR,
                                    false
                                ).show()
                            }
                        }


                }
            }
        }
        bindingSignup.loginRedirectText.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }
}





