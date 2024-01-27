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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.R
import com.example.bookbuddy.databinding.RowPostsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
//TODO: imie + nazwisko , to samo w postDetailsFr

class AdapterPosts(private val context: Context,
                   private var modelPosts: MutableList<ModelPost?>?) :
    RecyclerView.Adapter<AdapterPosts.MyHolder>() {

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

        getLikesCount(post.ptime!!, holder.like_count)
        getCommentsCount(post.ptime!!, holder.comments_count)
        checkIfLiked(post.ptime!!, holder.likebtn)
        getImage(post.uid!!,holder.picture)

        with(holder) {
            name.text = post.uname
            title.text = post.title
            description.text = post.description
            time.text = timedate

            try {
                Glide.with(context).load(post.upic).into(picture)
            } catch (_: Exception) {
            }

            more.visibility = View.GONE
            if (post.uid == myuid) {
                more.visibility = View.VISIBLE
            }
            more.setOnClickListener {
                showMoreOptions(more, position)
            }

            comment.setOnClickListener {
                val postDetailsFragment = PostDetailsFragment()
                val bundle = Bundle()
                // Przekazanie ID posta do PostDetailsFragment
                bundle.putString("pid", post.ptime)
                bundle.putString("uid", post.uid)
                postDetailsFragment.arguments = bundle

                if (itemView.context is AppCompatActivity) {
                    val appCompatActivity = itemView.context as AppCompatActivity
                    appCompatActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, postDetailsFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            likebtn.setOnClickListener { view ->
                likePost(post.ptime!!)
            }
        }
    }
    private fun getLikesCount(pid: String, likeCountTextView: TextView) {
        val likesRef = FirebaseDatabase.getInstance().getReference("Posts").child(pid).child("Likes")
        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val likesCount = dataSnapshot.childrenCount
                likeCountTextView.text = "Likes: $likesCount"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędu
                likeCountTextView.text = "Error"
            }
        })
    }

    private fun checkIfLiked(pid: String, likebtn: ImageView) {
        val likesRef =
            FirebaseDatabase.getInstance().getReference("Posts").child(pid).child("Likes")
                .child(myuid)
        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart_red)
                } else {
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędu w bazie danych
                Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


        private fun getImage(uid: String, imageView: CircleImageView) {
        val userInfoRef = FirebaseDatabase.getInstance().getReference("userInfo").child(uid)
        userInfoRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val imgUri = dataSnapshot.child("imgUrl").value.toString()
                    // Jeśli istnieje skrót do zdjęcia w Storage
                    if (imgUri.isNotEmpty()) {
                        // Pobierz adres URI obrazu z Firebase Storage
                        Glide.with(context)
                            .load(imgUri)
                            .into(imageView)
                        println(imgUri)
                    }
                } else {
                    // Ustaw domyślny obraz, jeśli brak skrótu do zdjęcia
                    // imageView.setImageResource(R.drawable.default_image)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


    }) }

    private fun getCommentsCount(pid: String, commentsCountTextView: TextView) {
        val commentsRef = FirebaseDatabase.getInstance().getReference("Posts").child(pid).child("Comments")
        commentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val commentsCount = dataSnapshot.childrenCount
                commentsCountTextView.text = "Comments: $commentsCount"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędu
                commentsCountTextView.text = "Error"
            }
        })
    }
    private fun likePost(pid: String) {
        val likesRef = FirebaseDatabase.getInstance().getReference("Posts").child(pid).child("Likes").child(myuid)
        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Użytkownik już polubił post, usuń polubienie
                    likesRef.removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(context, "Unliked", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to unlike: $e", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Użytkownik nie polubił jeszcze postu, polub post
                    likesRef.setValue(true)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to like: $e", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun Glide(pid: String, likebtn: ImageView) {
        val likesRef = FirebaseDatabase.getInstance().getReference("Posts").child(pid).child("Likes").child(myuid)
        likesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart_red)
                } else {
                    likebtn.setImageResource(com.example.bookbuddy.R.drawable.heart)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Obsługa błędu w bazie danych
                Toast.makeText(context, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun showMoreOptions(more: ImageButton, position: Int) {
        val popupMenu = PopupMenu(context, more, Gravity.END)
        popupMenu.menu.add(android.view.Menu.NONE, 0, 0, "DELETE")
        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == 0) {
                // Delete the post from the database
                val postIdToDelete = modelPosts!![position]?.ptime ?: ""
                deletePost(postIdToDelete)
            }
            false
        }
        popupMenu.show()
    }
    private fun deletePost(postId: String) {
        val postsRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId)
        postsRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Post deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to delete post: $e", Toast.LENGTH_SHORT).show()
            }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newPosts: MutableList<ModelPost?>?) {
        modelPosts = newPosts
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return modelPosts?.size ?: 0
    }
    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val picture: CircleImageView = itemView.findViewById(R.id.picturetv)
        val name: TextView = itemView.findViewById(R.id.unametv)
        val time: TextView = itemView.findViewById(R.id.utimetv)
        val more: ImageButton = itemView.findViewById(R.id.morebtn)
        val title: TextView = itemView.findViewById(R.id.ptitletv)
        val description: TextView = itemView.findViewById(R.id.descript)
        val like_count: TextView = itemView.findViewById(R.id.plike_count)
        val comments_count: TextView = itemView.findViewById(R.id.pcomment_count)
        val likebtn: ImageView = itemView.findViewById(R.id.like_iv)
        val comment: Button = itemView.findViewById(R.id.comment)
        val profile: LinearLayout = itemView.findViewById(R.id.profilelayout)
    }
}
