package com.example.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {
    private val clearButton: Button by lazy {
        findViewById(R.id.clear)
    }
    private val addButton: Button by lazy {
        findViewById(R.id.addButton)
    }
    private val runButton: Button by lazy {
        findViewById(R.id.runButton)
    }
    private val numberPicker: NumberPicker by lazy {
        findViewById(R.id.numberPicker)
    }

    private var didRun = false

    private val pickNumberSet = hashSetOf<Int>()

    private val numbers: List<TextView> by lazy {
        listOf(
            findViewById(R.id.Number1), findViewById(R.id.Number2), findViewById(R.id.Number3),
            findViewById(R.id.Number4), findViewById(R.id.Number5), findViewById(R.id.Number6)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberPicker.minValue = 1
        numberPicker.maxValue = 45
        initRunButton()
        initAddButton()
        initClear()

    }

    fun initAddButton() {
        addButton.setOnClickListener {
            if (didRun) {
                Toast.makeText(this, "초기화 후 다시 시도하세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개 까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택된 번호 입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numbers[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()
            setNumberBackground(numberPicker.value, textView)
            pickNumberSet.add(numberPicker.value)
        }
    }

    private fun setNumberBackground(number: Int, textView: TextView) {
        when (number) {
            in 1..10 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 -> textView.background =
                ContextCompat.getDrawable(this, R.drawable.circle_green)
            else -> textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
        }
    }

    fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNuber()
            list.forEachIndexed { index, i ->
                numbers[index].text = i.toString()
                numbers[index].isVisible = true
                setNumberBackground(i, numbers[index])
            }
            Log.d("List", list.toString())
            didRun = true
        }
    }

    fun initClear() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            didRun = false
            numbers.forEach {
                it.isVisible = false
            }
        }
    }

    fun getRandomNuber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45) {
                if (pickNumberSet.contains(i)) {
                    continue
                }
                this.add(i)
            }
        }
        numberList.shuffle()
        return (pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)).sorted()
    }
}