package com.example.bookbuddy.searchView


import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.example.bookbuddy.R
import com.bumptech.glide.request.target.Target


class BookRVAdapter(

    private var bookList: List<BookDetailsRVModel>,
    private var context: Context
) : RecyclerView.Adapter<BookRVAdapter.BookViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        val inflater = LayoutInflater.from(context)
        val itemView = inflater.inflate(R.layout.book_item, parent, false)
        return BookViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BookRVAdapter.BookViewHolder, position: Int) {
        val currentBook = bookList[position]
        holder.bookTitleTV.text = currentBook.title
        holder.bookAuthor.text = currentBook.authors.joinToString(", ")

        val thumbnailUrl = "https://covers.openlibrary.org/b/id/${currentBook.id}-S.jpg"
        loadThumbnailWithRetry(currentBook, thumbnailUrl, holder.bookThumbnail)

        // below line is use to add on click listener for our item of recycler view.
        holder.itemView.setOnClickListener {
            // inside on click listener method we are calling a new activity
            // and passing all the data of that item in next intent.
            val i = Intent(context, BookDetailsActivity::class.java)
            i.putExtra("title", currentBook.title)
            i.putExtra("id",currentBook.id)
            i.putExtra("olid",currentBook.olid)
            i.putExtra("authors", currentBook.authors.joinToString())
            i.putExtra("description", currentBook.description)
            i.putExtra("publisher", currentBook.publisher)
            i.putExtra("publishedDate", currentBook.publishedDate)
            i.putExtra("previewLink", currentBook.previewLink)

            // after passing that data we are
            // starting our new intent.
            context.startActivity(i)
        }
    }
    private fun loadThumbnailWithRetry(
        book: BookDetailsRVModel,
        url: String,
        imageView: ImageView,
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
                        var nextUrl = url

                        if (retryCount > 0) {
                            val newRetryCount = retryCount - 1

                            nextUrl = when {
                                url.contains("/id/") -> {
                                    // If loading with ID fails, try OLID
                                    "https://covers.openlibrary.org/b/olid/${book.olid}-S.jpg"
                                }
                                url.contains("/olid/") -> {
                                    // If OLID fails, stop trying
                                    url
                                }
                                else -> {
                                    // Last attempt: Try OLID if previous attempts fail
                                    "https://covers.openlibrary.org/b/id/${book.id}-S.jpg"
                                }
                            }

                            loadThumbnailWithRetry(book, nextUrl, imageView, newRetryCount)
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
                .into(imageView)
        }
    }


    override fun getItemCount(): Int {
        return bookList.size
    }
    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // on below line we are initializing our
        // course name text view and our image view.
        val bookTitleTV: TextView = itemView.findViewById(R.id.idTVBookName)
        val bookThumbnail: ImageView = itemView.findViewById(R.id.idIVBook)
        var bookAuthor: TextView = itemView.findViewById(R.id.idTVBookAuthor)

    }
}
