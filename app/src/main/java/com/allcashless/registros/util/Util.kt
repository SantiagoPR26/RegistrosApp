package com.allcashless.registros.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Locale


fun Double.toMoneyFormat(): String = "$ ${String.format("%,.0f", this)}"


fun String?.toDateFormat(): String {
    if (this == null) return "N/A"
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return outputFormat.format(formatter.parse(this) ?: return "N/A")
}

fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}