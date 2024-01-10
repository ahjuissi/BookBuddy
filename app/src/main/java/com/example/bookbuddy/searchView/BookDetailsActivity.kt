package com.example.bookbuddy.searchView


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookbuddy.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.bumptech.glide.request.target.Target


class BookDetailsActivity : AppCompatActivity() {

    // creating variables for strings,text view,
    // image views and button.
    private lateinit var bookIV: ImageView
    private lateinit var authorsTV: TextView
    private lateinit var publisherDateTV: TextView
    private lateinit var titleTV: TextView
    private lateinit var descTV: TextView
    private lateinit var subTV: TextView
    private lateinit var subPplTV: TextView
    private lateinit var subTimeTV: TextView
    private lateinit var previewBtn: Button
    private lateinit var addBtn: Button


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)

        // initializing our variables.
        bookIV = findViewById(R.id.idIVbook)
        authorsTV = findViewById(R.id.idTVauthors)
        publisherDateTV = findViewById(R.id.idTVPublishDate)
        titleTV = findViewById(R.id.idTVTitle)
        descTV = findViewById(R.id.idTVDescription)
        subTV = findViewById(R.id.idTVSubjects)
        subPplTV = findViewById(R.id.idTVSubjectPpl)
        subTimeTV = findViewById(R.id.idTVSubjectTime)
        previewBtn = findViewById(R.id.idBtnPreview)
        //Add button
        addBtn = findViewById(R.id.idBtnAdd)

        // getting the data which we have passed from our adapter class.
        val title = intent.getStringExtra("title")
        val id = intent.getStringExtra("id").toString()
        val olid = intent.getStringExtra("olid").toString()
        val cleanedOlid = olid?.substringAfterLast("/") ?: ""
        val authors = intent.getStringExtra("authors")?.split(", ")?.toMutableList() //ZMIANA
        val description = intent.getStringExtra("description")
        val previewLink = "https://openlibrary.org$olid"
        val subjects = intent.getStringExtra("subjects")?.split(", ")?.toMutableList()
        val subject_people=  intent.getStringExtra("subjectPeople")?.split(", ")?.toMutableList()
        val subject_times = intent.getStringExtra("subjectTimes")?.split(", ")?.toMutableList()
        val publishedDate = intent.getStringExtra("publishedDate")


        titleTV.text = title
        val authorsArrayList: ArrayList<String> = ArrayList()
        authors?.let {
            val maxAuthors = minOf(authors.size, 4) // Ograniczenie do maksymalnie 4 autorów
            for (j in 0 until maxAuthors) {
                authorsArrayList.add(authors[j])
            }
        }
        val authorsText = authorsArrayList.joinToString(", ") // Łączenie autorów przecinkami
        authorsTV.text = authorsText

        val thumbnailUrl = "https://covers.openlibrary.org/b/id/${id}-L.jpg"
        loadThumbnailWithRetry(id, cleanedOlid,thumbnailUrl,bookIV)


        val subText = subjects!!.joinToString(", ")
        subTV.text = subText
        val subPplText = subject_people!!.joinToString(", ")
        subPplTV.text = subPplText
        val subTimeText = subject_times!!.joinToString(", ")
        subTimeTV.text = subTimeText

        publisherDateTV.text = "$publishedDate"
        descTV.text = description

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
            //TODO: do głosowania
            bookData["title"] = title.toString()
//            bookData["publisher"] = publisher.toString()
            bookData["authors"] = authors.toString()
//            bookData["thumbnail"]=thumbnail.toString()
            bookData["id"]=id
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

    }
    private fun loadThumbnailWithRetry(
        id: String,
        cleanedOlid: String,
        url: String,
        bookIV: ImageView,
        retryCount: Int = 3
    ) {
        val handler = Handler(Looper.getMainLooper())

        handler.post {
            Glide.with(this@BookDetailsActivity) // Zmieniłem `requireContext()` na `this@BookDetailsActivity` ponieważ jesteśmy w Activity, a nie w fragmencie
                .load(url)
                .apply(RequestOptions().error(R.drawable.baseline_broken_image_24))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        var nextUrl = url
                        if (retryCount > 0) {
                            val newRetryCount = retryCount - 1

                            nextUrl = when {
                                url.contains("/id/") -> {
                                    "https://covers.openlibrary.org/b/olid/${cleanedOlid}-L.jpg"
                                }
                                url.contains("/olid/") -> {
                                    url
                                }
                                else -> {
                                    "https://covers.openlibrary.org/b/id/${id}-L.jpg"
                                }
                            }

                            loadThumbnailWithRetry(id, cleanedOlid, nextUrl, bookIV, newRetryCount)
                        }

                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .into(bookIV)
        }
    }


}
