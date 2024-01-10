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
import org.json.JSONObject
import java.io.IOException


class SearchFragment : Fragment(R.layout.fragment_search) {
    //    private lateinit var bindingSearch: FragmentSearchBinding
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
                        // Jeśli użytkownik ma rolę "Admin", pokaż przycisk
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

//        val title = arguments?.getString("title")
//        if (!title.isNullOrEmpty()) {
//            getBooksData(title)
//        }
        return bindingSearch.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // adding click listener for search button
        searchBtn.setOnClickListener {
            loadingPB.visibility = View.VISIBLE
            val searchTerm = searchEdt.text.toString()
            // checking if our edittext field is empty or not.
            if (searchTerm.isEmpty()) {
                searchEdt.error = "Please enter a search query" // Ustawienie błędu dla pola wyszukiwania
                loadingPB.visibility = View.GONE // Ukrycie wskaźnika ładowania, ponieważ nie ma żadnego zapytania
            } else {
                println("edit nie pusty leciiiii")
                getBooksData(searchTerm) // Wywołanie funkcji, jeśli pole wyszukiwania nie jest puste
                loadingPB.visibility = View.GONE
            }
        }
    }
    private fun getBooksData(searchTerm: String) {
        val client = OkHttpClient()
        val url = "https://openlibrary.org/search.json?q=$searchTerm"
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle errors
                Log.w("TAG", "onFailure")
            }
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                Log.w("TAG", "Processing JSON response data")
                val booksList = parseResults(responseData)
                updateListWithData(booksList)
            }
        })
    }
    private fun parseResults(responseData: String?): List<BookDetailsRVModel> {
        val booksList = mutableListOf<BookDetailsRVModel>()

        responseData?.let {
            try {
                val jsonObject = JSONObject(responseData)
                val docsArray = jsonObject.getJSONArray("docs")

                val maxBooksToShow = 4
                val booksCount = minOf(docsArray.length(), maxBooksToShow)

                for (i in 0 until booksCount) {
                    val bookObject = docsArray.getJSONObject(i)
                    val title = bookObject.optString("title")
                    val id = bookObject.optString("cover_i")
                    val olid = bookObject.optString("key") ?: bookObject.optString("olid")
                    val authorsArray = bookObject.optJSONArray("author_name")
                    val authorsArrayList: ArrayList<String> = ArrayList()
                    if (authorsArray!!.length() != 0) {
                        val maxAuthors = minOf(authorsArray.length(), 4) // Ograniczenie do maksymalnie 4 autorów
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
                    fetchBookDetailsAndUpdateList(bookInfo, olid, booksList) //test
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
        // Aktualizacja widoku RecyclerView na wątku głównym
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
                // Handle description request failure
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
                // Handle description request failure
                Log.e("TAG", "Description request failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val descriptionResponseData = response.body?.string()
                val descriptionJsonObject = JSONObject(descriptionResponseData!!)
                val description = descriptionJsonObject.optString("description")
                val updatedDescription = if (description.startsWith("{")) {
                    description.substringAfter("\"value\":").trim()
                } else {
                    description
                }
                    bookInfo.description = updatedDescription
                    updateListWithData(booksList)

            }
        })
    }
    private fun setCurrentFragment(fragment: Fragment) =
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            //    addToBackStack(null)
            commit()
        }
}
