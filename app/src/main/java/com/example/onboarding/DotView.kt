package com.example.onboarding

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.pointer.memo.ViewOnChangeDelegate


class DotView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var count: Int by ViewOnChangeDelegate(1, ::onAttributesChange)
    var radius: Float by ViewOnChangeDelegate(20f, ::onAttributesChange)
    var dotMargin: Int by ViewOnChangeDelegate(50, ::onAttributesChange)

    private var dotsOffset: Float = 0f
    private val textRect = Rect()

    private val dotPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
    }

    private val textPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.colorAccent)
        textSize = 55f
    }

    init {
        setPadding(30, 30, 30, 30)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = getDotsPosition(count - 1)
        val minHeight = paddingTop + radius * 2 + paddingBottom

        textPaint.getTextBounds("Back", 0, 4, textRect)

        setMeasuredDimension(
            measureDimension(minWidth.toInt(), widthMeasureSpec),
            measureDimension(minHeight.toInt(), heightMeasureSpec)
        )
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut")
        }
        return result.also { Log.d("ChartView", "Measure: $result") }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        onAttributesChange()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.apply {

            (1..count).forEach { index ->
                drawCircle(dotsOffset + getDotsPosition(index), height / 2f, radius, dotPaint)
            }

            drawText("Back", paddingStart.toFloat(), height.toFloat() - paddingBottom  + radius / 2,  textPaint)
            drawText("Next", width - paddingEnd.toFloat(), height.toFloat() - paddingBottom,  textPaint.apply { textAlign = Paint.Align.RIGHT })
//            drawTextRun()
        }
    }

    private fun onAttributesChange() {
        dotsOffset = getOffset()
        invalidate()
    }

    private fun getDotsPosition(position: Int): Float {
        return (position - 1) * (radius * 2 + dotMargin)
    }

    private fun getOffset(): Float = (width - getDotsPosition(count)) / 2f
}