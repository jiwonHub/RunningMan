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
import com.cjwjsw.runningman.databinding.ActivitySettingsBinding
import com.cjwjsw.runningman.presentation.screen.main.fragment.main.MainFragment
import com.google.android.gms.oss.licenses.OssLicensesActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {
    private lateinit var mainFragment: MainFragment
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as MainFragment
        backButtonClick()
        switchAlarmToggle()
        binding.stepMaximumButton.setOnClickListener {
            showEditStepMaxiMumDialog()
        }
        binding.openSourceLicense.setOnClickListener {
            showOssLicense()
        }
        binding.privacyPolicy.setOnClickListener {
            val intent = Intent(this, WebViewActivity::class.java)
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
            val number = stepEditText.text.toString()
            binding.stepMax.text = number
//            mainFragment.updateMaxSteps(number.toInt())
            dialog.dismiss()
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