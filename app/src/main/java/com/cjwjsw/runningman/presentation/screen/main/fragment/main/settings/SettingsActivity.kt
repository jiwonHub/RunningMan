package com.cjwjsw.runningman.presentation.screen.main.fragment.main.settings

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
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
    private val step = StepsSettings.steps
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        backButtonClick()
        switchAlarmToggle()
        initViews()
    }

    private fun initViews() = with(binding) {
        stepMax.text = step.toString()
        stepMaximumButton.setOnClickListener {
            showEditStepMaxiMumDialog()
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

    private fun showEditStepMaxiMumDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_step_maximum, null)
        val stepEditText = dialogView.findViewById<EditText>(R.id.stepMaximumEditText)
        val confirmButton = dialogView.findViewById<Button>(R.id.confirm_button)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancel_button)

        val dialog = Dialog(this, R.style.CustomDialog)
        dialog.setContentView(dialogView)


        confirmButton.setOnClickListener {
            val inputText = stepEditText.text.toString()
            val stepsValue = inputText.toIntOrNull()

            // 입력값이 비어있거나 숫자가 아닌 경우 처리
            if (stepsValue == null) {
                Toast.makeText(this, "값을 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
            // 값이 1000 이하인 경우 처리
            else if (stepsValue < 1000) {
                Toast.makeText(this, "1000 이상의 값으로 입력해주세요!", Toast.LENGTH_SHORT).show()
            }
            // 올바른 값인 경우 StepsSettings에 설정
            else {
                StepsSettings.steps = stepsValue
                binding.stepMax.text = stepsValue.toString() // 설정된 값을 텍스트에 반영
                Toast.makeText(this, "목표 걸음 수가 설정되었습니다: $stepsValue", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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