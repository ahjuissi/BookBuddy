package com.example.bookbuddy.profileViewUser

import ImagePicDialog
import ImagePicDialog.Companion.REQUEST_CODE_GALLERY
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentEditProfileBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.shashank.sony.fancytoastlib.FancyToast
import java.util.Date


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class EditProfileFragment : Fragment() {
    private lateinit var bindingEditProfile: FragmentEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private var db= Firebase.firestore
    private lateinit var imageDialog: ImagePicDialog
    private var selectedPicture : Uri? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingEditProfile = FragmentEditProfileBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage=FirebaseStorage.getInstance()
        imageDialog = ImagePicDialog(requireActivity())

        // Pobierz UID aktualnie zalogowanego użytkownika
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {}  //TODO

        return bindingEditProfile.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingEditProfile.editProfileBackBtn.setOnClickListener{
            val userProfileFragment = UserProfileFragment()
            setCurrentFragment(userProfileFragment)
        }
        bindingEditProfile.editPassword.setOnClickListener{
            passwordChangeDialog()
        }
        bindingEditProfile.editName.setOnClickListener{
            nameChange()
        }
        bindingEditProfile.editProfilepic.setOnClickListener{
           val intent=Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(intent,1)

        }
//        bindingEditProfile.continueBtn.setOnClickListener{
//            if(selectedPicture==null)
//            {
//                Toast.makeText(this@EditProfileFragment,"Please select your image",Toast.LENGTH_SHORT).show()
//            }else updateImage()
//
//        }
    }
    private fun updateImage(){
val reference=storage.reference.child("Profile").child(Date().time.toString())
        reference.putFile(selectedPicture!!).addOnCompleteListener{
            if(it.isSuccessful)
            {
                reference.downloadUrl.addOnSuccessListener { taks->
                    uploadInfo(taks.toString())
                }
            }
        }

    }
    private fun uploadInfo(imgUrl:String)
    {val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val databaseReference = FirebaseDatabase.getInstance().reference
            val userInfoRef = databaseReference.child("userInfo").child(uid)

            // Save imgUrl under the current user's ID in the userInfo node
            userInfoRef.child("imgUrl").setValue(imgUrl)
                .addOnSuccessListener {
                    // Handle success if necessary
                    // For example, Log a success message
                    println("Image URL saved successfully")
                }
                .addOnFailureListener { e ->
                    // Handle any errors that may occur during the operation
                    // For example, Log an error message
                    Log.e(TAG, "Error saving image URL: ${e.message}")
                }
        }

    }

    fun startActivityForResult(intent: Intent, requestCode: Int,data: Intent?) {
        super.startActivityForResult(intent, requestCode)
        if(data!=null)
        {
            if(data.data!=null)
            {
                selectedPicture=data.data!!

            }
        }

    }
    private fun nameChange() {
        val builder = AlertDialog.Builder(requireContext())
        val keyName = "name"
        val keySurname = "surname"
        builder.setTitle("Update $keyName and $keySurname")

        // Tworzenie układu do wprowadzenia nowej nazwy
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)

        val textInputLayoutName = TextInputLayout(requireContext())
        val editTextName = EditText(requireContext())
        textInputLayoutName.hint = "Enter name"
        textInputLayoutName.addView(editTextName)
        layout.addView(textInputLayoutName)

        // Dodane
        val textInputLayoutSurname = TextInputLayout(requireContext())
        val editTextSurname = EditText(requireContext())
        textInputLayoutSurname.hint = "Enter surname"
        textInputLayoutSurname.addView(editTextSurname)
        layout.addView(textInputLayoutSurname)

        builder.setView(layout)

        builder.setPositiveButton("Update") { dialog, which ->
            val valueName = editTextName.text.toString().trim()
            val valueSurname = editTextSurname.text.toString().trim()

            if (valueName.isNotEmpty() && valueSurname.isNotEmpty()) {
                val databaseReference = FirebaseDatabase.getInstance().reference
                val firebaseUser = FirebaseAuth.getInstance().currentUser

                firebaseUser?.let { user ->
                    val userId = user.uid
                    val userInfoRef = databaseReference.child("userInfo").child(userId)

                    val updates = hashMapOf<String, Any>(
                        keyName to valueName,
                        keySurname to valueSurname
                    )

                    userInfoRef.updateChildren(updates)
                        .addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Name and Surname updated",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                requireContext(),
                                "Unable to update",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            } else {
                Toast.makeText(requireContext(), "Please enter both name and surname", Toast.LENGTH_LONG).show()
            }
        }

        builder.create().show()
    }
    private fun passwordChangeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Change password.")

        // Tworzenie układu do wprowadzenia nowej nazwy
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(10, 10, 10, 10)

        val textInputLayoutPass = TextInputLayout(requireContext())
        val textInputLayoutPass2 = TextInputLayout(requireContext())

        val editTextPass = TextInputEditText(textInputLayoutPass.context)
        val editTextPass2 = TextInputEditText(textInputLayoutPass2.context)

        textInputLayoutPass.hint = "Enter password"
        textInputLayoutPass2.hint = "Repeat password"

        editTextPass.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        editTextPass2.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        editTextPass.transformationMethod = PasswordTransformationMethod.getInstance()
        editTextPass2.transformationMethod = PasswordTransformationMethod.getInstance()

        textInputLayoutPass.addView(editTextPass)
        textInputLayoutPass2.addView(editTextPass2)

        textInputLayoutPass.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
        textInputLayoutPass2.endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE

        layout.addView(textInputLayoutPass)
        layout.addView(textInputLayoutPass2)

        builder.setView(layout)

        builder.setPositiveButton("Update") { dialog, which ->
            val newPassword = editTextPass.text.toString().trim()
            val repeatPassword = editTextPass2.text.toString().trim()

            if (newPassword.isNotEmpty() && newPassword == repeatPassword) {
                // Firebase Auth instance
                val auth = FirebaseAuth.getInstance()
                val user = auth.currentUser

                user?.updatePassword(newPassword)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Password updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Passwords do not match or empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.WHITE)
            positiveButton.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_500
                )
            )
        }

        dialog.show()
    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            //    addToBackStack(null)
            commit()
        }
}
