package com.example.pomodoro

import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val remainMinuteTextView by lazy {
        findViewById<TextView>(R.id.remainMinutesTextView)
    }

    private val remainSecondTextView by lazy {
        findViewById<TextView>(R.id.remainSecondTextView)
    }

    private val seekBar : SeekBar by lazy {
        findViewById(R.id.seekBar)
    }

    private var currentCountDownTimer : CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()

    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindView()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun bindView(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //이벤트가 발샹한것
                if (fromUser)
                    updateRemainTime(progress * 60 * 1000L)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                stopCountDown()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar ?: return
                if(seekBar.progress == 0){
                    stopCountDown()
                }else {
                    startCountDown()
                }
            }

        })
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                updateRemainTime(millisUntilFinished)
                updateSeekbar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()
            }

        }


    private fun startCountDown(){
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        currentCountDownTimer?.start()
        tickingSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, -1, 1F)
        }
    }

    private fun completeCountDown(){
        updateRemainTime(0)
        updateSeekbar(0)
        soundPool.autoPause()
        bellSoundId?.let {
            soundPool.play(it, 1F, 1F, 0, 0, 1F)
        }
    }


    private fun updateRemainTime(remainMillis: Long){
        val remainSecondes = remainMillis / 1000
        seekBar.progress = (remainSecondes / 60).toInt()
        remainMinuteTextView.text = "%02d'".format(remainSecondes / 60)
        remainSecondTextView.text = "%02d".format(remainSecondes % 60)
    }

    private fun updateSeekbar(remainMillis: Long){
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
    private fun initSounds(){
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell,1)
    }

    private fun stopCountDown(){
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null
        soundPool.autoPause()
    }
}