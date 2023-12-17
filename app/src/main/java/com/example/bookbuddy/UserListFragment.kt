package com.example.bookbuddy

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.databinding.FragmentUserListBinding



class UserListFragment : Fragment() {
    private lateinit var bindingUserList: FragmentUserListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingUserList = FragmentUserListBinding.inflate(inflater, container, false)
        val adminFragment=AdminFragment()
        bindingUserList.backBtn.setOnClickListener {

            setCurrentFragment(adminFragment)

        }
        userList()
        // Inflate the layout for this fragment
        return bindingUserList.root
        //return inflater.inflate(R.layout.fragment_user_list, container, false)


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun userList(){

        recyclerView = bindingUserList.recyclerView

        val templist: List<UserViewModel> = emptyList()
        val data: MutableList<UserViewModel> = templist.toMutableList()
        adapter = UserAdapter(data)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        for (i in 0 until 15) {
            data.add(UserViewModel("Patryk Luczak","ziomkowski14gmail.com"))
            Log.w("FetchLog", "tr")
        }
        adapter.notifyDataSetChanged()


    }
    private fun setCurrentFragment(fragment: Fragment)=
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
          //  addToBackStack(null)
            commit()
        }
}