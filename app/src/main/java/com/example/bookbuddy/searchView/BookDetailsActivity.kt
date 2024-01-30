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
import com.shashank.sony.fancytoastlib.FancyToast


class BookDetailsActivity : AppCompatActivity() {

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
    private lateinit var favBtn: ImageView
    private lateinit var adminCity:String
    private lateinit var userId:String
    override fun onStart() {
        super.onStart()
        addBtn = findViewById(R.id.idBtnAdd)
        val firebaseAuth = FirebaseAuth.getInstance()
          userId = firebaseAuth.currentUser?.uid.toString()
        userId?.let { uid ->
            val userInfoRef = FirebaseDatabase.getInstance().getReference("userInfo").child(uid)
            userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRole = snapshot.child("role").getValue(String::class.java)
                     adminCity= snapshot.child("city").getValue(String::class.java).toString()

                    if (userRole == "Admin") {
                        println("admin")
                        addBtn.visibility = View.VISIBLE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        val firebaseAuth = FirebaseAuth.getInstance()
        userId = firebaseAuth.currentUser?.uid.toString()
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
        favBtn = findViewById(R.id.idBtnFav)


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

        checkIfLiked(favBtn, userId,cleanedOlid)
        titleTV.text = title
        val authorsArrayList: ArrayList<String> = ArrayList()
        authors?.let {
            val maxAuthors = minOf(authors.size, 4)
            for (j in 0 until maxAuthors) {
                authorsArrayList.add(authors[j])
            }
        }
        val authorsText = authorsArrayList.joinToString(", ")
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
        addBtn.visibility = View.GONE
        addBtn.setOnClickListener {
            if (::adminCity.isInitialized) {
                val databaseReference =
                    FirebaseDatabase.getInstance().getReference("Voting").child(adminCity)
                val bookData = HashMap<String, Any>()
                bookData["title"] = title.toString()
                bookData["authors"] = authors.toString()
                bookData["id"] = cleanedOlid
                bookData["thumbnail"] = thumbnailUrl
                databaseReference.child(cleanedOlid).setValue(bookData)
            }
        }

        favBtn.setOnClickListener{
            val firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser?.uid
            userId?.let { uid ->
                val favBook = FirebaseDatabase.getInstance().getReference("userInfo").child(uid).child("favourite").child(cleanedOlid)
                favBook.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.exists()) {
                            favBook.removeValue()
                                .addOnSuccessListener {

                                    FancyToast.makeText(
                                        this@BookDetailsActivity,
                                        "Removed",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.SUCCESS,
                                        false
                                    ).show()
                                    checkIfLiked(favBtn,uid,cleanedOlid)
                                }
                                .addOnFailureListener { e ->
                                    FancyToast.makeText(
                                        this@BookDetailsActivity,
                                        "Failed to remove: $e",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.ERROR,
                                        false
                                    ).show()

                                }
                        } else {
                            val bookData = HashMap<String, Any>()
                            bookData["title"] = title.toString()
                            bookData["authors"] = authors.toString()
                            bookData["id"]=cleanedOlid
                            bookData["thumbnail"]=thumbnailUrl
                            favBook.setValue(bookData)
                                .addOnSuccessListener {

                                    FancyToast.makeText(
                                        this@BookDetailsActivity,
                                        "Added",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.SUCCESS,
                                        false
                                    ).show()
                                    checkIfLiked(favBtn,uid,cleanedOlid)
                                }
                                .addOnFailureListener { e ->
                                    FancyToast.makeText(
                                        this@BookDetailsActivity,
                                        "Failed to add: $e",
                                        FancyToast.LENGTH_SHORT,
                                        FancyToast.ERROR,
                                        false
                                    ).show()
                                }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
        }
        previewBtn.setOnClickListener {
            if (previewLink.isNullOrEmpty()) {
                Toast.makeText(
                    this@BookDetailsActivity,
                    "No preview Link present",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            val uri: Uri = Uri.parse(previewLink)
            val i = Intent(Intent.ACTION_VIEW, uri)
            startActivity(i)
        }

    }
    private fun checkIfLiked(favBtn: ImageView, uid: String, cleanedOlid: String) {
        val favBook = FirebaseDatabase.getInstance().getReference("userInfo").child(uid).child("favourite").child(cleanedOlid)
        favBook.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    favBtn.setImageResource(R.drawable.baseline_star_gold)
                } else {
                    favBtn.setImageResource(R.drawable.baseline_star_border)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@BookDetailsActivity,
                    "Error: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
            Glide.with(this@BookDetailsActivity)
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
