package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivitiyFeedDetailBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.Comment.CommentModalBottomSheet
import com.cjwjsw.runningman.presentation.screen.main.fragment.social.FeedDetailViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFeedDetailScreen : AppCompatActivity() {
    private lateinit var binding: ActivitiyFeedDetailBinding
    private lateinit var viewPager : ViewPager2
    private lateinit var adapter : FeedDetailViewAdapter
    private var isLiked : Boolean = false
    private val viewModel : ProfileFeedDetailViewModel by viewModels()
    private var profileImg = ""
    private var uid = ""
    private var feedTitle = ""
    private var userNumber = ""
    private var IL = false
    private var Lc = 0
    private var Image = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitiyFeedDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alert = AlertDialog.Builder(this)

        initFeed() // 피드 초기화
        initAlert(alert,uid,Image) // 알람 초기화
        initAdapter(Image!!) // 어댑터 초기화
        initViewPager() // 뷰페이저 초기화



        binding.title.text = feedTitle
        binding.likedCountText.text = "${Lc}명이 좋아합니다"
        userNumber = userData?.userNumber ?: "0"

        isLikedImg(IL)
        viewModel.getProfileImage(userData!!.idToken)
        viewModel.userImage.observe(this, Observer {
            profileImg = it.toString()
            loadProfileImg()
        })
        binding.backBtn.setOnClickListener {
            finish()
        }

        viewModel.feed_time.observe(this){
            binding.feedTime.text = it
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

        binding.feedDelBtn.setOnClickListener { // 피드 삭제
            alert.show()
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
        val modal = CommentModalBottomSheet(uid, userName, profileUrl, userNumber)

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

    private fun initAlert(alert: AlertDialog.Builder, uid: String, image: ArrayList<String>?){
        alert.setTitle("피드를 삭제하시겠습니까?")
        alert.setPositiveButton("네", DialogInterface.OnClickListener { dialog, which ->
            viewModel.deleteFeed(uid,image)
            this.finish()
        })
        alert.setNegativeButton("아니오",DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
        })
    }

    private fun initAdapter(image: ArrayList<String>){
        adapter = FeedDetailViewAdapter(image)
        adapter = image?.let { FeedDetailViewAdapter(it) }!!
    }

    private fun initFeed() {
        uid = intent.getStringExtra("UID").toString()
        Image = intent.getStringArrayListExtra("URL")!!
        feedTitle = intent.getStringExtra("title").toString()
        IL = intent.getBooleanExtra("isLiked", false) // 좋아요 여부
        Lc = intent.getIntExtra("likedCount", 0) // 좋아요 개수
    }

    private fun initViewPager(){
        viewPager = binding.feedImgViewPager
        viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.feedImgViewPager)
    }



    companion object{
        val userData = UserManager.getInstance()
        val userName = userData?.nickName
        val userProfileImg = userData?.profileUrl
    }
}