package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.FragmentProfileBinding
import com.cjwjsw.runningman.domain.model.FeedModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment @Inject constructor() : Fragment(),ProfileViewAdapter.OnItemClickListener {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter : ProfileViewAdapter
    private val viewModel: ProfileViewModel by viewModels()
    private var arr = mutableListOf<FeedModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.feeddRecyclerView.layoutManager = GridLayoutManager(context, 3)
        binding.addFeedBtn.setOnClickListener {
            val intent = Intent(super.getActivity(), AddFeedActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfileImg() // 사용자 프로필 띄우기

        adapter = ProfileViewAdapter(mutableListOf(),this)
        binding.feeddRecyclerView.adapter= adapter

        viewModel.feedArr.observe(viewLifecycleOwner) { feed ->
            feed.let {
                for(i in 0..<feed?.size!!){
                    arr.add(feed[i])
                }
                Log.d("profilefragment",feed.toString())
            }
            adapter.updateImages(arr)
        }
        viewModel.getUserFeed()
    }

    private fun loadProfileImg(){
        val profileUrl = UserManager.getInstance()?.profileUrl
        if (profileUrl != null) {
            Glide.with(this)
                .load(profileUrl)
                .placeholder(R.drawable.sun)
                .error(R.drawable.calories)
                .into(binding.profileImg)
        }
    }


    override fun onItemClick(
        imageUrl: MutableList<String>,
        feedUid: MutableList<Char>,
        profileURL: String,
        title: String,
        content: String,
        likedCount: Int,
        isLiked: Boolean,
    ) {
        val feedInfo : ArrayList<String> = arrayListOf()
        Log.d("ProfileFragment","피드 UID : ${feedUid}")
        val uid = viewModel.charToString(feedUid)
        feedInfo.addAll(imageUrl)

        val intent = Intent(requireContext(), ProfileFeedDetailScreen::class.java).apply {
            putStringArrayListExtra("URL", feedInfo)
            putExtra("UID", uid)
            putExtra("profileUrl", profileURL)
            putExtra("title", title)
            putExtra("content", content)
            putExtra("likedCount", likedCount)
            putExtra("isLiked", isLiked)
        }
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}