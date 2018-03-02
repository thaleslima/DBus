package net.dublin.bus.ui.utilities

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import net.dublin.bus.R

object ViewUtil {
    fun showViewLayout(context: Context, view: View) {
        var animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom)
        animation.duration = 200
        view.startAnimation(animation)
        view.visibility = View.GONE

        animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom)
        animation.duration = 400
        view.startAnimation(animation)
        view.visibility = View.VISIBLE
    }

    fun hideViewLayout(context: Context, view: View) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_out_bottom)
        animation.duration = 200
        view.startAnimation(animation)
        view.visibility = View.GONE
    }
}
