package com.cjwjsw.runningman.presentation.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import java.time.Duration

class StatisticsProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private var progress = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val progressHeight = (height * (progress / 100.0)).toInt()

        val rectF = RectF(0f, (height - progressHeight).toFloat(), width.toFloat(), height.toFloat())
        val radius = width / 2f // 둥근 테두리 반경

        // 테두리를 둥글게 그리기
        canvas.drawRoundRect(rectF, radius, radius, paint)
    }

    private fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun animateProgress(targetProgress: Int, duration: Long = 1000) {
        val animator = ValueAnimator.ofInt(0, targetProgress)
        animator.duration = duration
        animator.addUpdateListener { animation ->
            setProgress(animation.animatedValue as Int)
        }
        animator.start()
    }

    fun getProgress(): Int {
        return progress
    }
}