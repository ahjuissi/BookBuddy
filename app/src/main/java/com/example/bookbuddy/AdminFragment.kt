package com.example.bookbuddy

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookbuddy.databinding.FragmentAdminAddBinding
import com.example.bookbuddy.databinding.FragmentAdminBinding

// TODO: Rename parameter arguments, choose names that match

class AdminFragment : Fragment(R.layout.fragment_admin) {
    private lateinit var bindingAdmin: FragmentAdminBinding
    private lateinit var bindingAdminAdd: FragmentAdminAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingAdmin = FragmentAdminBinding.inflate(layoutInflater)
        return bindingAdmin.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingAdmin = FragmentAdminBinding.inflate(layoutInflater)
        bindingAdmin.idImgBtnAddUser.setOnClickListener {
            // Kliknięto przycisk w fragmencie FragmentAdmin
            println("4")
            showAddView()
        }
    }
    private fun showAddView() {
        bindingAdmin = FragmentAdminBinding.inflate(layoutInflater)
        bindingAdminAdd = FragmentAdminAddBinding.inflate(layoutInflater)
        bindingAdmin.root.removeAllViews() // Usuń istniejące widoki z widoku "admin"
        bindingAdmin.root.addView(bindingAdminAdd.root) // Dodaj widok "add"

        // Dodaj obsługę kliknięcia przycisku "Wróć do admin" w widoku "add"
        bindingAdminAdd.backBtn.setOnClickListener {
            // Zmiana widoku na "profil admin" bez zmiany fragmentu
            bindingAdmin.root.removeAllViews() // Usuń istniejące widoki z widoku "add"
            bindingAdmin.root.addView(bindingAdmin.root) // Dodaj widok "admin" z powrotem
        }
    }



}
