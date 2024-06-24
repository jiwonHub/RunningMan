package com.cjwjsw.runningman.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.R

class StatisticsDotLine(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {
    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.blue) // 파란색
        style = Paint.Style.STROKE
        strokeWidth = 3.dpToPx()
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f) // 점선 효과
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawLine(0f, height / 2f, width.toFloat(), height / 2f, paint)
    }

    private fun Int.dpToPx(): Float {
        return this * resources.displayMetrics.density
    }
}