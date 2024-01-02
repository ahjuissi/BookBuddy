package com.example.bookbuddy.voteView

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.transition.Transition
import com.example.bookbuddy.R

class VotingAdapter(private val mList: MutableList<VotingViewModel>) : RecyclerView.Adapter<VotingAdapter.ViewHolder>(),
    Transition.ViewAdapter {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voting_list_view_design, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view ::  , payloads: MutableList<Any>
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val VotingViewModel = mList[position]
        holder.titleView.text = VotingViewModel.title
        holder.publisherView.text = VotingViewModel.publisher

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val titleView: TextView = itemView.findViewById(R.id.titleView)
        val publisherView: TextView = itemView.findViewById(R.id.publisherView)
    }

    override fun getView(): View {
        TODO("Not yet implemented")
    }

    override fun getCurrentDrawable(): Drawable? {
        TODO("Not yet implemented")
    }

    override fun setDrawable(drawable: Drawable?) {
        TODO("Not yet implemented")
    }

}