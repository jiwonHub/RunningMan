package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.domain.model.FeedModel


class ViewAdapter (
    private  var imageList: MutableList<FeedModel>,
    private val clickListener: OnItemClickListener,

    ) : RecyclerView.Adapter<ViewAdapter.Holder>() {
    interface OnItemClickListener{
        fun onItemClick(
            imageUrl: MutableList<String>,
            feedUid: MutableList<Char>,
            profileURL: String,
            title: String,
            content: String,
            likedCount: Int,
            isLiked : Boolean
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_recyclerview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageUrl = imageList[position].imageUrls
        for(i : Int in 0..<imageList.size){
            Log.d("ViewAdapter", "Loading image URL: ${imageList[i]}")

        }
        Glide.with(holder.feedImageView.context)
            .load(imageUrl[0])
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .centerCrop()
            .into(holder.feedImageView)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(imageList[position].imageUrls.toMutableList()
                ,imageList[position].feedUID.toMutableList()
                ,imageList[position].profileURL
                ,imageList[position].title
                ,imageList[position].content
                ,imageList[position].likedCount
                ,imageList[position].isLiked)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedImageView: ImageView = itemView.findViewById(R.id.feedImg)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateImages(newImages: MutableList<FeedModel>) {
        imageList = newImages
        notifyDataSetChanged()
    }


}
