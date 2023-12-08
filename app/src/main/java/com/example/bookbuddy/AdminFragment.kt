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
//    private lateinit var bindingAdminAdd: FragmentAdminAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindingAdmin = FragmentAdminBinding.inflate(layoutInflater)
//        bindingAdminAdd = FragmentAdminAddBinding.bind(view)

        bindingAdmin.idImgBtnAddUser.setOnClickListener {
            // Kliknięto przycisk w fragmencie FragmentAdmin
            println("4")
//            replaceFragment(R.layout.fragment_admin_add)
        }
//        bindingAdminAdd.backBtn.setOnClickListener {
////            Kliknięto przycisk w fragmencie FragmentAdminAdd
//            replaceFragment(R.layout.fragment_admin)
//         }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        }



}
