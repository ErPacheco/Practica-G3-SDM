package com.uc3m.whatthepass.views.passwordGeneration

import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class PasswordGeneratorActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_generator)

        // Enables Always-on
        setAmbientEnabled()
    }
}