package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.views.login.LoginActivity
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity

class PassAndFilesActivity : AppCompatActivity() {
    private val viewModel: PasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_and_files)

        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
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
                    FirebaseAuth.getInstance().signOut()
                }

                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()

                return true
            }
            R.id.PasswordGenerator -> {
                val intent = Intent(this, PasswordGeneratorActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
