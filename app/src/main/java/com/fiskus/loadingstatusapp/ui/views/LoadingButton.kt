package com.fiskus.loadingstatusapp.ui.views

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.fiskus.loadingstatusapp.R

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //button constants
    companion object {
        const val TEXT_SIZE = 70f
        const val CIRCLE_SIZE = 100f
        const val TEXT_CIRCLE_MARGIN = 10f
        const val ANIMATION_DURATION = 2000L
    }

    //button size
    private var widthSize = 0
    private var heightSize = 0

    //paint
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TEXT_SIZE
        typeface = Typeface.create("",Typeface.BOLD)
    }
    //animation progress- values are between 0.0 and 1.0
    private var animationProgressPercentage = 0f
    //download animator
    private val downloadAnimator = ValueAnimator.ofFloat(0f,1f)

    //downloading flag
    private var isDownloading = false

    //on size change save width and height
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthSize = w
        heightSize = h
    }

    //on draw
    //draw the color text and animation
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //draw button color
        canvas.drawColor(context.getColor(R.color.colorPrimary))
        //get correct text
        val text = getText()
        val textBounds = calcTextBounds(text)
        //draw loading according to the progress
        drawLoadingProgress(canvas, textBounds)
        //draw text
        drawText(canvas, text, textBounds)
    }

    //on user click start animation and disable buttons clicks
    override fun performClick(): Boolean {
        startLoadingAnimation()
        isClickable = false
        return super.performClick()
    }

    //start loading animation
    private fun startLoadingAnimation() {
        //set downloading flag to true
        isDownloading = true
        //set animation to repeat infinitely
        downloadAnimator.duration = ANIMATION_DURATION
        downloadAnimator.repeatCount = ValueAnimator.INFINITE
        downloadAnimator.repeatMode = ValueAnimator.RESTART
        //start animation
        downloadAnimator.start()
        //listen to updates to update rect and arc
        downloadAnimator.addUpdateListener { animation->
            animationProgressPercentage = animation.animatedValue as Float
            //redraw button
            invalidate()
        }
    }

    //stop animation- will be called by the activity when needed to stop
    fun stopLoadingAnimation() {
        //stop animation
        downloadAnimator.removeAllListeners()
        downloadAnimator.cancel()
        //reset progress
        animationProgressPercentage = 0f
        //set downloading flag to false
        isDownloading = false
        //redraw button
        invalidate()
        //change button to click able
        isClickable = true
    }

    //draw loading progress- rect and arc
    private fun drawLoadingProgress(canvas: Canvas, textBounds:Rect) {
        //draw loading rect
        paint.color = context.getColor(R.color.colorPrimaryDark)
        canvas.drawRect(Rect(0,0,(widthSize * animationProgressPercentage).toInt(),heightSize),paint)
        //draw loading arc
        paint.color =  context.getColor(R.color.colorAccent)
        canvas.drawArc(calcArcRectF(textBounds),0f,360f * animationProgressPercentage,true, paint)
    }

    //draw button text
    private fun drawText(canvas: Canvas,text:String, textBounds:Rect) {
        //set color and text
        paint.color = Color.WHITE
        //draw text
        canvas.drawText(text,calcTextPositionX(),calcTextPositionY(textBounds),paint)
    }

    //calculate text bounds
    private fun calcTextBounds(text:String): Rect {
        val textBounds = Rect()
        paint.getTextBounds(text,0, text.length, textBounds)
        return textBounds
    }

    //get button text
    private fun getText() = context.getString(if (!isDownloading) R.string.btn_download else R.string.btn_loading)

    //calc text x center position
    private fun calcTextPositionX() = (widthSize/2).toFloat()

    //calc text y bottom position
    private fun calcTextPositionY(textBounds:Rect) = (heightSize/2 + (textBounds.top*-1/2)).toFloat()

    //calc arc position and size RectF
    private fun calcArcRectF(textBounds:Rect):RectF{
        //arc x position- to the right of the text with margin
        val arcX = calcTextPositionX()+ textBounds.right/2 + TEXT_CIRCLE_MARGIN
        //arc y position- center in button
        val arcY = (heightSize/2).toFloat() - CIRCLE_SIZE/2
        //return arc rect position and size
        return RectF(arcX,arcY,arcX + CIRCLE_SIZE,arcY + CIRCLE_SIZE)
    }
}