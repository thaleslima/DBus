package net.dublin.bus.ui.utilities

import android.content.Context
import android.util.TypedValue

object Sizes {
    fun getDip(context: Context, value: Int): Int {
        val displayMetrics = context.resources.displayMetrics

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), displayMetrics).toInt()
    }
}
