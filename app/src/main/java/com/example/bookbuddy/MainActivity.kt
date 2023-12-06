package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Intent
import android.service.autofill.UserData
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.ActivityLoginBinding

// import kotlinx.android.synthetic.main.activity_main.*

import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value


class MainActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        //test NavBar
       // val NavIntent = Intent(this, NavActivity::class.java)
        //startActivity(NavIntent)




        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)

    //    setContentView(R.layout.activity_login)
        bindingLogin.btnLoginSubmit.setOnClickListener {
            val email = bindingLogin .etUserName.text.toString() //mail
            val password = bindingLogin.etPassword.text.toString() // haslo
            firebase.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(userSnapshot in snapshot.children){
                            val userData= userSnapshot.getValue(UserModel ::class.java)

                            if(userData != null && userData.userPassword == password){
                                Toast.makeText(this@MainActivity,"Login Successful",Toast.LENGTH_LONG).show()
                                startActivity(Intent(this@MainActivity, NavActivity::class.java))
                                finish()
                            }
                        }
                    }
                    Toast.makeText(this@MainActivity, "Login Failed", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@MainActivity,"Database Error: ${databaseError.message}",Toast.LENGTH_SHORT).show()
                }

            })


        }
        bindingLogin.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

}