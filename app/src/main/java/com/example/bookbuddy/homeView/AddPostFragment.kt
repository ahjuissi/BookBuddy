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
    private lateinit var image: ImageView
    private lateinit var upload: Button
    private lateinit var imageUri: Uri
    private lateinit var name: String
    private lateinit var email: String
    private lateinit var uid: String
    private lateinit var dp: String
    private lateinit var databaseReference: DatabaseReference

    // Deklaracje uprawnień
    private val cameraPermission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    // Rejestracja wyniku wyboru obrazu
    private val imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                imageUri = data.data!!  // Wybór obrazu z galerii lub aparatu
                image.setImageURI(imageUri)     // Ustawienie wybranego obrazu w ImageView
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inicjalizacja elementów interfejsu użytkownika
        bindingAddPost = FragmentAddPostBinding.inflate(inflater, container, false)
        val view = bindingAddPost.root

        // Inicjalizacja Firebase Auth i pobranie danych użytkownika
        firebaseAuth = FirebaseAuth.getInstance()
        title = bindingAddPost.postTitle
        des = bindingAddPost.pdes
        image = bindingAddPost.imagePost
        upload = bindingAddPost.pupload

        val intent = activity?.intent

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query: Query = databaseReference.orderByChild("email").equalTo(email)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    name = dataSnapshot1.child("name").value.toString()
                    email = dataSnapshot1.child("email").value.toString()
                    dp = dataSnapshot1.child("image").value.toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
        // Obsługa kliknięcia na przycisk wyboru obrazu
        image.setOnClickListener {
            showImagePicDialog()    // Wyświetlenie dialogu wyboru obrazu
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
            }

            // Sprawdzenie, czy obraz został wybrany
            if (!::imageUri.isInitialized) {
                // Komunikat o braku wybranego obrazu
                Toast.makeText(requireContext(), "Select an Image", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            } else {
                uploadData(titl, description) // Przesłanie danych do Firebase
            }
        }
        return view
    }


    // Metoda wyświetlająca dialog wyboru źródła obrazu (aparat lub galeria)
    private fun showImagePicDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Pick Image From")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
                1 -> if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    // Metoda sprawdzająca uprawnienia do dostępu do pamięci urządzenia
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Metoda żądająca uprawnień do dostępu do pamięci urządzenia
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

// Metoda żądająca uprawnień do dostępu do pamięci urządzenia
    private fun checkCameraPermission(): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val resultStorage = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return resultCamera && resultStorage
    }

    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )!!
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        imagePicker.launch(cameraIntent)
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePicker.launch(galleryIntent)
    }

    // Metoda przesyłająca dane do Firebase Storage i zapisująca dane do Firebase Database
    private fun uploadData(titl: String, description: String) {
        val progressBar = ProgressDialog(requireContext())
        progressBar.setMessage("Publishing Post")
        progressBar.show()

        val timestamp = System.currentTimeMillis().toString()
        val filepathname = "Posts/" + "post" + timestamp
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val storageReference1: StorageReference =
            FirebaseStorage.getInstance().getReference().child(filepathname)
        storageReference1.putBytes(data)
            .addOnSuccessListener { taskSnapshot ->
                storageReference1.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUri = uri.toString()

                    val hashMap: HashMap<String, Any> = HashMap()
                    hashMap["uid"] = uid
                    hashMap["uname"] = name
                    hashMap["uemail"] = email
                    hashMap["udp"] = dp
                    hashMap["title"] = titl
                    hashMap["description"] = description
                    hashMap["uimage"] = downloadUri
                    hashMap["ptime"] = timestamp
                    hashMap["plike"] = 0
                    hashMap["pcomments"] = 0

                    val databaseReference: DatabaseReference =
                        FirebaseDatabase.getInstance().getReference("Posts")
                    databaseReference.child(timestamp).setValue(hashMap)
                        .addOnSuccessListener {
                            progressBar.dismiss()
                            Toast.makeText(requireContext(), "Published", Toast.LENGTH_LONG)
                                .show()
                            title.setText("")
                            des.setText("")
                            image.setImageURI(null)
                            if (::imageUri.isInitialized) {
                                imageUri = Uri.EMPTY
                            }
                            startActivity(Intent(requireContext(), NavActivity::class.java))
                            requireActivity().finish()
                        }.addOnFailureListener { e ->
                            progressBar.dismiss()
                            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                progressBar.dismiss()
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
            }
    }


    companion object {
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
    }
}
