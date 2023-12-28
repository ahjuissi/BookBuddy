package com.example.bookbuddy.homeView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentHomeBinding
import com.example.bookbuddy.profileViewAdmin.UserListFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    var myuid: String? = null
    var posts: MutableList<ModelPost?>? = null

    private var firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var bindingHome: FragmentHomeBinding
    private lateinit var recyclerViewPosts: RecyclerView
    private lateinit var adapterPosts: AdapterPosts
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingHome = FragmentHomeBinding.inflate(inflater, container, false)
        recyclerViewPosts = bindingHome.recyclerviewPosts

        recyclerViewPosts.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recyclerViewPosts.setLayoutManager(layoutManager)

        posts = ArrayList()
        bindingHome.homeAddPostBtn.setOnClickListener{
            val addPostFragment = AddPostFragment()
            setCurrentFragment(addPostFragment)
        }
        loadPosts()
        return bindingHome.root
    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            //    addToBackStack(null)
            commit()
        }

    private fun loadPosts() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts!!.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost = dataSnapshot1.getValue(ModelPost::class.java)
                    posts!!.add(modelPost)
                    adapterPosts = AdapterPosts(activity!!, posts)
                    recyclerViewPosts!!.adapter = adapterPosts

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }

}