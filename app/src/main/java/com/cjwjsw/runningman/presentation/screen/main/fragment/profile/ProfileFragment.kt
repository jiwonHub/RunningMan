package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ProfileFragment @Inject constructor() : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter : ProfileViewAdapter
    private lateinit var recyclerView: RecyclerView
    private val viewModel: ProfileViewModel by viewModels()
    val arr : MutableList<String> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        recyclerView = binding.feeddRecyclerView
        binding.addFeedBtn.setOnClickListener {
            val intent = Intent(super.getActivity(), AddFeedActivity::class.java)
            startActivity(intent)
        }
        loadProfileImg()
        viewModel.getUserFeed()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = GridLayoutManager(context, 3)
        adapter = ProfileViewAdapter(emptyList())
        recyclerView.adapter = adapter

        viewModel.feedArr.observe(viewLifecycleOwner, Observer { feed ->
            feed.let {
                for(i in 0..<feed?.size!!){
                    arr.add(feed[i].imageUrls[0])
                }
                adapter = ProfileViewAdapter(arr)
                Log.d("profilefragment",feed.toString())
                recyclerView.adapter = adapter
            }
        })
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
}