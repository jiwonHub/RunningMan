package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.databinding.ActivityAddFeedBinding

class AddFeedActivity: AppCompatActivity() {
    lateinit var binding : ActivityAddFeedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val galleryRequestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.d("AddFeedActivity","권한 부여 완료")
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.d("AddFeedActivity","권한 부여 거부")
                }
            }

        val cameraRequestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.d("AddFeedActivity","권한 부여 완료")
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.d("AddFeedActivity","권한 부여 거부")
                }
            }

        binding.addPictureBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("AddFeedAcitivity","권한 부여 됌")
                    //TODO 권한이 부여되었을 때
                }else -> {
                Log.d("AddFeedAcitivity","권한 부여 안됌")
                    if(Build.VERSION.SDK_INT >= 33 ) //버전이 33이하일때,
                galleryRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                    Manifest.permission.READ_MEDIA_IMAGES
                )else{
                galleryRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                }
            }
            }
        }

        binding.cameraBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("AddFeedAcitivity","권한 부여 됌")
                    //TODO 권한이 부여되었을 때
                }else -> {
                Log.d("AddFeedAcitivity","권한 부여 안됌")
                    cameraRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                        Manifest.permission.CAMERA
                    )
                }
            }
        }
    }
}



