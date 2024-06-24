package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.databinding.ActivityAddFeedBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddFeedActivity: AppCompatActivity()  {
    //TODO 카메라 권한 받아왔고 에뮬말고 기기로 카메라 촬영 및 업로드 하는거 구현하기
    private val viewModel: ProfileViewModel by viewModels()
    lateinit var binding: ActivityAddFeedBinding
    private val imageLoadLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            updateImages(uriList)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addPictureBtn.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    Log.d("AddFeedAcitivity", "권한 부여 됌")
                    loadImage()
                }
                shouldShowRequestPermissionRationale( //사용자가 권한 거부했을 때 권한이 왜 필요한지 다이얼로그 뜸
                    Manifest.permission.READ_MEDIA_IMAGES
                ) -> {
                    showGalleryPermissionInfoDialog()
                }
                else -> {
                    Log.d("AddFeedAcitivity", "권한 부여 안됌")
                    if (Build.VERSION.SDK_INT >= 33) //버전이 33이하일때,
                        galleryRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) else {
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
                    Log.d("AddFeedAcitivity", "권한 부여 됌")
                    //TODO 권한이 부여되었을 때
                    openCamera()
                }
                shouldShowRequestPermissionRationale( //사용자가 권한 거부했을 때 권한이 왜 필요한지 다이얼로그 뜸
                    Manifest.permission.CAMERA
                ) -> {
                    showCameraPermissionInfoDialog()
                }
                else -> {
                    Log.d("AddFeedAcitivity", "권한 부여 안됌")
                    cameraRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                        Manifest.permission.CAMERA
                    )
                }
            }
        }
    }

    private val galleryRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d("AddFeedActivity", "권한 부여 완료")
                loadImage()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.

                Log.d("AddFeedActivity", "권한 부여 거부")
            }
        }

    private val cameraRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d("AddFeedActivity", "권한 부여 완료")
                openCamera()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d("AddFeedActivity", "권한 부여 거부")
                showCameraPermissionInfoDialog()
            }
        }

    private fun showGalleryPermissionInfoDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("이미지를 가져오기 위해서, 갤러리 권한이 필요합니다.")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ ->
                galleryRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        }.show()
    }

    private fun showCameraPermissionInfoDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("사진을 찍기 위해선 카메라 권한이 필요합니다.")
            setNegativeButton("취소", null)
            setPositiveButton("동의") { _, _ ->
                cameraRequestPermissionLauncher.launch( //launch 매개변수에 해당하는 권한 요청하는 알림 뜸.
                    Manifest.permission.CAMERA
                )
            }
        }.show()
    }

    private fun updateImages(uriList: List<Uri>) {
        Log.d("image","$uriList")
        viewModel.upLoadImage(uriList)
    }

    private fun loadImage() {
        imageLoadLauncher.launch("image/*")
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(cameraIntent)
    }
}


