package com.example.bookbuddy.voteView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.bookbuddy.R
import com.example.bookbuddy.searchView.BookDetailsActivity
import com.example.bookbuddy.searchView.SearchFragment
import com.squareup.picasso.Picasso
import java.util.*
class VotingAdapter(private var mList: MutableList<VotingViewModel>,
                    private val onItemClick: (VotingViewModel) -> Unit,
                    private val onDeleteClick: (VotingViewModel) -> Unit ) :
    RecyclerView.Adapter<VotingAdapter.ViewHolder>() {
    private var selectedItem: VotingViewModel? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.voting_list_view_design, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val votingItem = mList[position]
        holder.bind(votingItem)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val publisherView: TextView = itemView.findViewById(R.id.publisherView)
        private val bookLayout: LinearLayout = itemView.findViewById(R.id.Book)
        init {
            itemView.findViewById<ImageView>(R.id.delButton).setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = mList[position]
                    mList.removeAt(position)
                    notifyDataSetChanged()
                    onDeleteClick.invoke(item) // Wywoł
                }
            }
            itemView.findViewById<LinearLayout>(R.id.Book).setOnClickListener {
                val item = mList[position]
                val id = item.id
                val searchFragment = SearchFragment()

                if (itemView.context is AppCompatActivity) {
                    val appCompatActivity = itemView.context as AppCompatActivity

                    val bundle = Bundle()
                    bundle.putString("id", id)
                    searchFragment.arguments = bundle

                    appCompatActivity.supportFragmentManager.beginTransaction()
                        .replace(R.id.flFragment, searchFragment)
                        .addToBackStack(null)
                        .commit()
                }

            }
        }
            fun bind(item: VotingViewModel) {
                titleView.text = item.title
                publisherView.text = item.publisher
                itemView.setOnClickListener {
                    selectedItem = item
                    onItemClick(item)
                }
            }
        }

        fun updateList(newList: List<VotingViewModel>) {
            mList.clear()
            mList.addAll(newList)
            notifyDataSetChanged()
        }
    }

