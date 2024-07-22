package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivitiyFeedDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailScreen: AppCompatActivity() {
    private lateinit var binding: ActivitiyFeedDetailBinding
    private lateinit var viewPager : ViewPager2
    private lateinit var adapter : FeedDetailViewAdapter
    private lateinit var commentAdapter: FeedDetailCommentAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel : FeedViewModel by viewModels()
    private val profileUrl = UserManager.getInstance()?.profileUrl
    private val userName = UserManager.getInstance()?.nickName
    private var comment : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = intent.getStringExtra("UID")
        val image = intent.getStringArrayListExtra("URL")
        Log.d("FeedDetailScreen",uid.toString())
        Log.d("FeedDetailScreen",image.toString())
        adapter = FeedDetailViewAdapter(image)
        adapter = image?.let { FeedDetailViewAdapter(it) }!!
        viewPager = binding.feedImgViewPager
        viewPager.adapter = adapter

        binding.indicator.setViewPager(binding.feedImgViewPager)

        loadProfileImg()
        loadTextProfileImg()
        viewModel.fetchCommentData(uid.toString())
        recyclerView = binding.feedDetailRecycerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        commentAdapter = FeedDetailCommentAdapter(emptyList(),profileUrl.toString(),userName.toString())
        recyclerView.adapter = commentAdapter

        binding.backBtn.setOnClickListener {
            finish()
        }

        viewModel.commentArr.observe(this){ arr ->
            //TODO 댓글 RecycerView 텍스트 입력 정의 이곳에
            Log.d("FeedDetailScreen","Livedata 댓글 : ${userName.toString()}")
            commentAdapter = arr?.let { FeedDetailCommentAdapter(arr,profileUrl.toString(),userName.toString()) }!!
            recyclerView.adapter = commentAdapter
        }


        binding.commentUploadBtn.setOnClickListener {
            comment = binding.editText.text.toString()
            viewModel.uploadComment(comment,uid.toString())
        }
    }

//    override fun onStart() {
//        super.onStart()
//        viewModel.fetchCommentData(uid.toString())
//    }

    private fun loadProfileImg(){
        if (profileUrl != null) {
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .into(binding.feedDetailProfileImage)
        }
    }

    private fun loadTextProfileImg(){
        val profileUrl = UserManager.getInstance()?.profileUrl
        if (profileUrl != null) {
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .into(binding.textFeedDetailImg)
        }
    }
}