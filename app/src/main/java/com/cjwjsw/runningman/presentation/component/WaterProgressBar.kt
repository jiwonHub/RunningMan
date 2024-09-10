package com.cjwjsw.runningman.presentation.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.R

class WaterProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 0
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 40f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val backgroundColor = ContextCompat.getColor(context, R.color.gray26) // 배경 색상
    private val progressColor = ContextCompat.getColor(context, R.color.blue) // 진행 색상

    // 프로그레스 설정 메서드
    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate() // 뷰를 다시 그리도록 요청
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val strokeOffset = paint.strokeWidth / 2

        // 배경 원형 그리기
        paint.color = backgroundColor
        canvas.drawArc(
            strokeOffset,
            strokeOffset,
            width - strokeOffset,
            height - strokeOffset,
            0f,
            360f,
            false,
            paint
        )

        // 진행 원형 그리기
        paint.color = progressColor
        canvas.drawArc(
            strokeOffset,
            strokeOffset,
            width - strokeOffset,
            height - strokeOffset,
            90f, // 6시 방향에서 시작
            (progress / 100f) * 360f, // 진행 비율에 맞게 아크를 그림
            false,
            paint
        )
    }
}
