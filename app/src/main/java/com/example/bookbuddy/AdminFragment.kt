package com.example.bookbuddy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.databinding.FragmentAdminBinding
import com.google.firebase.firestore.auth.User


// TODO: Rename parameter arguments, choose names that match

class AdminFragment : Fragment(R.layout.fragment_admin) {
    private lateinit var bindingAdmin: FragmentAdminBinding


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


        bindingAdmin.idImgBtnAddUser.setOnClickListener {
            println("4")
            val userListFragment=UserListFragment()
            setCurrentFragment(userListFragment)
          //  showAddView()
        }
        return bindingAdmin.root
       // return inflater.inflate(R.layout.fragment_admin, container, false)
    }


    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
        //    addToBackStack(null)
            commit()
        }



}
