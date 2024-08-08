package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private var isLiked : Boolean = false
    private val viewModel : DetailFeedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uid = intent.getStringExtra("UID")
        val image = intent.getStringArrayListExtra("URL")
        val profileImg = intent.getStringExtra("profileUrl") //socialFragment에서 받아온 데이터들
        val feedTitle = intent.getStringExtra("title")
        val IL = intent.getBooleanExtra("isLiked",false) // 좋아요 눌렀는지 아닌지
        val Lc = intent.getIntExtra("likedCount",0) // 좋아요 개수


        Log.d("FeedDetailScreen",uid.toString())

        adapter = FeedDetailViewAdapter(image)
        adapter = image?.let { FeedDetailViewAdapter(it) }!!
        viewPager = binding.feedImgViewPager
        viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.feedImgViewPager)
        binding.title.text = feedTitle
        binding.likedCountText.text = "${Lc}명이 좋아합니다"
        isLikedImg(IL)
        //viewModel.getLikedCount(uid.toString())
        Glide.with(this)
            .load(profileImg)
            .fitCenter()
            .into(binding.feedDetailProfileImage)

        binding.backBtn.setOnClickListener {
            finish()
        }

        viewModel.isLiked.observe(this){
            isLikedImg(it)
            isLiked = it
        }

        viewModel.likeCount.observe(this){ text ->
             binding.likedCountText.text = "${text}명이 좋아합니다"
        }

        binding.likeBtn.setOnClickListener {
            when(isLiked){
                false -> {
                    viewModel.getLikedCount(uid.toString())
                }
                true ->{
                    viewModel.getLikedCount(uid.toString())
                }
            }
        }

        binding.commentBtn.setOnClickListener {
            modalBottomSheet()
        }

        //loadProfileImg()
//        loadTextProfileImg()
        //viewModel.fetchCommentData(uid.toString())
//        recyclerView = binding.feedDetailRecycerView
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        commentAdapter = FeedDetailCommentAdapter(emptyList())
//        recyclerView.adapter = commentAdapter


//        viewModel.commentArr.observe(this){ arr ->
//            //TODO 댓글 RecycerView 텍스트 입력 정의 이곳에
//            Log.d("FeedDetailScreen","Livedata 댓글 : ${userName.toString()}")
//            commentAdapter = arr?.let { FeedDetailCommentAdapter(arr) }!!
//            recyclerView.adapter = commentAdapter
//        }


//        binding.commentUploadBtn.setOnClickListener {
//            comment = binding.editText.text.toString()
//            viewModel.uploadComment(comment,uid.toString(),userName.toString(),profileUrl.toString())
//        }
    }


//    private fun loadProfileImg(){
//        if (profileUrl != null) {
//            Glide.with(this)
//                .load()
//                .placeholder(R.drawable.sun)
//                .error(R.drawable.calories)
//                .into(binding.feedDetailProfileImage)
//        }
//    }

//    private fun loadTextProfileImg(){
//        val profileUrl = UserManager.getInstance()?.profileUrl
//        if (profileUrl != null) {
//            Glide.with(this)
//                .load(profileUrl)
//                .placeholder(R.drawable.sun)
//                .error(R.drawable.calories)
//                .into(binding.textFeedDetailImg)
//        }
//    }

//    private fun feedDataFetch(profileImg: String, feedTitle: CharSequence){
//        Glide.with(this)
//            .load(profileImg)
//            .placeholder(R.drawable.sun)
//            .error(R.drawable.calories)
//            .into(binding.feedDetailProfileImage)
//        binding.title.text = feedTitle
//    }

    private fun modalBottomSheet() {
        val modal = CommentModalBottomSheet()

        modal.show(supportFragmentManager, CommentModalBottomSheet.TAG)
    }
    

    private fun isLikedImg(liked : Boolean){
        if(!liked){
            binding.likeBtn.setImageResource(R.drawable.empty_like_btn)
            isLiked = false
        }else{
            binding.likeBtn.setImageResource(R.drawable.like_icon)
            isLiked = true
        }
    }
    companion object{
        val userData = UserManager.getInstance()
    }
}