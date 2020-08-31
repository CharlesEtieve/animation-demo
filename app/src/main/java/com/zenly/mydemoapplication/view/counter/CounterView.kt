package com.zenly.mydemoapplication.view.counter

import android.app.AlertDialog
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.jakewharton.rxbinding2.view.RxView
import com.zenly.mydemoapplication.R
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import kotlinx.android.synthetic.main.cell_number.view.*
import kotlinx.android.synthetic.main.edit_text_counter.view.*
import kotlinx.android.synthetic.main.fragment_counter.view.*
import java.util.concurrent.TimeUnit


class CounterView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    private var state = State.ASK_NUMBER
    private val bag = CompositeDisposable()


    private enum class State {
        ASK_NUMBER,
        ROLL
    }

    init {
        inflate(context, R.layout.fragment_counter, this)

        //remove the manual scroll of the recyclerview
        counterRecyclerview.addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true
            }
        })
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        RxView
            .clicks(counterButton)
            .throttleFirst(1, TimeUnit.SECONDS)
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
                counterButton.text = resources.getString(R.string.reset)
                counterRecyclerview.layoutManager?.smoothScrollToPosition(counterRecyclerview, RecyclerView.State(), DEFAULT_ROLL_SIZE)
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
                    counterButton.text = resources.getString(R.string.roll)
                    initializeAdapter(value)
                }
            }
            .show()
    }

    private fun initializeAdapter(number: Int) {
        val dataSet = ArrayList<String>()
        for (i in number..number + DEFAULT_ROLL_SIZE) {
            val numberInRange = (i%1000) // 0 - 999 range
            val formattedLabel = String.format("%03d", numberInRange) //add leading zeros
            dataSet.add(formattedLabel)
        }
        val adapter = NumberAdapter(dataSet)
        counterRecyclerview.adapter = adapter
    }

    private class NumberAdapter(var dataSet: List<String>): RecyclerView.Adapter<NumberAdapter.NumberViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.cell_number,
                parent,
                false
            ) as ViewGroup
            return NumberViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            holder.itemView.textViewCellNumber.text = dataSet[position]
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        class NumberViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)
    }

    companion object {
        const val DEFAULT_ROLL_SIZE = 50
    }

}