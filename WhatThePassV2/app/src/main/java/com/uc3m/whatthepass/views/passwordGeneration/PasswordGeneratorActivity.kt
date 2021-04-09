package com.uc3m.whatthepass.views.passwordGeneration

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import com.uc3m.whatthepass.R

class PasswordGeneratorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_generator)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}