package com.example.bookbuddy.homeView

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bookbuddy.R
import com.example.bookbuddy.authenticationPart.NavActivity
import com.example.bookbuddy.databinding.FragmentAddPostBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class AddPostFragment : Fragment() {
    // Deklaracje zmiennych
    private lateinit var bindingAddPost: FragmentAddPostBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var title: EditText
    private lateinit var des: EditText
    private lateinit var upload: Button
    private var name: String=""
    private  var email: String=""
    private  var city: String=""
    private  var uid: String=""
    private lateinit var dp: String
    private lateinit var databaseReference: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicjalizacja elementów interfejsu użytkownika
        bindingAddPost = FragmentAddPostBinding.inflate(inflater, container, false)
        val view = bindingAddPost.root
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        uid = currentUser?.uid ?: ""
        // Inicjalizacja Firebase Auth i pobranie danych użytkownika
        title = bindingAddPost.postTitle
        des = bindingAddPost.pdes
        upload = bindingAddPost.pupload

        databaseReference = FirebaseDatabase.getInstance().getReference("userInfo")
        val query: Query = databaseReference.orderByChild("userId").equalTo(uid)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {

                    name = dataSnapshot1.child("name").getValue(String::class.java).toString()
                    email = dataSnapshot1.child("mail").getValue(String::class.java).toString()
                    city = dataSnapshot1.child("city").getValue(String::class.java).toString()


                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("AddPostFragment", "Error: ${databaseError.message}")
            }
        })



        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingAddPost.addPostBackBtn.setOnClickListener{
            val homeFragment = HomeFragment()
            setCurrentFragment(homeFragment)
        }
        // Obsługa przycisku przesłania postu
        upload.setOnClickListener {
            // Pobranie danych wprowadzonych przez użytkownika
            val titl = title.text.toString().trim()
            val description = des.text.toString().trim()

            if (TextUtils.isEmpty(titl)) {
                title.error = "Title Cant be empty"
                Toast.makeText(requireContext(), "Title can't be left empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(description)) {
                des.error = "Description Cant be empty"
                Toast.makeText(requireContext(), "Description can't be left empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }else{
                uploadData(titl, description,uid,name, email,city)
            }

        }

    }



    // Metoda przesyłająca dane do Firebase Storage i zapisująca dane do Firebase Database
    private fun uploadData(titl: String, description: String,uid:String,name:String,email:String, city:String) {
        val timestamp = System.currentTimeMillis().toString()

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = uid
        hashMap["uname"] = name
        hashMap["uemail"] = email
        hashMap["city"] = city
        hashMap["title"] = titl
        hashMap["description"] = description
        hashMap["ptime"] = timestamp

        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.child(timestamp).setValue(hashMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Published", Toast.LENGTH_LONG).show()
                title.setText("")
                des.setText("")
                //startActivity(Intent(requireContext(), NavActivity::class.java))
                //requireActivity().finish()
                //to psuło ^ , tera git
                val homeFragment = HomeFragment()
                setCurrentFragment(homeFragment)

            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            addToBackStack(null)
            commit()
        }


}
