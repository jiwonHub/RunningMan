package com.cjwjsw.runningman.presentation.screen.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityMainBinding
import com.cjwjsw.runningman.presentation.screen.feed.feedScreen
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.MainFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.map.MapFragment
import com.cjwjsw.runningman.presentation.screen.main.fragment.profile.ProfileFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(MainFragment())
        }

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(MainFragment())
                    true
                }
                R.id.navigation_map -> {
                    loadFragment(MapFragment())
                    true
                }
                R.id.navigation_social -> {
                    val intent : Intent =  Intent(this,feedScreen::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

}