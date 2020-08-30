package com.zenly.mydemoapplication.activity

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jakewharton.rxbinding2.widget.RxSeekBar
import com.zenly.mydemoapplication.R
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.activity_gradient.*

class GradientActivity: AppCompatActivity() {

    private val evaluator = ArgbEvaluator()
    private val bag = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gradient)
        RxSeekBar
            .changes(seekbar)
            .to { RxJavaBridge.toV3Observable(it) }
            .subscribe { value ->
            display(value)
        }.addTo(bag)
    }

    private fun display(value: Int) {
        val valueFloat = value.toFloat()/100
        val newStart = evaluator.evaluate(valueFloat, Color.RED, Color.GREEN) as Int
        val newEnd = evaluator.evaluate(valueFloat, Color.BLUE, Color.YELLOW) as Int
        val drawable = GradientDrawable()
        drawable.colors = intArrayOf(newStart, newEnd)
        imageView.setImageDrawable(drawable)
    }

    override fun onDestroy() {
        super.onDestroy()
        bag.clear()
    }
}