package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R


class ViewAdapter (
    private  var imageList: List<String>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<ViewAdapter.Holder>() {
    interface OnItemClickListener{
        fun onItemClick(imageUrl : String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_recyclerview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageUrl = imageList[position]
        Log.d("ViewAdapter", "Loading image URL: $imageUrl")

        Glide.with(holder.feedImageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .centerCrop()
            .into(holder.feedImageView)

        holder.itemView.setOnClickListener {
            clickListener.onItemClick(imageUrl)
        }
    }

    override fun getItemCount(): Int {
       return imageList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedImageView: ImageView = itemView.findViewById(R.id.feedImg)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateImages(newImages: List<String>) {
        imageList = newImages
        notifyDataSetChanged()
    }


}
