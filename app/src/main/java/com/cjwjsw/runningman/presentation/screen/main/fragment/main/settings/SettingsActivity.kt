package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.core.StepsSettings
import com.cjwjsw.runningman.databinding.ActivitySettingsBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.MainFragment
import com.google.android.gms.oss.licenses.OssLicensesActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backButtonClick()
        switchAlarmToggle()
        initViews()
    }

    private fun initViews() = with(binding) {
        stepMax.text = StepsSettings.steps.toString()
        calorieMax.text = StepsSettings.calories.toString()
        timeMax.text = StepsSettings.time.toString()
        distanceMax.text = StepsSettings.distance.toString()

        // 목표 걸음 수 설정 버튼 클릭 이벤트
        stepMaximumButton.setOnClickListener {
            showEditDialog(
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
            showEditDialog(
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
            showEditDialog(
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
            showEditDialog(
                title = "목표 거리 수정",
                minValue = 1,
                currentSetting = StepsSettings.distance,
                onValueSet = { newDistance ->
                    StepsSettings.distance = newDistance
                    distanceMax.text = newDistance.toString() // 텍스트 업데이트
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

    private fun showOssLicense() {
        OssLicensesMenuActivity.setActivityTitle("오픈소스 라이선스")
        startActivity(Intent(this, OssLicensesMenuActivity::class.java))
    }

    private fun showEditDialog(
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
                    Toast.makeText(this, "설정되었습니다: $inputValue", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    // 목표 걸음 수 설정 다이얼로그 호출 예시
    private fun showEditStepMaximumDialog() {
        showEditDialog(
            title = "목표 걸음 수 수정",
            minValue = 1000,
            currentSetting = StepsSettings.steps,
            onValueSet = { newSteps ->
                StepsSettings.steps = newSteps
                binding.stepMax.text = newSteps.toString()
            }
        )
    }

    // 목표 칼로리 설정 다이얼로그 호출 예시
    private fun showEditCalorieDialog() {
        showEditDialog(
            title = "목표 칼로리 수정",
            minValue = 50,
            currentSetting = StepsSettings.calories, // 칼로리 설정 값이 있다고 가정
            onValueSet = { newCalories ->
                StepsSettings.calories = newCalories
                binding.calorieMax.text = newCalories.toString() // UI에 반영
            }
        )
    }

    // 목표 시간 설정 다이얼로그 호출 예시
    private fun showEditTimeDialog() {
        showEditDialog(
            title = "목표 시간 수정",
            minValue = 10,
            currentSetting = StepsSettings.time, // 시간 설정 값이 있다고 가정 (단위: 분)
            onValueSet = { newTime ->
                StepsSettings.time = newTime
                binding.timeMax.text = newTime.toString() // UI에 반영
            }
        )
    }

    // 목표 거리 설정 다이얼로그 호출 예시
    private fun showEditDistanceDialog() {
        showEditDialog(
            title = "목표 거리 수정",
            minValue = 1,
            currentSetting = StepsSettings.distance, // 거리 설정 값이 있다고 가정 (단위: km)
            onValueSet = { newDistance ->
                StepsSettings.distance = newDistance
                binding.distanceMax.text = newDistance.toString() // UI에 반영
            }
        )
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