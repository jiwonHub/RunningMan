package com.cjwjsw.runningman.presentation.screen.main.fragment.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.FragmentMainBinding
import com.cjwjsw.runningman.databinding.FragmentMapBinding
import com.cjwjsw.runningman.databinding.FragmentProfileBinding
import com.cjwjsw.runningman.databinding.FragmentSocialBinding

class SocialFragment : Fragment() {
    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }
}