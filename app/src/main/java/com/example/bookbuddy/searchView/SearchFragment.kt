package com.example.bookbuddy.searchView

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentSearchBinding
import com.example.bookbuddy.voteView.VoteFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class SearchFragment : Fragment(R.layout.fragment_search) {
    lateinit var sendButton: FloatingActionButton
    private lateinit var bindingSearch: FragmentSearchBinding
    lateinit var mRequestQueue: RequestQueue
    lateinit var booksList: ArrayList<BookDetailsRVModel>
    lateinit var loadingPB: ProgressBar
    lateinit var searchEdt: EditText
    lateinit var searchBtn: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingSearch = FragmentSearchBinding.inflate(inflater, container, false)
        sendButton = bindingSearch.sendVotingfab
        sendButton.visibility = View.GONE
        val firebaseAuth = FirebaseAuth.getInstance()
        val userId = firebaseAuth.currentUser?.uid
        userId?.let { uid ->
            val userInfoRef = FirebaseDatabase.getInstance().getReference("userInfo").child(uid)

            userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userRole = snapshot.child("role").getValue(String::class.java)

                    if (userRole == "Admin") {
                        sendButton.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        sendButton.setOnClickListener {
            val ListVoteFragment = VoteFragment()
            setCurrentFragment(ListVoteFragment)
        }

        loadingPB = bindingSearch.idLoadingPB
        searchEdt = bindingSearch.idEdtSearchBooks
        searchBtn = bindingSearch.idBtnSearch

        val voteId = arguments?.getString("id")
        val voteTitle = arguments?.getString("title")
        val combined= "$voteId $voteTitle"
        if (!voteId.isNullOrEmpty()||!voteTitle.isNullOrEmpty()) {
            getBooksData(combined)
        }
        return bindingSearch.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchBtn.setOnClickListener {
            loadingPB.visibility = View.VISIBLE
            val searchTerm = searchEdt.text.toString()
            if (searchTerm.isEmpty()) {
                searchEdt.error = "Please enter a search query"
                loadingPB.visibility = View.GONE
            } else {
                println("edit nie pusty leciiiii")
                getBooksData(searchTerm)

            }
        }
    }
    private fun getBooksData(searchTerm: String) {
        val client = OkHttpClient()
        val url = "https://openlibrary.org/search.json?q=$searchTerm&limit=50"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.w("TAG", "onFailure")
                requireActivity().runOnUiThread {
                    loadingPB.visibility = View.GONE

                }
            }
            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("TAG", "Non-successful response: ${response.code}")
                    requireActivity().runOnUiThread {
                        loadingPB.visibility = View.GONE
                    }
                    return
                }


                val responseData = response.body?.string()
                Log.w("TAG", "Processing JSON response data")
                if (responseData.isNullOrEmpty()) {
                    Log.e("TAG", "Empty response data")
                    requireActivity().runOnUiThread {
                        loadingPB.visibility = View.GONE
                    }
                    return
                }

                try {
                    val booksList = parseResults(responseData)
                    updateListWithData(booksList)
                } catch (e: JSONException) {
                    Log.e("TAG", "JSON Parsing Error: ${e.message}")
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        loadingPB.visibility = View.GONE
                    }
                }
            }
        })
    }
    private fun parseResults(responseData: String?): List<BookDetailsRVModel> {
        val booksList = mutableListOf<BookDetailsRVModel>()

        responseData?.let {
            try {
                val jsonObject = JSONObject(responseData)
                val docsArray = jsonObject.getJSONArray("docs")
                val maxBooksToShow = 50
                val booksCount = minOf(docsArray.length(), maxBooksToShow)

                for (i in 0 until booksCount) {
                    val bookObject = docsArray.getJSONObject(i)
                    val title = bookObject.optString("title")
                    val id = bookObject.optString("cover_i")
                    val olid = bookObject.optString("key") ?: bookObject.optString("olid")
                    val authorsArray = bookObject.optJSONArray("author_name")
                    val authorsArrayList: ArrayList<String> = ArrayList()
                    if (authorsArray!!.length() != 0) {
                        val maxAuthors = minOf(authorsArray.length(), 4)
                        for (j in 0 until maxAuthors) {
                            authorsArrayList.add(authorsArray.optString(j))
                        }
                    }

                    val bookInfo = BookDetailsRVModel(
                        title,
                        id,
                        olid,
                        authorsArrayList,
                        "",
                        "",
                        "",
                        "",
                        ""
                    )
                    fetchBookDetailsAndUpdateList(bookInfo, olid, booksList)
                    fetchBookDescriptionAndUpdateList(bookInfo, olid, booksList)
                    booksList.add(bookInfo)
                }
            } catch (e: Exception) {
                Log.w("TAG", "Exception")
                e.printStackTrace()
            }
        }
        return booksList
    }
    private fun updateListWithData(booksList: List<BookDetailsRVModel>) {
        val adapter = BookRVAdapter(booksList, requireContext())
        val layoutManager = GridLayoutManager(requireContext(), 3)
        requireActivity().runOnUiThread {
            val mRecyclerView = requireView().findViewById<RecyclerView>(R.id.idRVBooks)
            mRecyclerView.layoutManager = layoutManager
            mRecyclerView.adapter = adapter
        }
    }
    private fun fetchBookDetailsAndUpdateList(bookInfo: BookDetailsRVModel, olid: String, booksList: MutableList<BookDetailsRVModel>) {
        val client = OkHttpClient()
        val bookDetailsUrl = "https://openlibrary.org$olid.json"
        println(bookDetailsUrl)
        val descriptionRequest = Request.Builder().url(bookDetailsUrl).build()

        client.newCall(descriptionRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TAG", "Details request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val detailsResponseData = response.body?.string()
                val detailsJsonObject = JSONObject(detailsResponseData!!)
                val subjectsArray = detailsJsonObject.optJSONArray("subjects")
                val subjectPeopleArray = detailsJsonObject.optJSONArray("subject_people")
                val subjectTimesArray = detailsJsonObject.optJSONArray("subject_times")
                val publishedDate = detailsJsonObject.optString("first_publish_date")

                val subjectsStringBuilder = StringBuilder()
                subjectsArray?.let {
                    val maxSubjects = if (it.length() > 30) 30 else it.length()
                    for (j in 0 until maxSubjects) {
                        subjectsStringBuilder.append(it.optString(j))
                        if (j < maxSubjects - 1) {
                            subjectsStringBuilder.append(", ")
                        }
                    }
                }
                val subjects = subjectsStringBuilder.toString()

                val subjectPeopleStringBuilder = StringBuilder()
                subjectPeopleArray?.let {
                    val maxSubjectPeople = if (it.length() > 30) 30 else it.length()
                    for (j in 0 until maxSubjectPeople) {
                        subjectPeopleStringBuilder.append(it.optString(j))
                        if (j < maxSubjectPeople - 1) {
                            subjectPeopleStringBuilder.append(", ")
                        }
                    }
                }
                val subjectPeople = subjectPeopleStringBuilder.toString()

                val subjectsTimesStringBuilder = StringBuilder()
                subjectTimesArray?.let {
                    for (j in 0 until it.length()) {
                        subjectsTimesStringBuilder.append(it.optString(j))
                        if (j < it.length() - 1) {
                            subjectsTimesStringBuilder.append(", ")
                        }
                    }
                }
                val subjectTimes = subjectsTimesStringBuilder.toString()

                bookInfo.subjects = subjects
                bookInfo.subjectPeople = subjectPeople
                bookInfo.subjectTimes = subjectTimes
                bookInfo.publishedDate = publishedDate
                updateListWithData(booksList)

            }
        })
    }
    private fun fetchBookDescriptionAndUpdateList(bookInfo: BookDetailsRVModel, olid: String, booksList: MutableList<BookDetailsRVModel>) {
        val client = OkHttpClient()
        val descriptionUrl = "https://openlibrary.org$olid.json"
        val descriptionRequest = Request.Builder()
            .url(descriptionUrl)
            .build()

        client.newCall(descriptionRequest).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("TAG", "Description request failed: ${e.message}")
            }


                override fun onResponse(call: Call, response: Response) {
            if (!response.isSuccessful) {
                Log.e("TAG", "Non-successful response: ${response.code}")
                return
            }

            val descriptionResponseData = response.body?.string()
            try {
                val descriptionJsonObject = JSONObject(descriptionResponseData!!)
                val description = descriptionJsonObject.optString("description")
                val cleanDescription = description
                    .replace("\\r\\n", "\n")
                    .replace(Regex("\\{.*?\\}"), "")
                    .trim()

                val updatedDescription = if (cleanDescription.startsWith("{")) {
                    cleanDescription.substringAfter("\"value\":").trim()
                } else {
                    cleanDescription
                }
                bookInfo.description = updatedDescription
                requireActivity().runOnUiThread {
                    loadingPB.visibility = View.GONE
                }
                updateListWithData(booksList)
            } catch (e: JSONException) {
                Log.e("TAG", "JSON Parsing Error: ${e.message}")
                e.printStackTrace()
            }
        }
    })
    }
    private fun setCurrentFragment(fragment: Fragment) =
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}
