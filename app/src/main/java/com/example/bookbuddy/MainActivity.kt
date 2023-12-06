package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.service.autofill.UserData
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import com.shashank.sony.fancytoastlib.FancyToast
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        setContentView(R.layout.activity_loading)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val textView = findViewById<TextView>(R.id.textView)

        progressBar.max = 100
        progressBar.scaleY=3F
        val anim=ProgressBarAnimation(
            this,progressBar,textView,0F,100F

        )
        anim.duration=8000
        progressBar.animation=anim

    }


    class ProgressBarAnimation (
        var context: Context,
        var progressBar:ProgressBar,
        var textView:TextView,
        var from:Float,
        var to:Float
    ) : Animation()

    {

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            val value = from + (to - from) * interpolatedTime
            progressBar.progress=value.toInt()
            textView.text="Loding ${value.toInt()} %"
            if (value ==to)
            {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }
        }


    }
}
