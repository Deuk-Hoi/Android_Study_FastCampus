package com.example.bmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.w3c.dom.Text
import kotlin.math.pow

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val height = intent.getIntExtra("height", 0)
        val weight = intent.getIntExtra("weight", 0)
        Log.d("ResultActivity", "Height: $height, Weight: $weight")

        val bmi = weight / (height / 100.0).pow(2.0)
        val resultText = when {
            bmi >= 35.0 -> "고도비만"
            bmi >= 30.0 -> "중정도비만"
            bmi >= 25.0 -> "경도비만"
            bmi >= 20.0 -> "과체중"
            bmi >= 18.5 -> "정상체중"
            else -> "저체중"
        }

        val bmiTextView = findViewById<TextView>(R.id.bmiTextView)
        val resultTextView = findViewById<TextView>(R.id.resultText)

        bmiTextView.text = bmi.toString()
        resultTextView.text = resultText
    }
}