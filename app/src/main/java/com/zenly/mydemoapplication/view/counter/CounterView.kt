package com.zenly.mydemoapplication.view.counter

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.jakewharton.rxbinding2.view.RxView
import com.zenly.mydemoapplication.R
import com.zenly.mydemoapplication.util.toPx
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.cell_number.view.*
import kotlinx.android.synthetic.main.edit_text_counter.view.*
import java.util.concurrent.TimeUnit


class CounterView(context: Context, attrs: AttributeSet): RelativeLayout(context, attrs) {

    private val button = Button(context)
    private val recyclerView = RecyclerView(context)
    private val defaultSpeed = 3000000
    private var state = State.ASK_NUMBER
    private val bag = CompositeDisposable()

    private enum class State {
        ASK_NUMBER,
        ROLL
    }

    init {
        //initialize button
        button.id = android.R.id.button1
        button.text = resources.getString(R.string.set_number)

        //initialize recyclerview
        recyclerView.layoutManager = LinearLayoutManager(context)
        //remove the over scroll effect
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        //remove the manual scroll of the recyclerview
        recyclerView.addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
        })

        //place button
        val params = LayoutParams(LayoutParams.MATCH_PARENT, resources.getDimensionPixelSize(R.dimen.button_height))
        params.addRule(ALIGN_PARENT_BOTTOM)
        params.setMargins(resources.getDimensionPixelSize(R.dimen.horizontal_margin), 0, resources.getDimensionPixelSize(R.dimen.horizontal_margin), 15f.toPx(context))
        addView(button, params)

        //place recyclerview
        val params2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params2.addRule(ALIGN_PARENT_TOP)
        params2.addRule(ABOVE, button.id)
        addView(recyclerView, params2)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        RxView
            .clicks(button)
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .to { RxJavaBridge.toV3Observable(it) }
            .subscribe {
                handleButtonClicked()
            }
            .addTo(bag)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bag.clear()
    }

    private fun handleButtonClicked() {
        when(state) {
            State.ASK_NUMBER -> {
                askNumber()
            }
            State.ROLL -> {
                state = State.ASK_NUMBER
                button.text = resources.getString(R.string.reset)
                recyclerView.fling(0, defaultSpeed)
            }
        }
    }

    private fun askNumber() {
        val layout = LayoutInflater.from(context).inflate(R.layout.edit_text_counter, null) as LinearLayout
        AlertDialog.Builder(context)
            .setMessage(R.string.counter_popup_message)
            .setView(layout)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                layout.editTextCounterPopup.text.toString().toIntOrNull()?.let { value ->
                    state = State.ROLL
                    button.text = resources.getString(R.string.roll)
                    initializeAdapter(value)
                }
            }
            .show()
    }

    private fun initializeAdapter(number: Int) {
        val dataSet = ArrayList<String>()
        for (i in number..number+12) {
            dataSet.add(i.toString())
        }
        val textSize = (height/10).toFloat()
        val adapter = NumberAdapter(dataSet, textSize)
        recyclerView.adapter = adapter
    }

    private class NumberAdapter(var dataSet: List<String>, val textSize: Float): RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.cell_number,
                parent,
                false
            ) as ViewGroup
            itemView.numberLabel.textSize = textSize
            return NumberViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.itemView.numberLabel.text = dataSet[position]
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        class NumberViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)
    }

}