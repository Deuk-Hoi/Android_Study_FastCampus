package com.example.digitalgallary

import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import java.util.*
import kotlin.concurrent.timer

class GallaryActivity : AppCompatActivity() {

    private val photoList = mutableListOf<Uri>()

    private val backImage by lazy {
        findViewById<ImageView>(R.id.backImage)
    }
    private val frontImage by lazy {
        findViewById<ImageView>(R.id.frontImage)
    }

    private var currentPosition = 0

    private var timer : Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallary)
        Log.d("PhotoFrame", "onCreate")
        getPhotoUriFromIntent()
    }

    private fun getPhotoUriFromIntent(){
        val size = intent.getIntExtra("photoListSize", 0)
        for(i in 0..size){
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer(){
        timer = timer(period = 5 * 1000){
            runOnUiThread{
                Log.d("PhotoFrame", "5초 지나감")
                val current = currentPosition
                val next = if(photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backImage.setImageURI(photoList[current])
                frontImage.alpha = 0f
                frontImage.setImageURI(photoList[next])
                frontImage.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()
                currentPosition = next
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("PhotoFrame", "onStop")
        timer?.cancel()
    }

    override fun onStart() {
        super.onStart()
        Log.d("PhotoFrame", "onStart")
        startTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PhotoFrame", "onDestroy")
        timer?.cancel()
    }
}