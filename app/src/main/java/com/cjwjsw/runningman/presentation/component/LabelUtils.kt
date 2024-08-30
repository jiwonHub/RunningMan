package com.cjwjsw.runningman.presentation.component

import android.content.Context
import android.graphics.PorterDuff
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.cjwjsw.runningman.R

object LabelUtils {

    fun setAverageLabel(
        constraintLayout: ConstraintLayout,
        imageView: ImageView,
        textView: TextView,
        customView: View,
        average: List<Int>,
        maxSteps: Int
    ) {
        val constraintSet = ConstraintSet().apply {
            clone(constraintLayout)
            val nonZeroAverages = average.filter { it > 0 }
            val averageValue = if (nonZeroAverages.isNotEmpty()) nonZeroAverages.average() else 0.0
            val height = 290 * (averageValue / maxSteps)
            val averagePx = (height + 13).toInt().dpToPx(constraintLayout.context)

            Log.d("average", average.toString())
            Log.d("average", nonZeroAverages.toString())
            Log.d("average", averageValue.toString())
            Log.d("average", height.toString())
            Log.d("average", averagePx.toString())

            // ImageView constraints
            connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, averagePx)

            // TextView constraints
            connect(textView.id, ConstraintSet.START, imageView.id, ConstraintSet.START)
            connect(textView.id, ConstraintSet.END, imageView.id, ConstraintSet.END)
            connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(textView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)

            // Custom View constraints
            connect(customView.id, ConstraintSet.START, textView.id, ConstraintSet.END)
            connect(customView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(customView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(customView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)
        }

        constraintSet.applyTo(constraintLayout)
    }

    fun set6kLabel(
        constraintLayout: ConstraintLayout,
        imageView: ImageView,
        textView: TextView,
        customView: View,
        maxSteps: Int
    ) {
        val constraintSet = ConstraintSet().apply {
            clone(constraintLayout)
            val height = 290 * (6000 / maxSteps.toFloat())
            val px6k = (height + 30).toInt().dpToPx(constraintLayout.context)

            // ImageView constraints
            connect(imageView.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            connect(imageView.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, px6k)

            // TextView constraints
            connect(textView.id, ConstraintSet.START, imageView.id, ConstraintSet.START)
            connect(textView.id, ConstraintSet.END, imageView.id, ConstraintSet.END)
            connect(textView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(textView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)

            // Custom View constraints
            connect(customView.id, ConstraintSet.START, textView.id, ConstraintSet.END)
            connect(customView.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
            connect(customView.id, ConstraintSet.TOP, imageView.id, ConstraintSet.TOP)
            connect(customView.id, ConstraintSet.BOTTOM, imageView.id, ConstraintSet.BOTTOM)
        }

        constraintSet.applyTo(constraintLayout)
    }

    fun addStepLabels(
        constraintLayout: ConstraintLayout,
        maxSteps: Int,
        stepsList: List<Int>
    ) {
        val stepInterval = if (maxSteps > 20000) 10000 else 5000

        for (i in 1..(maxSteps / stepInterval)) {
            val stepCount = i * stepInterval
            val stepLabel = createStepLabel(constraintLayout.context, stepCount)
            constraintLayout.addView(stepLabel)

            val constraintSet = ConstraintSet()
            constraintSet.clone(constraintLayout)
            val height = 290 * (stepCount / maxSteps.toFloat())
            val stepPx = (height + 30).toInt().dpToPx(constraintLayout.context)

            constraintSet.connect(stepLabel.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
            constraintSet.connect(stepLabel.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, stepPx)

            constraintSet.applyTo(constraintLayout)
        }
    }

    fun createStepLabel(context: Context, stepCount: Int): TextView {
        return TextView(context).apply {
            id = View.generateViewId()
            text = "${stepCount/1000}k"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun createImageView(context: Context): ImageView {
        return ImageView(context).apply {
            id = View.generateViewId()
            setImageResource(R.drawable.label)
            layoutParams = ConstraintLayout.LayoutParams(
                33.dpToPx(context),
                33.dpToPx(context)
            )
        }
    }

    fun createTextView(context: Context, text: String): TextView {
        return TextView(context).apply {
            id = View.generateViewId()
            this.text = text
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    fun createCustomView(context: Context): View {
        return View(context).apply {
            id = View.generateViewId()
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            layoutParams = ConstraintLayout.LayoutParams(
                0,
                3.dpToPx(context)
            )
        }
    }

    fun create6kLabel(context: Context): ImageView {
        return ImageView(context).apply {
            id = View.generateViewId()
            setColorFilter(
                ContextCompat.getColor(context, R.color.blue),
                PorterDuff.Mode.SRC_IN
            )
            setImageResource(R.drawable.label)
            layoutParams = ConstraintLayout.LayoutParams(
                33.dpToPx(context),
                33.dpToPx(context)
            )
        }
    }

    fun create6kDotLine(context: Context): View {
        return StatisticsDotLine(context).apply {
            id = View.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                0,
                3.dpToPx(context)
            )
        }
    }

    fun create6kTextView(context: Context): TextView {
        return TextView(context).apply {
            id = View.generateViewId()
            text = "6k"
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
    }
}