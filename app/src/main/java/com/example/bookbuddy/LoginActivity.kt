package com.example.bookbuddy


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookbuddy.databinding.ActivityLoginBinding
import com.example.bookbuddy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.protobuf.Value
import com.shashank.sony.fancytoastlib.FancyToast


class LoginActivity : AppCompatActivity() {

    private lateinit var bindingLogin: ActivityLoginBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingLogin = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(bindingLogin.root)
        val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        //test NavBar
        // val NavIntent = Intent(this, NavActivity::class.java)
        //startActivity(NavIntent)






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
                                FancyToast.makeText(this@LoginActivity,"Login Successful",FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show()
                                startActivity(Intent(this@LoginActivity, NavActivity::class.java))
                                finish()
                            }
                        }
                    }
                    FancyToast.makeText(this@LoginActivity, "Login Failed", FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    FancyToast.makeText(this@LoginActivity,"Database Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT).show()
                }

            })


        }
        bindingLogin.signupRedirectText.setOnClickListener {
            val signupIntent = Intent(this, SignupActivity::class.java)
            startActivity(signupIntent)
        }
    }

}