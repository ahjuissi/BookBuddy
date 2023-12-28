package com.example.bookbuddy.authenticationPart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import android.widget.TextView
import com.example.bookbuddy.R

// import kotlinx.android.synthetic.main.activity_main.*

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
        setContentView(R.layout.activity_loading)

        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)
        val textView = findViewById<TextView>(R.id.textView)

        progressBar.max = 100
        progressBar.scaleY=3F
        val anim= ProgressBarAnimation(
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
