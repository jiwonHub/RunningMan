package com.cjwjsw.runningman.presentation.screen.main.fragment.Comment

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.domain.model.CommentModel

class CommentAdapter(
    private val comment: List<CommentModel>,
    private val feedUid : String,
    private val fragmentManager: FragmentManager
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
        holder.commentUpload(comment[position].comment, comment[position].userName)
        holder.setCommentKey(comment[position].newCommentKey)
        holder.bind(comment[position].profileUrl)
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
        private var commentKey : String = ""


        fun setCommentKey(key : String){
            commentKey = key
        }

        fun getCommentKey() : String{
            Log.d(TAG,"getCommentKey : $commentKey")
            return commentKey
        }

        fun bind(item: String) {
            Glide.with(view)
                .load(item)
                .centerCrop()
                .into(view)
            val modal = Comment2thBottomSheet(
                feedUid,
                commentKey = (getCommentKey())
            )

            deleteBtn.setOnClickListener {
               modal.show(fragmentManager,Comment2thBottomSheet.TAG)
            }
        }

        fun commentUpload(item: String, name: String) {
            commentText.text = item
            userName.text = name
        }
    }

    companion object{
        const val TAG = "CommentAdapter"
    }

}