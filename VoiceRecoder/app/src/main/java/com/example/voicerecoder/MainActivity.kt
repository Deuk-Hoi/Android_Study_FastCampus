package com.example.voicerecoder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private val visualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.soundVisualizer)
    }

    private val recordTimeView : CountUpView by lazy {
        findViewById(R.id.recodeTimeText)
    }

    private val recodeButton : RecordButton by lazy {
        findViewById(R.id.recodeButton)
    }
    private val requiredPermisson = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private var state = State.BEFORE_RECORDING
        set(value) {
            field = value
            resetButton.isEnabled = (value == State.AFTER_RECORDING) || (value == State.ON_PLAYING)
            recodeButton.uptateIconWithState(value)
        }
    private var recoder :MediaRecorder? = null
    private var player :MediaPlayer? = null

    private val recordingFilePath : String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private val resetButton :Button by lazy {
        findViewById(R.id.resetButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestAudioPermission()
        initViews()
        bindViews()
        initVariables()
    }

    override fun onRequestPermissionsResult( requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val audioRecordPermissionGranted = requestCode == REQUEST_RECODE_AUDIO_PERMISSION && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if(!audioRecordPermissionGranted){
            finish()
        }
    }

    private fun requestAudioPermission(){
        requestPermissions(requiredPermisson, REQUEST_RECODE_AUDIO_PERMISSION)
    }
    private fun initViews(){
        recodeButton.uptateIconWithState(state)
    }

    private fun bindViews(){
        recodeButton.setOnClickListener{
            when(state){
                State.BEFORE_RECORDING->{
                    startRecording()
                }
                State.ON_RECORDING->{
                    stopRecording()
                }
                State.AFTER_RECORDING->{
                    startPlaying()
                }
                State.ON_PLAYING->{
                    stopPlaying()
                }
            }
        }
        resetButton.setOnClickListener {
            stopPlaying()
            visualizerView.clearVisualization()
            recordTimeView.clearCountTime()
            state = State.BEFORE_RECORDING
        }
        visualizerView.onRequestCurrentAmplitude = {
            recoder?.maxAmplitude ?: 0
        }
    }

    private fun initVariables(){
        state = State.BEFORE_RECORDING
    }

    private fun startRecording(){
        recoder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(recordingFilePath)
            prepare()
        }
        recoder?.start()
        visualizerView.startVisualizing(false)
        recordTimeView.startCountUp()
        state = State.ON_RECORDING
    }
    private fun stopRecording(){
        recoder?.run {
            stop()
            release()
        }
        recoder = null
        visualizerView.stopVisualizing()
        recordTimeView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    private fun startPlaying(){
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            prepare()
        }
        player?.setOnCompletionListener {
            stopPlaying()
            state = State.AFTER_RECORDING
        }
        player?.start()
        visualizerView.startVisualizing(true)
        recordTimeView.startCountUp()
        state = State.ON_PLAYING
    }

    private fun stopPlaying(){
        player?.release()
        player = null
        visualizerView.stopVisualizing()
        recordTimeView.stopCountUp()
        state = State.AFTER_RECORDING
    }

    companion object{
        private const val REQUEST_RECODE_AUDIO_PERMISSION = 201
    }
}