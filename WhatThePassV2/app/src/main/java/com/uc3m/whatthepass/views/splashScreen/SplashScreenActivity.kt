package com.uc3m.whatthepass.views.splashScreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.views.passAndFiles.PassAndFilesActivity

// Splash Screen que se ve después de iniciar sesión
class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 2000L // 2 segundos
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this@SplashScreenActivity, PassAndFilesActivity::class.java)
                startActivity(i)
                finish()
            },
            SPLASH_TIME_OUT
        )
    }
}
