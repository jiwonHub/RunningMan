package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.StepsSettings
import com.cjwjsw.runningman.core.UserManager
import com.cjwjsw.runningman.databinding.ActivitySettingsBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by viewModels()
    private lateinit var userId : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backButtonClick()
        switchAlarmToggle()
        initViews()
        observeData()
    }

    private fun initViews() = with(binding) {
        stepMax.text = StepsSettings.steps.toString()
        calorieMax.text = StepsSettings.calories.toString()
        timeMax.text = StepsSettings.time.toString()
        distanceMax.text = StepsSettings.distance.toString()

        userId = UserManager.getInstance()?.idToken ?: ""
        viewModel.fetchUserInfo(userId)

        // 목표 걸음 수 설정 버튼 클릭 이벤트
        stepMaximumButton.setOnClickListener {
            showNumberEditDialog(
                title = "목표 걸음 수 수정",
                minValue = 1000,
                currentSetting = StepsSettings.steps,
                onValueSet = { newSteps ->
                    StepsSettings.steps = newSteps
                    stepMax.text = newSteps.toString() // 텍스트 업데이트
                }
            )
        }

        // 목표 칼로리 설정 버튼 클릭 이벤트
        calorieMaximumButton.setOnClickListener {
            showNumberEditDialog(
                title = "목표 칼로리 수정",
                minValue = 50,
                currentSetting = StepsSettings.calories,
                onValueSet = { newCalories ->
                    StepsSettings.calories = newCalories
                    calorieMax.text = newCalories.toString() // 텍스트 업데이트
                }
            )
        }

        // 목표 시간 설정 버튼 클릭 이벤트
        timeMaximumButton.setOnClickListener {
            showNumberEditDialog(
                title = "목표 시간 수정",
                minValue = 10,
                currentSetting = StepsSettings.time,
                onValueSet = { newTime ->
                    StepsSettings.time = newTime
                    timeMax.text = newTime.toString() // 텍스트 업데이트
                }
            )
        }

        // 목표 거리 설정 버튼 클릭 이벤트
        distanceMaximumButton.setOnClickListener {
            showNumberEditDialog(
                title = "목표 거리 수정",
                minValue = 1,
                currentSetting = StepsSettings.distance,
                onValueSet = { newDistance ->
                    StepsSettings.distance = newDistance
                    distanceMax.text = newDistance.toString() // 텍스트 업데이트
                }
            )
        }

        userAgeButton.setOnClickListener {
            showNumberEditDialog(
                title = "나이 수정",
                minValue = 1,
                currentSetting = userAgeMax.text.toString().toInt(),
                onValueSet = { newAge ->
                    viewModel.updateAge(userId, newAge) // ViewModel 메서드 호출
                }
            )
        }

        userHeightButton.setOnClickListener {
            showNumberEditDialog(
                title = "키 수정",
                minValue = 50,
                currentSetting = userHeightMax.text.toString().toInt(),
                onValueSet = { newHeight ->
                    viewModel.updateHeight(userId, newHeight) // ViewModel 메서드 호출
                }
            )
        }

        userWeightButton.setOnClickListener {
            showNumberEditDialog(
                title = "몸무게 수정",
                minValue = 20,
                currentSetting = userWeightMax.text.toString().toInt(),
                onValueSet = { newWeight ->
                    viewModel.updateWeight(userId, newWeight) // ViewModel 메서드 호출
                }
            )
        }

        userGenderButton.setOnClickListener {
            showGenderEditDialog(
                currentGender = userGenderMax.text.toString(),
                onGenderSet = { newGender ->
                    viewModel.updateGender(userId, newGender) // ViewModel 메서드 호출
                }
            )
        }

        openSourceLicense.setOnClickListener {
            showOssLicense()
        }
        privacyPolicy.setOnClickListener {
            val intent = Intent(this@SettingsActivity, WebViewActivity::class.java)
            startActivity(intent)
        }
    }

    // ViewModel의 LiveData 관찰하여 UI 업데이트
    private fun observeData() {
        viewModel.userInfo.observe(this) { userInfo ->
            binding.userAgeMax.text = userInfo.age.toString()
            binding.userHeightMax.text = userInfo.height.toString()
            binding.userWeightMax.text = userInfo.weight.toString()
            binding.userGenderMax.text = userInfo.gender
        }
    }

    private fun showOssLicense() {
        OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun showNumberEditDialog(
        title: String,
        minValue: Int,
        currentSetting: Int,
        onValueSet: (Int) -> Unit
    ) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_step_maximum, null)
        val stepEditText = dialogView.findViewById<EditText>(R.id.stepMaximumEditText)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.stepMaximumDialogTitle)

        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.setContentView(dialogView)

        dialogTitle.text = title
        stepEditText.hint = "현재 설정: $currentSetting"

        confirmButton.setOnClickListener {
            val inputText = stepEditText.text.toString()
            val inputValue = inputText.toIntOrNull()

            when {
                inputValue == null -> {
                    Toast.makeText(this, "값을 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
                inputValue < minValue -> {
                    Toast.makeText(this, "$minValue 이상의 값으로 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    onValueSet(inputValue)
                    dialog.dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    // 성별 수정 다이얼로그
    private fun showGenderEditDialog(
        currentGender: String,
        onGenderSet: (String) -> Unit
    ) {
        val genderOptions = arrayOf("남성", "여성") // 성별 선택지
        val currentSelection = if (currentGender == "남성") 0 else 1

        val builder = AlertDialog.Builder(this)
        builder.setTitle("성별 수정")
        builder.setSingleChoiceItems(genderOptions, currentSelection) { dialog, which ->
            val selectedGender = genderOptions[which]
            onGenderSet(selectedGender) // 선택된 성별 업데이트
            dialog.dismiss()
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun switchAlarmToggle() {
        binding.alarmSwitch.setOnCheckedChangeListener  { _, isChecked ->
            binding.alarmSwitch.animate().setDuration(300).alpha(if (isChecked) 1.0f else 0.5f).start()
        }
    }

    private fun backButtonClick() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }
}