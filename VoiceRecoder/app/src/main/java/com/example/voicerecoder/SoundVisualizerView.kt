package com.example.voicerecoder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(context: Context, attributeSet: AttributeSet? = null): View(context, attributeSet) {

    var onRequestCurrentAmplitude:(()->Int)? = null

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.purple_500)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }
    private var drawingWidth : Int = 0
    private var drawingHeight : Int = 0
    private var drawingAmplitudes : List<Int> = emptyList()
    private var isReplaying : Boolean = false
    private var replayingPosition : Int = 0

    private val visualizeRepeatAction : Runnable = object : Runnable{
        override fun run() {
            if(!isReplaying){
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            }else{
                replayingPosition++
            }
            invalidate() //갱신
            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas ?: return

        val centerY = drawingHeight / 2f
        var offsetX = drawingWidth.toFloat() //오른쪽 부터 나오는 것으로 설계를 하기 때문에 폭의 길이가 곧 맨 끝화면의 위치

        drawingAmplitudes
            .let {
                if(isReplaying){
                    it.takeLast(replayingPosition)
                }else{
                    it
                }
            }
            .forEach {
            val lineLength = it / MAX_AMPLITUBE * drawingHeight * 0.8F
            offsetX -= LINE_SPACE //오른쪽에서 왼쪽으로 가는 것이기 때문에 라인 스페이스 만큼 빼면서 옆으로 찍으면 된다.
            if(offsetX < 0) return@forEach //화면을 넘어가는 것이 있으면

            canvas.drawLine(
                offsetX,
                centerY - lineLength / 2F,
                offsetX,
                centerY + lineLength / 2F,
                amplitudePaint
            )
        }

    }

    fun startVisualizing(isReplaying : Boolean){
        this.isReplaying = isReplaying
        handler?.post(visualizeRepeatAction)
    }

    fun stopVisualizing(){
        replayingPosition = 0
        handler?.removeCallbacks(visualizeRepeatAction)
    }

    fun clearVisualization(){
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object{
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUBE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}