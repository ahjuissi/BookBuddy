package com.example.bookbuddy.profileViewUser

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class EditProfileFragment : Fragment() {
    private lateinit var bindingEditProfile: FragmentEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private var db= Firebase.firestore



    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingEditProfile = FragmentEditProfileBinding.bind(view)
        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Pobierz UID aktualnie zalogowanego użytkownika
        val userId = firebaseAuth.currentUser?.uid
        userId?.let {}  //TODO
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingEditProfile = FragmentEditProfileBinding.inflate(inflater, container, false)

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
//            ImagePicDialog()
        }


        return bindingEditProfile.root
    }
    private fun nameChange() {
        val builder = AlertDialog.Builder(requireContext())
        var key = "name"
        builder.setTitle("Update $key")

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

                // Tutaj aktualizujemy nową nazwę w bazie
//                databaseReference.child(firebaseUser!!.uid).updateChildren(result)
//                    .addOnSuccessListener {
//                        // Po aktualizacji wyświetlamy komunikat
//                        Toast.makeText(requireContext(), "updated", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(requireContext(), "Unable to update", Toast.LENGTH_LONG).show()
//                    }

                if (key == "name") {
//                    val databaseRef, override fun onDataChange(dataSnapshot: DataSnapshot) {
                }
            } else {
                Toast.makeText(requireContext(), "Unable to update", Toast.LENGTH_LONG).show()
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
            val value = editTextPass.text.toString().trim()
            val value2 = editTextPass2.text.toString().trim()
            if (value.isNotEmpty()) {
                println(value)
                println(value2)
//                TODO: updatePassword(value, value2)
            } else{
                println("meh")
            }
        }
        val dialog = builder.create()
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(Color.WHITE)
            positiveButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))
        }

        dialog.show()

    }
    private fun updatePassword(oldp: String, newp: String) {
//        z neta:
//        val user = firebaseAuth.currentUser
//        val authCredential = EmailAuthProvider.getCredential(user!!.email!!, oldp)
//        user.reauthenticate(authCredential)
//            .addOnSuccessListener {
//                user.updatePassword(newp)
//                    .addOnSuccessListener {
//                        Toast.makeText(requireContext(), "Changed Password", Toast.LENGTH_LONG).show()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
//                    }
//            }
//            .addOnFailureListener { e ->
//                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show()
//            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            //    addToBackStack(null)
            commit()
        }
}