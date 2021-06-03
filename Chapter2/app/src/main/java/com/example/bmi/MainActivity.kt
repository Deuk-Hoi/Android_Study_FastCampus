package com.example.bmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val heightEdit: EditText = findViewById(R.id.heightEdit)
        val weightEdit = findViewById<EditText>(R.id.weightEdit)

        val resultBtn = findViewById<Button>(R.id.resultBtn)

        resultBtn.setOnClickListener {
            Log.d("MainActivity", "Result Button Click")
            if (heightEdit.text.toString().isEmpty() || weightEdit.text.toString().isEmpty()) {
                Toast.makeText(this, "빈 값이 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val height = heightEdit.text.toString().toInt()
            val weight = weightEdit.text.toString().toInt()
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)
            Log.d("MainActivity", "Height : $height, Weight : $weight")
        }
    }
}