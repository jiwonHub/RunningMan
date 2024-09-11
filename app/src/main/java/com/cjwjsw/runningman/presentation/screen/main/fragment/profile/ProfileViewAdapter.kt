package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.domain.model.FeedModel

class ProfileViewAdapter(
    private var feed: MutableList<FeedModel>,
    private val clickListener: ProfileFragment
) :
    RecyclerView.Adapter<ProfileViewAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onItemClick(
            imageUrl: MutableList<String>,
            feedUid: MutableList<Char>,
            profileURL: String,
            title: String,
            content: String,
            likedCount: Int,
            isLiked : Boolean,
        )
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profilefeed_recycerview, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       val imageUrl = feed[position].imageUrls

        Glide.with(holder.imageView.context)
            .load(imageUrl[0])
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(feed[position].imageUrls.toMutableList()
                ,feed[position].feedUID.toMutableList()
                ,feed[position].profileURL
                ,feed[position].title
                ,feed[position].content
                ,feed[position].likedCount
                ,feed[position].isLiked)
        }
    }

    override fun getItemCount(): Int {
        return feed.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateImages(newImages: MutableList<FeedModel>) {
        feed = newImages
        notifyDataSetChanged()
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: AppCompatImageView = itemView.findViewById(R.id.feedimg)
    }
}