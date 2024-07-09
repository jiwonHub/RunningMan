package com.cjwjsw.runningman.presentation.screen.main.fragment.social

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
    private var imageArr = mutableListOf<MutableList<String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = ViewAdapter(mutableListOf(),this)
        binding.recyclerView.adapter = adapter
        viewModel.feedArr.observe(viewLifecycleOwner) { urls ->
            for(i in 0 ..< urls.size ){
                imageArr.add(urls[i].imageUrls.toMutableList())
            }
            Log.d("SocialFragment",imageArr.size.toString())
            adapter.updateImages(imageArr)
        }

        viewModel.fetchFeedData()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(imageUrl: MutableList<String>) {
        val feedInfo : ArrayList<String> = arrayListOf()
        feedInfo.addAll(imageUrl)
        Log.d("SocialFragment3",imageUrl.toString())
       val intent = Intent(requireContext(),FeedDetailScreen::class.java).apply {
           putStringArrayListExtra("URL",feedInfo)
           Log.d("onclick", feedInfo.toString())
       }
        startActivity(intent)
    }

}