package com.example.bookbuddy.homeView

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Locale


class HomeFragment : Fragment() {

    private var firebaseAuth = FirebaseAuth.getInstance()
    var myuid: String? = firebaseAuth.currentUser?.uid
    var myuCity: String? = null
    var posts: MutableList<ModelPost?>? = null

    private lateinit var bindingHome: FragmentHomeBinding
    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var adapterPosts: AdapterPosts
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var cityTV: TextView? = null
    private var dateTV: TextView? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingHome = FragmentHomeBinding.inflate(inflater, container, false)
        recyclerViewPosts = bindingHome.recyclerviewPosts
        recyclerViewPosts.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerViewPosts.layoutManager = layoutManager

        adapterPosts = AdapterPosts(requireActivity(), posts)
        recyclerViewPosts.adapter = adapterPosts

        swipeRefreshLayout = bindingHome.containerRvHome

        posts = ArrayList()

        cityTV = bindingHome.cityTV
        dateTV = bindingHome.dateTV

        loadPosts()

        return bindingHome.root
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindingHome.homeAddPostBtn.setOnClickListener{
            val addPostFragment = AddPostFragment()
            setCurrentFragment(addPostFragment)
        }
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            recyclerViewPosts.adapter = adapterPosts
            adapterPosts.notifyDataSetChanged()

        }

    }
    private fun loadDate() {
        cityTV?.text = myuCity.toString()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Meeting").child(myuCity!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val meetingData = dataSnapshot.getValue(ModelMeeting::class.java)
                    val meetingDate = meetingData?.date
                    val meetingTime = meetingData?.time
                    if (isMeetingDateInPast(meetingDate)) {
                        dateTV?.text = "No meeting scheduled"
                    } else {
                        dateTV?.text = "$meetingDate $meetingTime"
                    }
                } else {
                    dateTV?.text = "No meeting scheduled"
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                activity?.let {
                    Toast.makeText(it, databaseError.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun isMeetingDateInPast(meetingDate: String?): Boolean {
        meetingDate?.let {
            val currentDate = DateFormat.format("dd/MM/yyyy", Calendar.getInstance().time).toString()
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            val meeting = sdf.parse(meetingDate)
            val current = sdf.parse(currentDate)

            return current.after(meeting)
        }
        return false
    }
    private fun loadPosts() {
        val cityRef = FirebaseDatabase.getInstance().getReference("userInfo").child(myuid!!)
        cityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                myuCity = dataSnapshot.child("city").getValue(String::class.java)
                loadDate()
            }
            override fun onCancelled(databaseError: DatabaseError) {

                activity?.let {
                    Toast.makeText(it, databaseError.message, Toast.LENGTH_LONG).show()
                }
            }
        })

        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts?.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost = dataSnapshot1.getValue(ModelPost::class.java)
                    val postCity = modelPost?.city

                    if (postCity == myuCity) {
                        posts?.add(modelPost)
                    }
                }
                adapterPosts.updateData(posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                activity?.let {
                    Toast.makeText(it, databaseError.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }

    data class ModelMeeting(
        val date: String = "",
        val time: String = "",
        val city: String = ""
    )

}
