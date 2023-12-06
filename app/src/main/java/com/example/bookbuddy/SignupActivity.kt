package com.example.bookbuddy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bookbuddy.databinding.ActivitySignupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.sql.Connection
//import com.example.bookbuddy.connection





class SignupActivity : AppCompatActivity() {
    private lateinit var bindingSignup: ActivitySignupBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        bindingSignup = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)

        bindingSignup.btnSignupSubmit.setOnClickListener {
        val name = bindingSignup.etUserName.text.toString()
        val surname = bindingSignup.etUserSurname.text.toString()
       val mail = bindingSignup.etUserMail.text.toString()
       val password = bindingSignup.etUserMail.text.toString()
        val password2 = bindingSignup.etUserMail.text.toString()
        val city = bindingSignup.spinnerCity.toString()
        val role = bindingSignup.spinnerRole.toString()
            if(name.isEmpty() || surname.isEmpty() || mail.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Please insert all the data", Toast.LENGTH_LONG).show()
            }else
            {
                if(password != password2)
                {
                    Toast.makeText(this, "Your passwords must be the same", Toast.LENGTH_LONG).show()
                }else
                {
                    val userId = firebase.push().key!!

                    val user= UserModel(userId,name,surname,mail,password,1,0)
                    firebase.child(userId).setValue(user).addOnCompleteListener {
                        Toast.makeText(this, "Data inserted good", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{err->
                        Toast.makeText(this, "Data insert fail, ${err.message}", Toast.LENGTH_LONG).show()

                    }
                }
            }


        }
        bindingSignup.loginRedirectText.setOnClickListener {
            val signupIntent = Intent(this, MainActivity::class.java)
            startActivity(signupIntent)
        }
    }
}


