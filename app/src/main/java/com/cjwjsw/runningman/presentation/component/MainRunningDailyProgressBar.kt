package com.cjwjsw.runningman.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.R

class MainRunningDailyProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var progress: Int = 0
    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val gray26 = ContextCompat.getColor(context, R.color.gray26)
    private val blue = ContextCompat.getColor(context, R.color.blue)

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        paint.color = gray26
        canvas.drawArc(
            paint.strokeWidth,
            paint.strokeWidth,
            width - paint.strokeWidth,
            height - paint.strokeWidth,
            0f,
            360f,
            false,
            paint
        )

        paint.color = blue
        canvas.drawArc(
            paint.strokeWidth,
            paint.strokeWidth,
            width - paint.strokeWidth,
            height - paint.strokeWidth,
            90f,
            (progress / 100f) * 360f,
            false,
            paint
        )
    }

}