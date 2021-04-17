package com.uc3m.whatthepass.views.passwordGeneration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.views.login.LoginActivity

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.logout -> {
                val sp = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val email = sp.getString("loginEmail", null)
                if (email.equals("Online")) {
                    sp.edit().remove("loginEmail").commit()
                    Firebase.auth.signOut()
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
            R.id.PasswordGenerator -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
