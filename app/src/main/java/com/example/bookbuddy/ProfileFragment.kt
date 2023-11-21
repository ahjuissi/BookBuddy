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
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var bindingProfile: FragmentProfileBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

//    private var param1: String? = null
//    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
////        arguments?.let {
////            param1 = it.getString(ARG_PARAM1)
////            param2 = it.getString(ARG_PARAM2)
////        }
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

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment ProfileFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            ProfileFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}