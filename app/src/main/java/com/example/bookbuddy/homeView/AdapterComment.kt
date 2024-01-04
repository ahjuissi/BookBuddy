package com.example.bookbuddy.homeView

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookbuddy.R

import java.util.Calendar
import java.util.Locale

class AdapterComment(
    private val context: Context,
    private var list: List<ModelComment>,
    private val myuid: String,
    private val postid: String
) :
    RecyclerView.Adapter<AdapterComment.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.row_comments, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val commentData = list[position]

        holder.name.text = commentData.uname
        holder.comment.text = commentData.comment


        commentData.ptime?.let {
            val calendar = Calendar.getInstance(Locale.ENGLISH)
            calendar.timeInMillis = it.toLong()
            val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()
            holder.time.text = timedate
        } ?: run {
            holder.time.text = "No date available" // lub dowolny inny tekst
        }

        try {
            Glide.with(context).load(commentData.udp).into(holder.imagea)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    fun updateComments(newComments: List<ModelComment>) {
        list = newComments
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagea: ImageView = itemView.findViewById(R.id.loadcomment)
        var name: TextView = itemView.findViewById(R.id.commentname)
        var comment: TextView = itemView.findViewById(R.id.commenttext)
        var time: TextView = itemView.findViewById(R.id.commenttime)
    }
}
