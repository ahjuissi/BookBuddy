package com.example.bookbuddy.homeView

import android.content.ContentValues
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.bookbuddy.databinding.FragmentPostDetailsBinding
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
import com.example.bookbuddy.profileViewAdmin.UserViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.firestore
import java.util.Calendar
import java.util.Locale

import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PostDetailsFragment : Fragment() {
    private var hisuid: String = ""
    private var ptime: String = ""
    private var myuid: String = ""
    private var myname: String = ""
    private var myemail: String = ""
    private var mysurname: String = ""
    private var mydp: String = ""
    private var uimage: String = ""
    private var postId: String = ""
    private var plike: String = ""
    private var hisdp: String = ""
    private var hisname: String = ""

    private var pPicture: ImageView? = null
    private var pName: TextView? = null
    private var pTime: TextView? = null
    private var more: ImageButton? = null
    private var pTitle: TextView? = null
    private var pDescription: TextView? = null
    private var pCommentCount: TextView? = null
    private var pLikeCount: TextView? = null
    private var typeComment: EditText? = null
    private var likebtn: ImageView? = null

    private var sendb: ImageButton? = null
    private var comPic: ImageView? = null
    private var profile: LinearLayout? = null
    private var recyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var mlike: Boolean = false

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
    ): View {

        bindingPostDetails = FragmentPostDetailsBinding.inflate(inflater, container, false)
        recyclerView = bindingPostDetails.recycleComment

        pPicture = bindingPostDetails.detailsPictureCo
        pName = bindingPostDetails.detailsUnameCo
        pTime = bindingPostDetails.detailsUtimeCo
        more = bindingPostDetails.detailsMorebtnCo
        pTitle = bindingPostDetails.detailsPtitleCo
        pDescription = bindingPostDetails.detailsDescriptCo
        pCommentCount = bindingPostDetails.detailsPcommentCount
        pLikeCount = bindingPostDetails.detailsPlikeCount
        likebtn = bindingPostDetails.detailsLikeIv

        comPic = bindingPostDetails.commentPic
        typeComment = bindingPostDetails.typeCommet
        sendb = bindingPostDetails.sendcomment

        profile = bindingPostDetails.profilelayoutCo
        progressBar = bindingPostDetails.detailsPB

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        // Extract postId from arguments
        postId = arguments?.getString("pid") ?: ""

        return bindingPostDetails.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentUserInfo()
        progressBar = bindingPostDetails.detailsPB
        loadPostInfo(postId)


        sendb?.setOnClickListener {
            postComment()
        }

//        setLikes()
//        loadComments()

//        likebtn.setOnClickListener {
//            likepost()
//        }
//        pLikeCount.setOnClickListener {
//            val intent = Intent(this@PostDetailsFragment, PostLikedByActivity::class.java)
//            intent.putExtra("pid", postId)
//            startActivity(intent)
//        }

    }
    private fun postComment() {
        progressBar?.visibility = View.VISIBLE

        val commentss = typeComment?.text.toString().trim()
        if (commentss.isEmpty()) {

            Toast.makeText(requireContext(), "Empty comment", Toast.LENGTH_LONG).show()
            progressBar?.visibility = View.GONE
            return
        }
        val timestamp = System.currentTimeMillis().toString()
        val datarf = FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments")
        val hashMap: MutableMap<String, Any> = HashMap()
        hashMap["cId"] = timestamp
        hashMap["comment"] = commentss
        hashMap["uid"] = myuid
        hashMap["uemail"] = myemail
        hashMap["udp"] = mydp
        hashMap["uname"] = myname
        datarf.child(timestamp).setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Added", Toast.LENGTH_LONG).show()
                pCommentCount?.setText("")
               // updateCommentCount()
            }
            .addOnFailureListener { e ->

                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
        progressBar?.visibility = View.GONE
    }
    private fun currentUserInfo(){
        val userId = firebaseAuth.currentUser?.uid
        userId?.let { uid ->
            // Referencja do węzła userInfo dla danego użytkownika
            val userRef = databaseReference.child("userInfo").child(uid)

            // Odczytanie danych z bazy danych
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Odczytanie danych i przypisanie do zmiennych
                         myname = snapshot.child("name").getValue(String::class.java).toString()
                         myemail = snapshot.child("mail").getValue(String::class.java).toString()
                        mysurname= snapshot.child("surname").getValue(String::class.java).toString()
                    } else {
                        // Jeśli węzeł nie istnieje
                        Log.d("UserInfo", "User data not found")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserInfo", "Error reading user data: ${error.message}")
                }
            })
        }
    }

    private fun loadPostInfo(postId: String) {
        //TODO: pPicture profilowe
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        val query: Query = databaseReference.orderByChild("ptime").equalTo(postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val post =
                        dataSnapshot1.getValue(object : GenericTypeIndicator<Map<String, Any>>() {})
                    if (post != null) {
                        val ptitle = post["title"] as? String ?: ""
                        val descriptions = post["description"] as? String ?: ""
                        val uimage = post["upic"] as? String ?: ""
                        val uemail = post["uemail"] as? String ?: ""
                        val hisname = post["uname"] as? String ?: ""
                        val ptime = post["ptime"] as? String ?: ""

                        // Użyj wydobytych danych do aktualizacji interfejsu użytkownika
                        val calendar = Calendar.getInstance(Locale.ENGLISH)
                        calendar.timeInMillis = ptime.toLong()
                        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()

                        pName?.text = hisname
                        pTime?.text = timedate
                        pTitle?.text = ptitle
                        pDescription?.text = descriptions

                    }
                }
            }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Obsługa błędów związanych z anulowaniem operacji odczytu z bazy danych
                }

        })
    }

    // Funkcja do wczytywania komentarzy
    private fun loadComments() {
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView?.layoutManager = layoutManager
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
                recyclerView?.adapter = adapterComment
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


    private var count = false
//    private fun updateCommentCount() {
//        count = true
//        val reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
//        reference.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (count) {
//                    val comments = dataSnapshot.child("pcomments").getValue(String::class.java)
//                    val newcomment = comments?.toInt() ?: 0 + 1
//                    reference.child("pcomments").setValue(newcomment.toString())
//                    count = false
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Obsługa błędów związanych z anulowaniem operacji odczytu z bazy danych
//            }
//
//        })
//    }

}