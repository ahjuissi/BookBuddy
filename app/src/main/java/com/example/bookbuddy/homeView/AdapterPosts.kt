package com.example.bookbuddy.homeView

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.databinding.RowPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AdapterPosts(private val context: Context, private var modelPosts: MutableList<ModelPost?>?) :
    RecyclerView.Adapter<AdapterPosts.MyHolder>() {

    private lateinit var bindingRowPosts: RowPostsBinding

    //TODO: do spr
    private var myuid: String = FirebaseAuth.getInstance().currentUser!!.uid
//    private val liekeref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Likes")
//    private val postref: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Posts")
//    private var mprocesslike = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        bindingRowPosts = RowPostsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyHolder(bindingRowPosts.root)
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
//            like.text = "${post.plike} Likes"
//            comments.text = "${post.pcomments} Comments"

            try {
                Glide.with(context).load(post.upic).into(picture)
            } catch (_: Exception) {
            }


//            like.setOnClickListener {
//                val intent = Intent(holder.itemView.context, PostLikedByActivity::class.java)
//                intent.putExtra("pid", post.ptime)
//                holder.itemView.context.startActivity(intent)
//            }

//            likebtn.setOnClickListener {
//                val plike = post.plike!!.toInt()
//                mprocesslike = true
//                val postid = post.ptime
//                liekeref.addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(dataSnapshot: DataSnapshot) {
//                        if (mprocesslike) {
//                            mprocesslike = if (dataSnapshot.child(postid!!).hasChild(myuid)) {
//                                postref.child(postid).child("plike").setValue("" + (plike - 1))
//                                liekeref.child(postid).child(myuid).removeValue()
//                                false
//                            } else {
//                                postref.child(postid).child("plike").setValue("" + (plike + 1))
//                                liekeref.child(postid).child(myuid).setValue("Liked")
//                                false
//                            }
//                        }
//                    }
//
//                    override fun onCancelled(databaseError: DatabaseError) {}
//                })
//            }

            more.setOnClickListener {
                showMoreOptions(more, post.uid, myuid, post.ptime)
            }
//
//            comment.setOnClickListener {
//                val intent = Intent(context, PostDetailsActivity::class.java)
//                intent.putExtra("pid", post.ptime)
//                context.startActivity(intent)
//            }
        }
    }

    private fun showMoreOptions(
        more: android.widget.ImageButton,
        uid: String?,
        myuid: String,
        pid: String?
    ) {
        val popupMenu = PopupMenu(context, more, Gravity.END)
        if (uid == myuid) {
            popupMenu.menu.add(android.view.Menu.NONE, 0, 0, "DELETE")
        }
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 0) {
                //TODO: usuń post
            }
            false
        }
        popupMenu.show()
    }
    //odświeżenie recycleview
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPosts: MutableList<ModelPost?>?) {
        modelPosts = newPosts
        notifyDataSetChanged()
    }


//    private fun setLikes(holder: MyHolder, pid: String?) {
//        liekeref.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                if (dataSnapshot.child(pid!!).hasChild(myuid)) {
//                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.common_google_signin_btn_icon_light_focused, 0, 0, 0)
//                    holder.likebtn.text = "Liked"
//                } else {
//                    holder.likebtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.common_google_signin_btn_icon_dark, 0, 0, 0)
//                    holder.likebtn.text = "Like"
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {}
//        })
//    }

    override fun getItemCount(): Int {
        return modelPosts!!.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: ImageView = bindingRowPosts.picturetv
        val name: TextView = bindingRowPosts.unametv
        val time: TextView = bindingRowPosts.utimetv
        val more: ImageButton = bindingRowPosts.morebtn
        val title: TextView = bindingRowPosts.ptitletv
        val description: TextView = bindingRowPosts.descript
        val like: TextView = bindingRowPosts.plikeb
        val comments: TextView = bindingRowPosts.pcommentco
        val likebtn: Button = bindingRowPosts.like
        val comment: Button = bindingRowPosts.comment
        val profile: LinearLayout = bindingRowPosts.profilelayout
    }
}
