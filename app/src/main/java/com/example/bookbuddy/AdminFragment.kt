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

    private var adminView: View? = null
    private var adminAddView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("onCreate")
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("onViewCreated")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingAdmin = FragmentAdminBinding.inflate(inflater, container, false)
        bindingAdminAdd = FragmentAdminAddBinding.inflate(inflater, container, false)

        adminView = bindingAdmin.root
        adminAddView = bindingAdminAdd.root

        bindingAdmin.idImgBtnAddUser.setOnClickListener {
            println("4")
            showAddView()
        }
        bindingAdminAdd.backBtn.setOnClickListener {
            showAdminView()
        }
        return adminView
    }

    private fun showAddView() {
        val parent = adminView?.parent as? ViewGroup
        parent?.removeView(adminView)
        parent?.addView(adminAddView)
    }

    private fun showAdminView() {
        val parent = adminAddView?.parent as? ViewGroup
        parent?.removeView(adminAddView)
        parent?.addView(adminView)
    }


}
