package com.uc3m.whatthepass.views.passAndFiles

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel


class PassAndFilesActivity : AppCompatActivity() {
    private val viewModel: PasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_and_files)

        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =findNavController(R.id.fragment);
        return navController.navigateUp() ||super.onSupportNavigateUp()
    }




}