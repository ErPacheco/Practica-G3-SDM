package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.viewModels.UserViewModel
import com.uc3m.whatthepass.views.login.LoginActivity
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity
import kotlinx.coroutines.launch

class PassAndFilesActivity : AppCompatActivity() {
    private val viewModel: PasswordViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_and_files)

        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Funciones que habilitan el menu de opciones del navegador
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
                    sp.edit().remove("loginEmail").apply()
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

            R.id.Delete -> {
                val sp = getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                val email = sp.getString("loginEmail", null)
                var online = false
                if (email.equals("Online")) {
                    online = true
                }

                lifecycleScope.launch {
                    if (email != null) {
                        deleteUser(online, email)
                    }
                }
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Función que elimina el usuario por completo de cualquiera de las BBDD (local u online)
    private suspend fun deleteUser(online: Boolean, email: String) {
        if (online) {
            val auth = FirebaseAuth.getInstance().currentUser?.uid
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users/$auth/passwords")
            myRef.removeValue()
            val refUser = database.getReference("Users/$auth")
            refUser.removeValue()
        } else {
            viewModel.deletePasswordByUser(email)
            userViewModel.deleteUser(email)
        }
    }
}
