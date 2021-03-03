package com.uc3m.whatthepass.views.passAndFiles

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.models.Password

class PassAndFilesActivity : AppCompatActivity(),Comunicator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_and_files)

        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =findNavController(R.id.fragment);
        return navController.navigateUp() ||super.onSupportNavigateUp()
    }

    override fun passDataCom(password_input: Password) {
        TODO("Not yet implemented")
    }
}