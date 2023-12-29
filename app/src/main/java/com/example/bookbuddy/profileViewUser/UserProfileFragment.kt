package com.example.bookbuddy.profileViewUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookbuddy.databinding.FragmentProfileBinding
import android.annotation.SuppressLint
import com.example.bookbuddy.R
import com.example.bookbuddy.homeView.AddPostFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class UserProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingProfile = FragmentProfileBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Pobierz UID aktualnie zalogowanego użytkownika
        val userId = firebaseAuth.currentUser?.uid

        userId?.let { fetchUserInfo(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingProfile = FragmentProfileBinding.inflate(inflater, container, false)
        bindingProfile.editProfileFab.setOnClickListener{
            //TODO: editUserProfile()
             val editProfileFragment = EditProfileFragment()
             setCurrentFragment(editProfileFragment)
        }
        return bindingProfile.root
        }

    private fun fetchUserInfo(userId: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("userInfo").child(userId)

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("SetTextI18n")
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    println("test")
                    val name = dataSnapshot.child("name").getValue(String::class.java)
                    val surname = dataSnapshot.child("surname").getValue(String::class.java)
                    val email = dataSnapshot.child("mail").getValue(String::class.java)

                    // val image = dataSnapshot.child("image").getValue(String::class.java)
                    // try {
                    // Glide.with(requireActivity()).load(image).into(avatartv)
                    // } catch (e: Exception) {}
                    // Ustaw pobrane dane w odpowiednich widokach
                    bindingProfile.textViewName.text = "Name: $name"
                    bindingProfile.textViewSurname.text = "Surname: $surname"
                    bindingProfile.textViewEmail.text = "Email: $email"

                //TODO: bindingProfile.avatarIv =
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędów
            }
        })
    }

    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            //    addToBackStack(null)
            commit()
        }

}

