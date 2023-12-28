package com.example.bookbuddy.profileViewAdmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookbuddy.R
import com.example.bookbuddy.profileViewUser.UserProfileFragment
import com.example.bookbuddy.databinding.FragmentAdminBinding


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
