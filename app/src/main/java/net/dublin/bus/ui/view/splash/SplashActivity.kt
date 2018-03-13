package net.dublin.bus.ui.view.splash

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import net.dublin.bus.ui.view.main.MainActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MainActivity.navigate(this, true)
        finish()
    }
}
