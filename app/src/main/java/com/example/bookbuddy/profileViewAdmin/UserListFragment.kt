package com.example.bookbuddy.profileViewAdmin

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentUserListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserListFragment : Fragment() {
    private lateinit var bindingUserList: FragmentUserListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    private var db = FirebaseFirestore.getInstance()
    private var firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindingUserList = FragmentUserListBinding.inflate(inflater, container, false)
        val adminFragment= AdminFragment()
            //    val db = FirebaseFirestore.getInstance()
      //  val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            db.collection("userInfo")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userCity = documentSnapshot.getString("city")
                    bindingUserList.userList.text = "User List of ${userCity}"
                }
        }

        bindingUserList.backBtn.setOnClickListener {

            setCurrentFragment(adminFragment)

        }

        userList()
        // Inflate the layout for this fragment
        return bindingUserList.root
        //return inflater.inflate(R.layout.fragment_user_list, container, false)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun userList() {
        recyclerView = bindingUserList.recyclerView

        val db = FirebaseFirestore.getInstance()
        val templist: List<UserViewModel> = emptyList()
        val data: MutableList<UserViewModel> = templist.toMutableList()
        adapter = UserAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid

        if (userId != null) {
            db.collection("userInfo")
                .document(userId)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val userCity = documentSnapshot.getString("city")

                    // Użyj pobranej wartości "city" w zapytaniu whereEqualTo
                    db.collection("userInfo")
                        .whereEqualTo("city", userCity)
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                // Dla każdego użytkownika w pobranym "city"
                                val id = document.getString("userId")
                                val name = document.getString("name")
                                val surname = document.getString("surname")
                                val mail = document.getString("mail")
                                data.add(UserViewModel(id,name, surname, mail))
                            }
                            adapter.notifyDataSetChanged()
                        }
                        .addOnFailureListener { exception ->
                            Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting document: ", exception)
                }
        }
    }



    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
          //  addToBackStack(null)
            commit()
        }




    }
