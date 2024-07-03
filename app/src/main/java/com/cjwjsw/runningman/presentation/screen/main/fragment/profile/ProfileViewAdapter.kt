package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R

class ProfileViewAdapter(private val feed : List<String>) :
    RecyclerView.Adapter<ProfileViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profilefeed_recycerview, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(feed[position])
    }

    override fun getItemCount(): Int {
        return feed.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: AppCompatImageView = itemView.findViewById(R.id.feedimg)
        fun bind(item: String) {
            Glide.with(imageView)
                .load(item)
                .into(imageView)
        }
    }
}