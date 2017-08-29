package com.project.mt.dc.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable


/**
 * Created by mt on 8/13/17.
 */
class BadgeDrawable(context: Context) : Drawable() {

    private val mTextSize: Float
    private val mBadgePaint: Paint
    private val mTextPaint: Paint
    private val mTxtRect = Rect()

    private var mCount = ""
    private var mWillDraw = false

    init {
        //mTextSize = context.getResources().getDimension(R.dimen.badge_text_size);
        mTextSize = 24f

        mBadgePaint = Paint()
        mBadgePaint.color = Color.RED
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL

        mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.typeface = Typeface.DEFAULT_BOLD
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        if (!mWillDraw) {
            return
        }

        val bounds = bounds
        val width = bounds.right - bounds.left
        val height = bounds.bottom - bounds.top

        // Position the badge in the top-right quadrant of the icon.
        val radius = (Math.min(width, height) / 2 - 1) / 2
        val centerX = width - radius - 1f
        val centerY = radius + 1

        // Draw badge circle.
        canvas.drawCircle(centerX, centerY.toFloat(), radius.toFloat(), mBadgePaint)

        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = mTxtRect.bottom - mTxtRect.top
        val textY = centerY + textHeight / 2f
        canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: Int) {
        mCount = Integer.toString(count)

        // Only draw a badge if there are notifications.
        mWillDraw = count > 0
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) {
        // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }
}