package com.cjwjsw.runningman.presentation.screen.feed

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.cjwjsw.runningman.databinding.ActivityFeedMainBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.social.FeedViewModel
import com.cjwjsw.runningman.presentation.screen.main.fragment.social.viewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class feedScreen: AppCompatActivity() {
    private lateinit var binding : ActivityFeedMainBinding
    private val viewModel : FeedViewModel by viewModels()
    private lateinit var adapter: viewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = viewAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        viewModel.imageUrls.observe(this) { urls ->
            adapter.updateImages(urls)
        }

        viewModel.fetchImage()
    }
}
