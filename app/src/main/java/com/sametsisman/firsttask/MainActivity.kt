package com.sametsisman.firsttask

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveButton.setOnClickListener {
            val minute = minuteEditText.text.toString().toInt()
            val second = secondEditText.text.toString().toInt()
            val prepareSecond = prepareSecondEditText.text.toString().toInt()

            val sharedPreferences = getSharedPreferences("timeShared", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("minute",minute)
            editor.putInt("second",second)
            editor.putInt("prepareSecond",prepareSecond)
            editor.apply()

            val intent = Intent(this,TimerActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}