package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import ViewAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cjwjsw.runningman.databinding.FragmentSocialBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialFragment : Fragment(),ViewAdapter.OnItemClickListener {
    private var _binding: FragmentSocialBinding? = null
    private val viewModel : FeedViewModel by viewModels()
    private lateinit var adapter: ViewAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ViewAdapter(this)
        binding.recyclerView.adapter = adapter

        viewModel.fetchFeedData()

        viewModel.feedArr.observe(viewLifecycleOwner) { feedList ->
            Log.d("SocialFragment", feedList.toString())

            if (feedList == null) {
                Log.d("SocialFragment", "피드 정보가 없습니다")
            } else {
                // 어뎁터에 데이터 업데이트
                adapter.submitList(feedList.toMutableList())
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(
        imageUrl: MutableList<String>,
        feedUid: MutableList<Char>,
        profileURL: String,
        title: String,
        content: String,
        likedCount : Int,
        isLiked : Boolean
    ) {
        val feedInfo : ArrayList<String> = arrayListOf()
        Log.d("SocialFragment","피드 UID : ${feedUid}")
        val uid = viewModel.charToString(feedUid)
        feedInfo.addAll(imageUrl)
        val intent = Intent(requireContext(),FeedDetailScreen::class.java).apply {
            putStringArrayListExtra("URL",feedInfo)
            putExtra("UID",uid)
            putExtra("profileUrl",profileURL)
            putExtra("title",title)
            putExtra("content",content)
            putExtra("likedCount",likedCount)
            putExtra("isLiked",isLiked)

            Log.d("onclick", uid)
            Log.d("onclick", likedCount.toString())
        }
        startActivity(intent)
    }
}