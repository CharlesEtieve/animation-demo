package com.zenly.mydemoapplication.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.zenly.mydemoapplication.R
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        gradientButton.setOnClickListener {
            val intent = Intent(this, GradientActivity::class.java)
            startActivity(intent)
        }
        counterButton.setOnClickListener {
            val intent = Intent(this, CounterActivity::class.java)
            startActivity(intent)
        }

    }

}