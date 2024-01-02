package com.example.bookbuddy.searchView

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
//import com.example.bookbuddy.databinding.FragmentSearchBinding
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.FragmentSearchBinding
import com.google.firebase.auth.FirebaseAuth


class SearchFragment : Fragment(R.layout.fragment_search){
//    private lateinit var bindingSearch: FragmentSearchBinding
    lateinit var sendButton :Button
    private lateinit var bindingSearch: FragmentSearchBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mRequestQueue: RequestQueue
    lateinit var booksList: ArrayList<BookRVModal>
    lateinit var loadingPB: ProgressBar
    lateinit var searchEdt: EditText
    lateinit var searchBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
//        bindingSearch = FragmentSearchBinding.inflate(inflater, container, false)

//        val rootView = inflater.inflate(R.layout.fragment_search, container, false)
        bindingSearch = FragmentSearchBinding.inflate(inflater, container, false)
        val numberOfBooks = arguments?.getInt("numberOfBooks", 0)
        if (numberOfBooks!=null)
        {
            sendButton= bindingSearch.idBtnSend

// Ustawiamy widoczność przycisku w zależności od liczby książek
            if (numberOfBooks == 3) {
                sendButton.visibility = View.VISIBLE
            } else {
                sendButton.visibility = View.GONE
            }
        }
        firebaseAuth= FirebaseAuth.getInstance()


        // on below line we are initializing
        // our variable with their ids.

//        loadingPB = rootView.findViewById(R.id.idLoadingPB)
//        searchEdt = rootView.findViewById(R.id.idEdtSearchBooks)
//        searchBtn = rootView.findViewById(R.id.idBtnSearch)
        loadingPB = bindingSearch.idLoadingPB
        searchEdt = bindingSearch.idEdtSearchBooks
        searchBtn = bindingSearch.idBtnSearch

        // adding click listener for search button
        searchBtn.setOnClickListener {
            loadingPB.visibility = View.VISIBLE

            // checking if our edittext field is empty or not.
            if (searchEdt.text.toString().isEmpty()) {
                searchEdt.setError("Please enter search query")
            }

            // if the search query is not empty then we are
            // calling get book info method to load all
            // the books from the API.
            getBooksData(searchEdt.text.toString())
        }
        return bindingSearch.root
    }


    private fun getBooksData(searchQuery: String) {

        // creating a new array list.
        booksList = ArrayList()
        // below line is use to initialize
        // the variable for our request queue.
        mRequestQueue = Volley.newRequestQueue(requireContext())
        // below line is use to clear cache this
        // will be use when our data is being updated.
        mRequestQueue.cache.clear()

        // below is the url for getting data from API in json format.
        val url = "https://www.googleapis.com/books/v1/volumes?q=$searchQuery"
        // below line we are  creating a new request queue.
        val queue = Volley.newRequestQueue(requireContext())

        // on below line we are creating a variable for request
        // and initializing it with json object request
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            loadingPB.visibility = View.GONE
            // inside on response method we are extracting all our json data.
            try {
                val itemsArray = response.getJSONArray("items")
                for (i in 0 until itemsArray.length()) {
                    val itemsObj = itemsArray.getJSONObject(i)
                    val volumeObj = itemsObj.getJSONObject("volumeInfo")
                    val title = volumeObj.optString("title")
                    val subtitle = volumeObj.optString("subtitle")
                    val authorsArray = volumeObj.getJSONArray("authors")
                    val publisher = volumeObj.optString("publisher")
                    val publishedDate = volumeObj.optString("publishedDate")
                    val description = volumeObj.optString("description")
                    val pageCount = volumeObj.optInt("pageCount")
                    val imageLinks = volumeObj.optJSONObject("imageLinks")
                    val thumbnail = imageLinks!!.optString("thumbnail")
                    val previewLink = volumeObj.optString("previewLink")
                    val infoLink = volumeObj.optString("infoLink")
                    val saleInfoObj = itemsObj.optJSONObject("saleInfo")
                    val buyLink = saleInfoObj!!.optString("buyLink")
                    val authorsArrayList: ArrayList<String> = ArrayList()
                    if (authorsArray.length() != 0) {
                        for (j in 0 until authorsArray.length()) {
                            authorsArrayList.add(authorsArray.optString(i))
                        }
                    }

                    // after extracting all the data we are
                    // saving this data in our modal class.
                    val bookInfo = BookRVModal(
                        title,
                        subtitle,
                        authorsArrayList,
                        id,
                        publisher,
                        publishedDate,
                        description,
                        pageCount,
                        thumbnail,
                        previewLink,
                        infoLink,
                        buyLink
                    )
                    // below line is use to pass our modal
                    // class in our array list.
                    booksList.add(bookInfo)

                    // below line is use to pass our
                    // array list in adapter class.
                    val adapter = BookRVAdapter(booksList, requireContext())
                    // below line is use to add linear layout
                    // manager for our recycler view.
                    val layoutManager = GridLayoutManager(requireContext(), 3)
                    val mRecyclerView = requireView().findViewById<RecyclerView>(R.id.idRVBooks)

                    // in below line we are setting layout manager and
                    // adapter to our recycler view.
                    mRecyclerView.layoutManager = layoutManager
                    mRecyclerView.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, { error ->
            Toast.makeText(requireContext(), "No books found..", Toast.LENGTH_SHORT).show()
        })

        queue.add(request)
    }


}