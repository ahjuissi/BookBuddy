package com.example.bookbuddy.searchView


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.bookbuddy.R
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        val authors = intent.getStringExtra("authors")?.split(", ")?.toMutableList() //ZMIANA
        val publishedDate = intent.getStringExtra("publishedDate")
        val description = intent.getStringExtra("description")
        val id = intent.getStringExtra("id")
        val pageCount = intent.getIntExtra("pageCount", 0)
        val thumbnail = intent.getStringExtra("thumbnail")
        val previewLink = intent.getStringExtra("previewLink")
        val infoLink = intent.getStringExtra("infoLink")
        val buyLink = intent.getStringExtra("buyLink")

        // after getting the data we are setting
        // that data to our text views and image view.
        titleTV.text = title
        subtitleTV.text = subtitle
        publisherTV.text = "$publisher\n${authors?.joinToString("\n")}"
        publisherDateTV.text = "Published On : $publishedDate"
        descTV.text = description
        pageTV.text = "No Of Pages : $pageCount"
        Glide.with(this@BookDetailsActivity)
            .load(thumbnail)
            .into(bookIV)


        addBtn = findViewById(R.id.idBtnAdd)

        addBtn.visibility = View.GONE // Ukryj przycisk domyślnie

        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { uid ->
            val userInfoRef = FirebaseDatabase.getInstance().getReference("userInfo").child(uid)

            userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRole = snapshot.child("role").getValue(String::class.java)

                    if (userRole == "Admin") {
                        // Jeśli użytkownik ma rolę "Admin", pokaż przycisk
                        addBtn.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }


        addBtn.setOnClickListener {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Voting")
            val bookData = HashMap<String, Any>()
            bookData["title"] = title.toString()
            bookData["publisher"] = publisher.toString()
            bookData["authors"] = authors.toString()
            bookData["thumbnail"]=thumbnail.toString()
            bookData["id"]=id.toString()
            databaseReference.child(id.toString()).setValue(bookData)
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
