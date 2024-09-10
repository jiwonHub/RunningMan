package com.cjwjsw.runningman.presentation.screen.main.fragment.main.water.watersetting

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cjwjsw.runningman.R
import com.cjwjsw.runningman.databinding.ActivityWaterSettingBinding
import com.cjwjsw.runningman.receiver.AlarmReceiver
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaterSettingBinding
    private val viewModel: WaterSettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeData()
        setupButtons()
    }

    private fun observeData() {
        viewModel.targetWater.observe(this) { targetWater ->
            binding.waterTarget.text = targetWater.toString() // 목표 물 섭취량 텍스트 업데이트
        }

        viewModel.drinkingWater.observe(this) { drinkingWater ->
            binding.waterDrinking.text = drinkingWater.toString() // 한 컵 용량 텍스트 업데이트
        }
    }

    private fun setupButtons() = with(binding) {
        // 뒤로가기 버튼 클릭 이벤트
        backButton.setOnClickListener {
            finish()
        }

        // 목표 물 섭취량 설정 버튼 클릭 이벤트
        waterTargetButton.setOnClickListener {
            showNumberEditDialog(
                title = "목표 물 섭취량 수정",
                minValue = 100,
                currentSetting = viewModel.targetWater.value ?: 0,
                onValueSet = { newTarget ->
                    viewModel.updateTargetWater(newTarget) // ViewModel을 통해 값 업데이트
                    setResult(RESULT_OK)
                }
            )
        }

        // 한 컵 용량 설정 버튼 클릭 이벤트
        waterDrinkingButton.setOnClickListener {
            showNumberEditDialog(
                title = "한 컵 용량 수정",
                minValue = 50,
                currentSetting = viewModel.drinkingWater.value ?: 0,
                onValueSet = { newDrinking ->
                    viewModel.updateDrinkingWater(newDrinking) // ViewModel을 통해 값 업데이트
                    setResult(RESULT_OK)
                }
            )
        }

        // 알림 설정 버튼 클릭 이벤트
        waterAlarmButton.setOnClickListener {
            // 알림 설정 다이얼로그 호출
            showAlarmEditDialog(
                onAlarmSet = { newAlarmSetting ->
                    updateAlarmSetting(newAlarmSetting) // 새 알림 설정을 업데이트
                }
            )
        }
    }

    private fun updateAlarmSetting(newAlarmSetting: String) {
        val intervalMillis = when (newAlarmSetting) {
            "30분마다" -> 30 * 60 * 1000L
            "1시간마다" -> 60 * 60 * 1000L
            "2시간마다" -> 2 * 60 * 60 * 1000L
            else -> 0L // 알림 끄기
        }

        if (intervalMillis > 0) {
            setAlarm(intervalMillis)
        } else {
            cancelAlarm()
        }
    }

    private fun setAlarm(intervalMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("intervalMillis", intervalMillis)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // 플래그 추가
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + intervalMillis,
            intervalMillis,
            pendingIntent
        )

        // 알림 설정에 대한 로그 출력
        Log.d("AlarmSetting", "알림이 설정되었습니다: 주기 = ${intervalMillis / 60000}분마다")
    }

    private fun cancelAlarm() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // 플래그 추가
        )

        alarmManager.cancel(pendingIntent)
        Log.d("AlarmSetting", "알림이 해제되었습니다.")
    }

    private fun showNumberEditDialog(
        title: String,
        minValue: Int,
        currentSetting: Int,
        onValueSet: (Int) -> Unit
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_step_maximum, null)
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

    // 알림 설정 다이얼로그
    private fun showAlarmEditDialog(
        onAlarmSet: (String) -> Unit
    ) {
        val alarmOptions = arrayOf("알림 끄기", "30분마다", "1시간마다", "2시간마다")

        // 선택된 항목에 대한 초기 선택 설정을 하지 않아도 됨
        val builder = AlertDialog.Builder(this)
        builder.setTitle("알림 설정")
        builder.setItems(alarmOptions) { dialog, which ->
            val selectedAlarm = alarmOptions[which]
            onAlarmSet(selectedAlarm) // 선택된 알림 설정 업데이트
            dialog.dismiss()
        }
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

}