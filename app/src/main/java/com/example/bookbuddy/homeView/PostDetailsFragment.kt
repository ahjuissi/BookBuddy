package com.example.bookbuddy.homeView

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.FragmentPostDetailsBinding
import android.app.ProgressDialog
import android.text.format.DateFormat
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.firestore

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostDetailsFragment : Fragment() {
    private var hisuid: String = ""
    private var ptime: String = ""
    private var myuid: String = ""
    private var myname: String = ""
    private var myemail: String = ""
    private var mydp: String = ""
    private var uimage: String = ""
    private var postId: String = ""
    private var plike: String = ""
    private var hisdp: String = ""
    private var hisname: String = ""

    private lateinit var picture: ImageView
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var time: TextView
    private lateinit var more: ImageButton
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var like: TextView
    private lateinit var tcomment: TextView
    private lateinit var likebtn: ImageView
    private lateinit var comment: EditText
    private lateinit var sendb: ImageButton
    private lateinit var imagep: ImageView
    private lateinit var profile: LinearLayout
    private lateinit var recyclerView: RecyclerView

    private var mlike: Boolean = false
    private lateinit var actionBar: androidx.appcompat.app.ActionBar
    private var progressBar: ProgressBar? = null

    private lateinit var commentList: MutableList<ModelComment>
    private lateinit var adapterComment: AdapterComment

    private lateinit var bindingPostDetails: FragmentPostDetailsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingPostDetails = FragmentPostDetailsBinding.inflate(inflater, container, false).apply {
            recyclerView = recycleComment
            picture = detailsPictureCo
            name = detailsUtimeCo
            time = detailsUtimeCo
            more = detailsMorebtnCo
            title = detailsPtitleCo
            description = detailsDescriptCo
            tcomment = detailsPcommentco
            like = detailsPlikeb
            likebtn = detailsLikeIv
            comment = typecommet
            sendb = sendcomment
            imagep = commentimge
            profile = profilelayoutCo
        }

        // Set up ActionBar if needed
        val actionBar = (activity as? AppCompatActivity)?.supportActionBar
        actionBar?.apply {
            title = "Post Details"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        // Extract postId from arguments
        postId = arguments?.getString("pid") ?: ""

        return bindingPostDetails.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPostInfo()
//        loadUserInfo()
//        setLikes()
        progressBar = bindingPostDetails.detailsPB
        actionBar.subtitle = "SignedInAs:$myemail"
        loadComments()

        sendb.setOnClickListener {
            postComment()
        }
//        likebtn.setOnClickListener {
//            likepost()
//        }
//        like.setOnClickListener {
//            val intent = Intent(this@PostDetailsFragment, PostLikedByActivity::class.java)
//            intent.putExtra("pid", postId)
//            startActivity(intent)
//        }
    }

    // Funkcja do wczytywania komentarzy
    private fun loadComments() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        commentList = ArrayList()
        val reference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                commentList.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelComment: ModelComment? = dataSnapshot1.getValue(ModelComment::class.java)
                    modelComment?.let { commentList.add(it) }
                }
                // Po zakończeniu pętli for, aktualizuj adapter RecyclerView raz
                adapterComment = AdapterComment(requireContext(), commentList, myuid, postId)
                recyclerView.adapter = adapterComment
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędów związanych z anulowaniem operacji odczytu z bazy danych
            }
        })
    }

    private fun loadUserInfo() {
        val userId = firebaseAuth.currentUser?.uid
        if (userId != null) {
            val databaseReference =
                FirebaseDatabase.getInstance().getReference("userInfo").child(userId)
        }
    }

    private fun setLikes() {
        // Integration of setLikes from Java code goes here
    }

    private fun likepost() {
        // Integration of likepost from Java code goes here
    }

    private fun postComment() {
        progressBar?.visibility = View.VISIBLE

        val commentss = comment.text.toString().trim()
        if (commentss.isEmpty()) {
            Toast.makeText(requireContext(), "Empty comment", Toast.LENGTH_LONG).show()
            return
        }
        val timestamp = System.currentTimeMillis().toString()
        val datarf = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments")
        val hashMap: MutableMap<String, Any> = HashMap()
        hashMap["cId"] = timestamp
        hashMap["comment"] = commentss
        hashMap["ptime"] = timestamp
        hashMap["uid"] = myuid
        hashMap["uemail"] = myemail
        hashMap["udp"] = mydp
        hashMap["uname"] = myname
        datarf.child(timestamp).setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Added", Toast.LENGTH_LONG).show()
                comment.setText("")
                updateCommentCount()
            }
            .addOnFailureListener { e ->

                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
        progressBar?.visibility = View.GONE
    }

    private var count = false
    private fun updateCommentCount() {
        count = true
        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (count) {
                    val comments = dataSnapshot.child("pcomments").getValue(String::class.java)
                    val newcomment = comments?.toInt() ?: 0 + 1
                    reference.child("pcomments").setValue(newcomment.toString())
                    count = false
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędów związanych z anulowaniem operacji odczytu z bazy danych
            }
        })
    }




    private fun loadPostInfo() {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
            val query: Query = databaseReference.orderByChild("ptime").equalTo(postId)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        val ptitle = dataSnapshot1.child("title").getValue(String::class.java)
                        val descriptions = dataSnapshot1.child("description").getValue(String::class.java)
                        uimage = dataSnapshot1.child("uimage").getValue(String::class.java) ?: ""
                        hisdp = dataSnapshot1.child("udp").getValue(String::class.java) ?: ""
                        val uemail = dataSnapshot1.child("uemail").getValue(String::class.java) ?: ""
                        hisname = dataSnapshot1.child("uname").getValue(String::class.java) ?: ""
                        ptime = dataSnapshot1.child("ptime").getValue(String::class.java) ?: ""
                        plike = dataSnapshot1.child("plike").getValue(String::class.java) ?: ""
                        val commentcount = dataSnapshot1.child("pcomments").getValue(String::class.java) ?: ""

                        val calendar = Calendar.getInstance(Locale.ENGLISH)
                        calendar.timeInMillis = ptime.toLong()
                        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()

                        name.text = hisname
                        title.text = ptitle
                        description.text = descriptions
                        like.text = "$plike Likes"
                        time.text = timedate
                        tcomment.text = "$commentcount Comments"

                        image.visibility = if (uimage == "noImage") View.GONE else View.VISIBLE
                        if (uimage != "noImage") {
                            try {
                                Glide.with(requireContext()).load(uimage).into(image)
                            } catch (e: Exception) {
                                // Obsługa błędu ładowania obrazu
                            }
                        }
                        try {
                            Glide.with(requireContext()).load(hisdp).into(picture)
                        } catch (e: Exception) {
                            // Obsługa błędu ładowania obrazu
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Obsługa błędów związanych z anulowaniem operacji odczytu z bazy danych
                }
            })
        }


    }