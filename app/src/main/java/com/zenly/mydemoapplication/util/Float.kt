package com.zenly.mydemoapplication.util

import android.content.Context

fun Float.toPx(context: Context): Int {
    return (this*context.resources.displayMetrics.density).toInt()
}