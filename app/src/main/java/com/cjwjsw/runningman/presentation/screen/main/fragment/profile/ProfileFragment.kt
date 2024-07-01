package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
    //lateinit var adapter : ProfileViewAdapter
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.addFeedBtn.setOnClickListener {
            val intent = Intent(super.getActivity(), AddFeedActivity::class.java)
            startActivity(intent)
        }
       // initAdapter()
        viewModel.getUserFeed()
        loadProfileImg()
        return binding.root
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

//    private fun initAdapter(){
//        adapter = ProfileViewAdapter()
//        binding.feeddRecyclerView.adapter = adapter
//        binding.feeddRecyclerView.layoutManager = GridLayoutManager(this)
//    }
}