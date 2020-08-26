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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener
import com.zenly.mydemoapplication.R
import kotlinx.android.synthetic.main.cell_number.view.*


class CounterView(context: Context, attrs: AttributeSet): RelativeLayout(context, attrs) {

    private val button = Button(context)
    private val recyclerView = RecyclerView(context)
    private val defaultSpeed = 3000000
    private var state = State.ASK_NUMBER

    enum class State {
        ASK_NUMBER,
        ROLL
    }

    init {
        //initialize button
        button.id = android.R.id.button1
        button.text = resources.getString(R.string.set_number)
        button.setOnClickListener {
            when(state) {
                State.ASK_NUMBER -> {
                    askNumber()
                }
                State.ROLL -> {
                    recyclerView.fling(0, defaultSpeed)
                    state = State.ASK_NUMBER
                    button.text = resources.getString(R.string.reset)
                }
            }
        }

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
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params.addRule(ALIGN_PARENT_BOTTOM)
        addView(button, params)

        //place recyclerview
        val params2 = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        params2.addRule(ALIGN_PARENT_TOP)
        params2.addRule(ABOVE, button.id)
        addView(recyclerView, params2)
    }

    private fun askNumber() {
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        AlertDialog.Builder(context)
            .setMessage(R.string.counter_popup_message)
            .setView(input)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                input.text.toString().toIntOrNull()?.let { value ->
                    initializeAdapter(value)
                    state = State.ROLL
                    button.text = resources.getString(R.string.roll)
                }
            }
            .show()
    }

    private fun initializeAdapter(number: Int) {
        val dataSet = ArrayList<String>()
        for (i in number..number+12) {
            dataSet.add(i.toString())
        }

        val adapter = NumberAdapter(dataSet)
        recyclerView.adapter = adapter
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
            holder.itemView.numberLabel.text = dataSet[position]
        }

        override fun getItemCount(): Int {
            return dataSet.size
        }

        class NumberViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup)
    }

}