package com.cjwjsw.runningman.presentation.component

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.cjwjsw.runningman.R
import java.time.Duration

class StatisticsProgressBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val defaultColor = Color.WHITE
    private val highlightColor = Color.BLUE

    private val paint = Paint().apply {
        color = defaultColor
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private var progress = 0
    private var maxSteps = 1000

    private val starPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var bubbleTextView: TextView? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val progressHeight = (height * (progress / maxSteps.toFloat())).toInt()

        val rectF = RectF(0f, (height - progressHeight).toFloat(), width.toFloat(), height.toFloat())
        val radius = width / 2f // 둥근 테두리 반경

        // 테두리를 둥글게 그리기
        canvas.drawRoundRect(rectF, radius, radius, paint)
        // 별 아이콘 그리기
        val starCx = width / 2f
        val starCy = height - progressHeight.toFloat() + starCx
        val starRadius = width / 3f
        drawStar(canvas, starCx, starCy, starRadius)
    }

    private fun drawStar(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val path = Path()
        val angle = (2.0 * Math.PI / 5.0).toFloat()

        for (i in 0 until 5) {
            val x = (cx + radius * Math.cos((i * 2.0 * angle))).toFloat()
            val y = (cy + radius * Math.sin((i * 2.0 * angle))).toFloat()
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        // 회전 변환 적용
        val matrix = Matrix()
        matrix.postRotate(-18f, cx, cy) // 왼쪽으로 18도 회전
        path.transform(matrix)

        canvas.drawPath(path, starPaint)
    }

    private fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    fun setMaxSteps(maxSteps: Int) {
        this.maxSteps = maxSteps
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

    fun setProgressColor(isHighlight: Boolean) {
        paint.color = if (isHighlight) highlightColor else defaultColor
        invalidate()
    }

    fun showCustomBubble() {
        val parent = parent as? ConstraintLayout ?: return
        if (bubbleTextView == null) {
            bubbleTextView = TextView(context).apply {
                text = "$progress 걸음"
                setBackgroundResource(R.drawable.bubble_background)
                setPadding(16.dpToPx(), 8.dpToPx(), 16.dpToPx(), 8.dpToPx())
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                id = generateViewId()
            }
            parent.addView(bubbleTextView)
        } else {
            bubbleTextView?.visibility = View.VISIBLE
            bubbleTextView?.text = "$progress 걸음"
        }

        val constraintSet = ConstraintSet().apply {
            clone(parent)
            connect(bubbleTextView!!.id, ConstraintSet.BOTTOM, this@StatisticsProgressBar.id, ConstraintSet.TOP, 8.dpToPx())
            connect(bubbleTextView!!.id, ConstraintSet.START, this@StatisticsProgressBar.id, ConstraintSet.START)
            connect(bubbleTextView!!.id, ConstraintSet.END, this@StatisticsProgressBar.id, ConstraintSet.END)
        }
        constraintSet.applyTo(parent)
    }

    fun hideCustomBubble() {
        bubbleTextView?.visibility = GONE
    }


    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    interface BubbleListener {
        fun onProgressBarTouched(touchedProgressBar: StatisticsProgressBar)
    }

}