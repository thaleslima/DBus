package net.dublin.bus.ui.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import net.dublin.bus.R

private const val PREFERENCES_MAP = "preferences_map"

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

fun Context.getDip(value: Int): Int {
    val displayMetrics = this.resources.displayMetrics

    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value.toFloat(), displayMetrics).toInt()
}

fun Fragment.getDip(value: Int): Int {
    return this.requireContext().getDip(value)
}

fun Context.isNetworkAvailable(): Boolean {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = cm.activeNetworkInfo
    return info != null && info.isConnected && info.isAvailable
}

fun Context.showViewLayout(view: View) {
    var animation = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_bottom)
    animation.duration = 200
    view.startAnimation(animation)
    view.visibility = View.GONE

    animation = AnimationUtils.loadAnimation(this, R.anim.abc_slide_in_bottom)
    animation.duration = 400
    view.startAnimation(animation)
    view.visibility = View.VISIBLE
}

fun Context.hideViewLayout(view: View) {
    val animation = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_bottom)
    animation.duration = 200
    view.startAnimation(animation)
    view.visibility = View.GONE
}

fun Context.saveShowMapAtRouteDetail(value: Boolean) {
    val settings = PreferenceManager.getDefaultSharedPreferences(this)
    val editor = settings.edit()
    editor.putBoolean(PREFERENCES_MAP, value)
    editor.apply()
}

fun Context.getShowMapAtRouteDetail(): Boolean {
    val settings = PreferenceManager.getDefaultSharedPreferences(this)
    return settings.getBoolean(PREFERENCES_MAP, false)
}

