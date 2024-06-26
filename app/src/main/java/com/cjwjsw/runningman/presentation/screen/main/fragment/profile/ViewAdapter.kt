package com.cjwjsw.runningman.presentation.screen.main.fragment.profile
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R

class ViewAdapter(
    private  var imageList: List<Uri>
) :  RecyclerView.Adapter<ViewAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_feed_recyclerview, parent, false)
        return Holder(view)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewAdapter.Holder, position: Int) {
        val imageUrl = imageList[position]
        Log.d("ViewAdapter", "Loading image URL: $imageUrl")
        Glide.with(holder.feedImageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .centerCrop()
            .into(holder.feedImageView)
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedImageView: ImageView = itemView.findViewById(R.id.addImage)
    }
}