package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R


class ViewpageAdapter(private val arr: List<Uri>) :
    RecyclerView.Adapter<ViewpageAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arr[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_add_feed_recyclerview, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: AppCompatImageView = itemView.findViewById(R.id.addImage)

        fun bind(item: Uri) {
            Glide.with(imageView)
                .load(item)
                .into(imageView)
        }
    }
}
