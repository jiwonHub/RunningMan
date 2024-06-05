package com.cjwjsw.runningman.presentation.screen.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cjwjsw.runningman.databinding.ItemFeedRecyclerviewBinding
import com.cjwjsw.runningman.domain.model.FeedImageModel


//데이터 테이블을 목록 형태로 보여주기 위한 어댑터
// viewHoler ? 화면에 표시될 데이터나 아이템들을 저장하는 역할
class viewAdapter (val uri : ArrayList<FeedImageModel>) : RecyclerView.Adapter<viewAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewAdapter.Holder {
        //viewHolder가 생성되는 함수
        //리사이클러뷰 정보 Holder로 넘기기
        val binding = ItemFeedRecyclerviewBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: viewAdapter.Holder, position: Int) {
        holder.binding.feedImg
    }

    override fun getItemCount(): Int {
       return uri.size
    }

    inner class Holder(val binding: ItemFeedRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root){
        val uri = binding.feedImg
    }
}