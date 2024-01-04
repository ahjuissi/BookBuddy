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
import com.example.bookbuddy.databinding.FragmentVotingBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.NonDisposableHandle.parent

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

    private var picture: ImageView? = null
    private var image: ImageView? = null
    private var name: TextView? = null
    private var time: TextView? = null
    private var more: ImageButton? = null
    private var title: TextView? = null
    private var description: TextView? = null
    private var like: TextView? = null
    private var tcomment: TextView? = null
    private var likebtn: ImageView? = null
    private var comment: EditText? = null
    private var sendb: ImageButton? = null
    private var imagep: ImageView? = null
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
        picture = bindingPostDetails.detailsPictureCo
        name = bindingPostDetails.detailsUnameCo
        time = bindingPostDetails.detailsUtimeCo
        more = bindingPostDetails.detailsMorebtnCo
        title = bindingPostDetails.detailsPtitleCo
        description = bindingPostDetails.detailsDescriptCo
        tcomment = bindingPostDetails.detailsPcommentco
        like = bindingPostDetails.detailsPlikeb
        likebtn = bindingPostDetails.detailsLikeIv
        comment = bindingPostDetails.typecommet
        sendb = bindingPostDetails.sendcomment
        imagep = bindingPostDetails.commentimge
        profile = bindingPostDetails.profilelayoutCo
        progressBar = bindingPostDetails.detailsPB

        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        // Extract postId from arguments
        postId = arguments?.getString("pid") ?: ""

        if (title != null) {
            val textView = title
            textView?.text = "New Text"
        }

        return bindingPostDetails.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadPostInfo(postId)
//        loadUserInfo()
//        setLikes()
        progressBar = bindingPostDetails.detailsPB
        loadComments()
        sendb?.setOnClickListener {
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
    private fun loadPostInfo(postId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        val query: Query = databaseReference.orderByChild("ptime").equalTo(postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost = dataSnapshot1.getValue(ModelPost::class.java)
                    modelPost?.let {
                        // Pobierz informacje o poście z modelu
                        val ptitle = it.title
                        val descriptions = it.description
                        val uimage = it.upic ?: ""
                        val hisdp = it.upic ?: ""
                        val uemail = it.uemail ?: ""
                        val hisname = it.uname ?: ""
                        val ptime = it.ptime ?: ""

                        // Ustaw informacje o poście w interfejsie użytkownika
                        title?.text = ptitle
                        description?.text = descriptions
                        // Tutaj ustaw inne pola widoku na podstawie danych o poście
                        // ...
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

    private fun postComment() {
        progressBar?.visibility = View.VISIBLE

        val commentss = comment?.text.toString().trim()
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
                comment?.setText("")
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





}