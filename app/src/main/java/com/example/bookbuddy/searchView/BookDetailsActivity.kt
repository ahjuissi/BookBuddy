package com.example.bookbuddy.searchView


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookbuddy.R
import com.bumptech.glide.Glide

class BookDetailsActivity : AppCompatActivity() {

    // creating variables for strings,text view,
    // image views and button.
    lateinit var titleTV: TextView
    lateinit var subtitleTV: TextView
    lateinit var publisherTV: TextView
    lateinit var descTV: TextView
    lateinit var pageTV: TextView
    lateinit var publisherDateTV: TextView
    lateinit var previewBtn: Button
    lateinit var buyBtn: Button
    lateinit var bookIV: ImageView
    lateinit var addBtn: Button


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_book_details)

        // initializing our variables.
        titleTV = findViewById(R.id.idTVTitle)
        subtitleTV = findViewById(R.id.idTVSubTitle)
        publisherTV = findViewById(R.id.idTVpublisher)
        descTV = findViewById(R.id.idTVDescription)
        pageTV = findViewById(R.id.idTVNoOfPages)
        publisherDateTV = findViewById(R.id.idTVPublishDate)
        previewBtn = findViewById(R.id.idBtnPreview)
        buyBtn = findViewById(R.id.idBtnBuy)
        bookIV = findViewById(R.id.idIVbook)

        //Add button
        addBtn = findViewById(R.id.idBtnAdd)

        // getting the data which we have passed from our adapter class.
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("subtitle")
        val publisher = intent.getStringExtra("publisher")
        val publishedDate = intent.getStringExtra("publishedDate")
        val description = intent.getStringExtra("description")
        val pageCount = intent.getIntExtra("pageCount", 0)
        val thumbnail = intent.getStringExtra("thumbnail")
        val previewLink = intent.getStringExtra("previewLink")
        val infoLink = intent.getStringExtra("infoLink")
        val buyLink = intent.getStringExtra("buyLink")

        // after getting the data we are setting
        // that data to our text views and image view.
        titleTV.text = title
        subtitleTV.text = subtitle
        publisherTV.text = publisher
        publisherDateTV.text = "Published On : $publishedDate"
        descTV.text = description
        pageTV.text = "No Of Pages : $pageCount"
        Glide.with(this@BookDetailsActivity)
            .load(thumbnail)
            .into(bookIV)



        //TODO: ADD button , spr czy można zmienic admin = visibility.true itp
        addBtn = findViewById(R.id.idBtnAdd)
        addBtn.setOnClickListener {
            println("git .")
        }

        // adding on click listener for our preview button.
        previewBtn.setOnClickListener {
            if (previewLink.isNullOrEmpty()) {
                // below toast message is displayed
                // when preview link is not present.
                Toast.makeText(
                    this@BookDetailsActivity,
                    "No preview Link present",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            // if the link is present we are opening
            // that link via an intent.
            val uri: Uri = Uri.parse(previewLink)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }

        // adding click listener for buy button
        buyBtn.setOnClickListener {
            if (buyLink.isNullOrEmpty()) {
                // below toast message is displaying
                // when buy link is empty.
                Toast.makeText(
                    this@BookDetailsActivity,
                    "No buy page present for this book",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // if the link is present we are opening
            // the link via an intent.
            val uri = Uri.parse(buyLink)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }

    }
}
