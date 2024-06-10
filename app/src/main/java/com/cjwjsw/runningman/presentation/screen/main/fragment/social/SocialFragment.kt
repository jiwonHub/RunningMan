package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.cjwjsw.runningman.databinding.FragmentSocialBinding

class SocialFragment : Fragment() {
    private var _binding: FragmentSocialBinding? = null
    private val viewModel : FeedViewModel by viewModels()
    private lateinit var adapter: viewAdapter
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = viewAdapter(emptyList())
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

}