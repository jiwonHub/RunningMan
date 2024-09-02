package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R

class FeedDetailViewAdapter(
    val image: ArrayList<String>?
) : RecyclerView.Adapter<FeedDetailViewAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedDetailViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_detail,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedDetailViewAdapter.ViewHolder, position: Int) {
            holder.bind(image?.get(position) ?: return )
    }

    override fun getItemCount(): Int {
        return image?.size ?: 0
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        private val view : ImageView = itemView.findViewById(R.id.feedimage)
        fun bind(item: String) {
            Glide.with(view)
                .load(item)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .centerCrop()
                .into(view)
        }
    }
}