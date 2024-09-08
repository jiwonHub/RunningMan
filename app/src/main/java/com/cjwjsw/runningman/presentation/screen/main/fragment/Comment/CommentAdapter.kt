package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.domain.model.CommentModel
import com.cjwjsw.runningman.presentation.screen.main.fragment.social.DetailFeedViewModel

class CommentAdapter(
    private val comment: List<CommentModel>,
    private val viewModel: DetailFeedViewModel
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_recycerview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("FeedDetailCommenAdapter",comment.size.toString())
        return comment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comment[position].profileUrl)
        holder.commentUpload(comment[position].comment, comment[position].userName)
        Log.d("FeedDetailCommenAdapter", comment[position].toString())
        //아이템 간 간격 설정
        val layoutParams = holder.itemView.layoutParams
        layoutParams.height = 150
        holder.itemView.requestLayout()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val view: ImageView = itemView.findViewById(R.id.commentProfileImage)
        private val commentText: TextView = itemView.findViewById(R.id.comment)
        private val userName: TextView = itemView.findViewById(R.id.userName)
        private val deleteBtn : ImageButton = itemView.findViewById(R.id.manageBtn)


        fun bind(item: String) {
            Glide.with(view)
                .load(item)
                .centerCrop()
                .into(view)

            deleteBtn.setOnClickListener {
                viewModel.deleteComment(UserManager.getInstance()?.idToken.toString())
            }
        }

        fun commentUpload(item: String, name: String) {
            commentText.text = item
            userName.text = name
        }

    }
}