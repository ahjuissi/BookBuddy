package com.example.bookbuddy.homeView

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.RowPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class AdapterPosts(private val context: Context,
                   private var modelPosts: MutableList<ModelPost?>?) :
    RecyclerView.Adapter<AdapterPosts.MyHolder>() {
    private lateinit var bindingRowPosts: RowPostsBinding

    private var myuid: String = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.bookbuddy.R.layout.row_posts, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val post = modelPosts!![position]
        val calendar = Calendar.getInstance(Locale.ENGLISH)
        calendar.timeInMillis = post?.ptime!!.toLong()
        val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()

        with(holder) {
            name.text = post.uname
            title.text = post.title
            description.text = post.description
            time.text = timedate

            try {
                Glide.with(context).load(post.upic).into(picture)
            } catch (_: Exception) {
            }

            likebtn.setOnClickListener { view ->
                if (likebtn.isSelected) {
                    likebtn.isSelected = false
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart)
                    println("un like")
                } else {
                    likebtn.isSelected = true
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart_red)
                    println("like")
                }
            }

            more.visibility = View.GONE
            if (post.uid == myuid) {
                more.visibility = View.VISIBLE
            }
            more.setOnClickListener {
                showMoreOptions(more, post.uid, myuid, post.ptime)
            }

            comment.setOnClickListener {
                val postDetailsFragment = PostDetailsFragment()
                val bundle = Bundle()
                // Przekazanie ID posta do PostDetailsFragment
                bundle.putString("pid", post?.pid)
                postDetailsFragment.arguments = bundle

                if (itemView.context is AppCompatActivity) {
                    val appCompatActivity = itemView.context as AppCompatActivity
                    appCompatActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, postDetailsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    private fun showMoreOptions(
        more: ImageButton,
        uid: String?,
        myuid: String,
        pid: String?
    ) {
        val popupMenu = PopupMenu(context, more, Gravity.END)
        popupMenu.menu.add(android.view.Menu.NONE, 0, 0, "DELETE")
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 0) {
                //TODO: usuń post
            }
            false
        }
        popupMenu.show()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPosts: MutableList<ModelPost?>?) {
        modelPosts = newPosts
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return modelPosts!!.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = itemView.findViewById(R.id.picturetv)
        val name: TextView = itemView.findViewById(R.id.unametv)
        val time: TextView = itemView.findViewById(R.id.utimetv)
        val more: ImageButton = itemView.findViewById(R.id.morebtn)
        val title: TextView = itemView.findViewById(R.id.ptitletv)
        val description: TextView = itemView.findViewById(R.id.descript)
        val like: TextView = itemView.findViewById(R.id.plikeb)
        val comments: TextView = itemView.findViewById(R.id.pcommentco)
        val likebtn: ImageView = itemView.findViewById(R.id.like_iv)
        val comment: Button = itemView.findViewById(R.id.comment)
        val profile: LinearLayout = itemView.findViewById(R.id.profilelayout)
    }
}
