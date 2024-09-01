package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
    private var isLiked : Boolean = false
    private val viewModel : DetailFeedViewModel by viewModels()
    private var profileImg = ""
    private var uid = ""
    private var feedTitle = ""
    private var userNumber = ""
    private var IL = false
    private var Lc = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        uid = intent.getStringExtra("UID").toString()
        val image = intent.getStringArrayListExtra("URL")
        //profileImg = intent.getStringExtra("profileUrl").toString() //socialFragment에서 받아온 데이터들
        profileImg = userData?.profileUrl!!
        feedTitle = intent.getStringExtra("title").toString()
        IL = intent.getBooleanExtra("isLiked",false) // 좋아요 눌렀는지 아닌지
        Lc = intent.getIntExtra("likedCount",0) // 좋아요 개수


        adapter = FeedDetailViewAdapter(image)
        adapter = image?.let { FeedDetailViewAdapter(it) }!!
        viewPager = binding.feedImgViewPager
        viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.feedImgViewPager)
        binding.title.text = feedTitle
        binding.likedCountText.text = "${Lc}명이 좋아합니다"
        userNumber = userData.userNumber
        isLikedImg(IL)
        loadProfileImg()
        viewModel.getFeedUploadTime(uid)



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
            modalBottomSheet(uid.toString(), userName.toString(),userProfileImg.toString())
        }
    }


    private fun loadProfileImg(){
        Glide.with(this)
            .load(profileImg)
            .placeholder(R.drawable.sun)
            .error(R.drawable.calories)
            .into(binding.feedDetailProfileImage)
    }



    private fun modalBottomSheet(uid : String,userName : String,profileUrl : String) {
        val modal = CommentModalBottomSheet(uid,userName,profileUrl,userNumber)

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
        val userName = userData?.nickName
        val userProfileImg = userData?.profileUrl
    }
}