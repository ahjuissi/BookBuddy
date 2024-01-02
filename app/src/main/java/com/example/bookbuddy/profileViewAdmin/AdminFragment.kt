package com.example.bookbuddy.profileViewAdmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookbuddy.R
import com.example.bookbuddy.profileViewUser.UserProfileFragment
import com.example.bookbuddy.databinding.FragmentAdminBinding
import com.example.bookbuddy.searchView.SearchFragment
import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.Toast


class AdminFragment : Fragment(R.layout.fragment_admin) {
    private lateinit var bindingAdmin: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        bindingAdmin = FragmentAdminBinding.inflate(inflater, container, false)


        bindingAdmin.idImgBtnAddUser.setOnClickListener {
            val userListFragment= UserListFragment()
            setCurrentFragment(userListFragment)
          //  showAddView()
        }
        bindingAdmin.idImgBtnProfile.setOnClickListener {
            val userProfileFragment= UserProfileFragment()
            setCurrentFragment(userProfileFragment)
            //  showAddView()
        }
        bindingAdmin.idImgBtnAddVote.setOnClickListener {
            // Tworzymy alert dialog
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Wprowadź liczbę książek")

            // Dodajemy pole do wpisania liczby książek
            val input = EditText(requireContext())
            input.inputType = InputType.TYPE_CLASS_NUMBER
            builder.setView(input)

            // Dodajemy przyciski "OK" i "Anuluj"
            builder.setPositiveButton("OK") { _, _ ->
                // Pobieramy wpisaną wartość
                val numberOfBooks = input.text.toString().toIntOrNull()
                if (numberOfBooks != null && numberOfBooks > 0) {
                    // Jeśli wpisana wartość jest poprawna, wykonujemy odpowiednie akcje
                    // Na przykład, możemy wyświetlić wprowadzoną liczbę książek
                    Toast.makeText(requireContext(), "Wybrano $numberOfBooks książek", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle()
                    bundle.putInt("numberOfBooks", numberOfBooks)
                    val votingFragment = SearchFragment()
                    votingFragment.arguments = bundle

                    // Ustawiamy fragment w aktywności
                    setCurrentFragment(votingFragment)
                    // Tutaj możesz dodać kod obsługujący wybraną liczbę książek
                } else {
                    // Jeśli wpisana wartość jest niepoprawna, możemy poinformować użytkownika
                    Toast.makeText(requireContext(), "Wprowadź poprawną liczbę książek", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Anuluj") { dialog, _ -> dialog.cancel() }

            // Wyświetlamy alert dialog
            builder.show()
        }
        return bindingAdmin.root
       // return inflater.inflate(R.layout.fragment_admin, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")

    }



    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
        //    addToBackStack(null)
            commit()
        }



}
