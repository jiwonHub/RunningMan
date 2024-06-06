package com.cjwjsw.runningman.presentation.screen.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R


//데이터 테이블을 목록 형태로 보여주기 위한 어댑터
// viewHoler ? 화면에 표시될 데이터나 아이템들을 저장하는 역할
class viewAdapter (private  var imageList: List<String>) : RecyclerView.Adapter<viewAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewAdapter.Holder {
        //viewHolder가 생성되는 함수
        //리사이클러뷰 정보 Holder로 넘기기
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feed_recyclerview, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val imageUrl = imageList[position]
        Log.d("ViewAdapter", "Loading image URL: $imageUrl")

        Glide.with(holder.feedImageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.sun) // Placeholder image while loading
            .error(R.drawable.calories) // Error image if loading fails
            .into(holder.feedImageView)
    }

    override fun getItemCount(): Int {
       return imageList.size
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedImageView: ImageView = itemView.findViewById(R.id.feedImg)
    }

    // Method to update the image list
    fun updateImages(newImages: List<String>) {
        imageList = newImages
        notifyDataSetChanged()
    }
}