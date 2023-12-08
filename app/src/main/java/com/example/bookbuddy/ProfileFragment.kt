package com.example.bookbuddy

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.bookbuddy.databinding.FragmentProfileBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookbuddy.databinding.ActivityMainBinding
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.CalendarView
import android.widget.TextView
import android.widget.CalendarView.OnDateChangeListener
import android.icu.util.Calendar
import android.os.Build
import android.text.Editable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingProfile = FragmentProfileBinding.bind(view)
        recyclerView = bindingProfile.recyclerView

        val templist: List<UserViewModel> = emptyList()
        val data: MutableList<UserViewModel> = templist.toMutableList()
        adapter = UserAdapter(data)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        for (i in 0 until 5) {
            data.add(UserViewModel("a", i))
            Log.w("FetchLog", "tr")
        }
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        bindingProfile = FragmentProfileBinding.inflate(inflater, container, false)
//        return bindingProfile.root
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

}