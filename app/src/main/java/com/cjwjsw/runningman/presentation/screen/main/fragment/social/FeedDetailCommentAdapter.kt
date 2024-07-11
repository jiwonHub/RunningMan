package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.domain.model.CommentModel

class FeedDetailCommentAdapter(
        val comment : MutableList<CommentModel>,
        val profileImage : String
): RecyclerView.Adapter<FeedDetailCommentAdapter.ViewHolder>() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedDetailCommentAdapter.ViewHolder {
      val view = LayoutInflater.from(parent.context)
          .inflate(R.layout.item_comment_recycerview,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comment.size
    }

    override fun onBindViewHolder(holder: FeedDetailCommentAdapter.ViewHolder, position: Int) {
        holder.bind(profileImage)
        holder.commentUpload(comment[position])
    }

    inner class ViewHolder(view : View) : RecyclerView.ViewHolder(view){
        private val view : ImageView = itemView.findViewById(R.id.commentProfileImage)
        private val commentText : TextView = itemView.findViewById(R.id.comment)
        private val userName : TextView = itemView.findViewById(R.id.userName)
        //private val commentTime : TextView = itemView.findViewById(R.id.timeTextView) 시간 입력

        fun bind(item : String){
            Glide.with(view)
                .load(item)
                .into(view)
        }

        fun commentUpload(item : CommentModel){
                commentText.text = item.comment.joinToString { " " }
                userName.text = item.id
        }
    }

}