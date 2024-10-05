package com.cjwjsw.runningman.presentation.screen.main.fragment.profile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.cjwjsw.runningman.databinding.ActivityAddFeedBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class AddFeedActivity : AppCompatActivity() {
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    lateinit var binding: ActivityAddFeedBinding
    private lateinit var viewPager: ViewPager2
    lateinit var adapter: ViewpageAdapter

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.previewImage

        initPreviewImage()
        viewModel.photoArr.observe(this, Observer { uriList ->
            Log.d("AddFeedActivity", uriList.toString())
            adapter = ViewpageAdapter(uriList)
            viewPager.adapter = adapter
            binding.indicator.setViewPager(binding.previewImage)
        })


        binding.addImageBtn.setOnClickListener { // 피드 등록 요청 버튼
            if (viewModel.photoArr.value.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "1개 이상의 이미지를 등록해주세요", Toast.LENGTH_SHORT).show();
            } else if (binding.titleEditText.text.isNullOrEmpty()) { // 제목이 입력되어있지 않다면
                Toast.makeText(applicationContext, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
            } else if (binding.contentsEditText.text.isNullOrEmpty()) { // 피드 내용글을 작성하지 않았다면
                Toast.makeText(applicationContext, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
            } else {
                val title = binding.titleEditText.text
                val contents = binding.contentsEditText.text
                viewModel.uploadPost(title.toString(), contents.toString())
            }

        }

        lifecycleScope.launch {
            viewModel.isPosted.collect {
                when (it) {
                    PostedState.Failure -> {
                        binding.loadingBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "피드 업데이트에 실패했습니다", Toast.LENGTH_SHORT)
                            .show();
                    }

                    PostedState.Loading -> {
                        binding.loadingBar.show()
                        Toast.makeText(applicationContext, "피드 업데이트 중입니다", Toast.LENGTH_SHORT)
                            .show();
                    }

                    PostedState.Success -> {
                        binding.loadingBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "피드 업데이트에 성공했습니다", Toast.LENGTH_SHORT)
                            .show();
                        finish()
                    }

                    PostedState.beforePosted -> return@collect
                }
            }
        }


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

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.setImageFile(Uri.fromFile(photoFile))
        }
    private val imageLoadLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
            uriList.forEach {
                val file = uriToFile(it)
                viewModel.setImageFile(Uri.fromFile(file))
            }
        }

    private val galleryRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("AddFeedActivity", "권한 부여 완료")
                loadImage()
            } else {
                Log.d("AddFeedActivity", "권한 부여 거부")
            }
        }

    private val cameraRequestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("AddFeedActivity", "권한 부여 완료")
                openCamera()
            } else {
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

    private fun loadImage() {
        imageLoadLauncher.launch("image/*")
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        takePictureLauncher.launch(cameraIntent)
    }

    private fun uriToFile(uri: Uri): File? {
        val file = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "temp_image_${System.currentTimeMillis()}.jpg"
        )
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        return file
    }

    private fun createImageFile(): File { //카메라로 찍은 사진 파일명 생성 메서드
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun initPreviewImage() {
        adapter = ViewpageAdapter(emptyList())
        viewPager.adapter = adapter
        binding.indicator.setViewPager(binding.previewImage)
    }

}


