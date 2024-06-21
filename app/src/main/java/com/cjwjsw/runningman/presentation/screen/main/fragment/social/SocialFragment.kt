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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = ViewAdapter(emptyList(),this)
        binding.recyclerView.adapter = adapter

        viewModel.imageUrls.observe(viewLifecycleOwner) { urls ->
            adapter.updateImages(urls)
        }
        viewModel.fetchImage()


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(imageUrl: String) {
       val intent = Intent(requireContext(),SocialDetailScreen::class.java).apply {
           putExtra("URL",imageUrl)
           Log.d("onclick",imageUrl)
       }
        startActivity(intent)
    }

}