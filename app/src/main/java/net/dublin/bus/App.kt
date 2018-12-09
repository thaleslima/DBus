package net.dublin.bus

import androidx.multidex.MultiDexApplication
import com.google.firebase.FirebaseApp

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        //FirebaseApp.initializeApp(this)
    }
}