package com.example.bookbuddy.profileViewUser

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookbuddy.databinding.FragmentProfileBinding
import android.annotation.SuppressLint
import com.example.bookbuddy.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class UserProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
    ): View? {
        // Inflate the layout for this fragment
//        bindingProfile = FragmentProfileBinding.inflate(inflater, container, false)
//        return bindingProfile.root
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun fetchUserInfo(userId: String) {
        db.collection("userInfo")
            .document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val name = documentSnapshot.getString("name")
                    val surname = documentSnapshot.getString("surname")
                    val email = documentSnapshot.getString("mail")

                    // Ustaw pobrane dane w odpowiednich widokach
                    bindingProfile.textViewName.text = "Name: $name"
                    bindingProfile.textViewSurname.text = "Surname: $surname"
                    bindingProfile.textViewEmail.text = "Email: $email"
                }
            }
            .addOnFailureListener { exception ->
                // Obsługa błędów
            }
    }
}

