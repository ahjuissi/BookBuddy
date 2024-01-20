package com.example.bookbuddy.profileViewUser

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.bookbuddy.R
import com.example.bookbuddy.searchView.SearchFragment
import com.example.bookbuddy.voteView.VotingViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavBookAdapter(private val mList: MutableList<FavBookViewModel>) : RecyclerView.Adapter<FavBookAdapter.ViewHolder>() {

    private var selectedItem: FavBookViewModel? = null
    private val database = FirebaseDatabase.getInstance()
    private val votesReference = database.reference.child("Voting")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavBookAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_fav, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val favBookItem = mList[position]
        holder.bind(favBookItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val authorView :TextView = itemView.findViewById(R.id.publisherView)
        private val idIVBook: ImageView = itemView.findViewById(R.id.idIVBook)
      //  private var loadingPB: ProgressBar = itemView.findViewById(R.id.idLoadingPB)
        private val context = itemView.context

        init {
            itemView.findViewById<LinearLayout>(R.id.Book).setOnClickListener {
                val item = mList[position]
                val id = item.id
                val title = item.title
                val searchFragment = SearchFragment()

                if (context is AppCompatActivity) {
                    val appCompatActivity = context as AppCompatActivity

                    val bundle = Bundle()
                    bundle.putString("id", id)
                    bundle.putString("title", title)
                    searchFragment.arguments = bundle

                    appCompatActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, searchFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        fun bind(item: FavBookViewModel) {
            titleView.text = item.title
            authorView.text=item.authors
            val firebaseAuth = FirebaseAuth.getInstance()
            val userId = firebaseAuth.currentUser?.uid
            val thumbnailRef = FirebaseDatabase.getInstance()
                .getReference("userInfo/${userId}/favourite/${item.id}/thumbnail")
            thumbnailRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val thumbnail = snapshot.getValue(String::class.java)
                    thumbnail?.let {
                        item.id?.let { it1 ->
                            item.thumbnail?.let { it2 ->
                                loadThumbnailWithRetry(
                                    it1,
                                    it2, idIVBook
                                )
                            }
                        }
                    //    loadingPB.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                 //   loadingPB.visibility = View.GONE
                }
            })
        }

        private fun loadThumbnailWithRetry(
            cleanedOlid: String,
            url: String,
            bookIV: ImageView,
            retryCount: Int = 3
        ) {
            val handler = Handler(Looper.getMainLooper())

            handler.post {
                Glide.with(context)
                    .load(url)
                    .apply(RequestOptions().error(R.drawable.baseline_broken_image_24))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            if (retryCount > 0) {
                                val newRetryCount = retryCount - 1
                                loadThumbnailWithRetry(cleanedOlid, url, bookIV, newRetryCount)
                            }

                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(bookIV)
            }
        }
    }

    fun updateList(newList: List<FavBookViewModel>) {
        mList.clear()
        mList.addAll(newList)
        notifyDataSetChanged()
    }
}
