package net.dublin.bus.ui.utilities

import android.content.Context
import android.support.annotation.NonNull
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.dublin.bus.R

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId, this, attachToRoot)
}

fun Context.snack(@NonNull view: View, @StringRes resId: Int) {
    Snackbar.make(view, resId, Snackbar.LENGTH_SHORT)
            .setActionTextColor(ContextCompat.getColor(this, R.color.button))
            .show()
}

fun Context.snackBarNoConnection(@NonNull view: View, listener: (View) -> Unit): Snackbar {
    return Snackbar.make(view, R.string.title_no_connection, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(ContextCompat.getColor(this, R.color.button))
            .setAction(R.string.title_retry, listener)
}

fun Context.snackBarErrorMessage(@NonNull view: View, listener: (View) -> Unit): Snackbar {
    return Snackbar.make(view, R.string.error_message, Snackbar.LENGTH_INDEFINITE)
            .setActionTextColor(ContextCompat.getColor(this, R.color.button))
            .setAction(R.string.title_retry, listener)
}