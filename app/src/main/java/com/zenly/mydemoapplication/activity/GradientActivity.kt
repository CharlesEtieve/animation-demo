package com.zenly.mydemoapplication.activity

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zenly.mydemoapplication.R
import kotlinx.android.synthetic.main.activity_gradient.*

class GradientActivity: AppCompatActivity() {

    private val evaluator = ArgbEvaluator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gradient)
        val drawable = ContextCompat.getDrawable(this, R.drawable.gradient)
        imageView.setImageDrawable(drawable)
        slider.addOnChangeListener { _, value, _ ->
            display(value)
        }
    }

    private fun display(value: Float) {
        val newStart = evaluator.evaluate(value, Color.RED, Color.GREEN) as Int
        val newEnd = evaluator.evaluate(value, Color.BLUE, Color.YELLOW) as Int
        (imageView.drawable as? GradientDrawable)?.colors = intArrayOf(newStart, newEnd)
    }
}