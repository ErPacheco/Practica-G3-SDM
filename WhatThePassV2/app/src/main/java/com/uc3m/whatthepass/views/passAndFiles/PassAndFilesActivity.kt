package com.uc3m.whatthepass.views.passAndFiles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.models.Password


class PassAndFilesActivity : AppCompatActivity() {

    private var email = "a@gmail.com" // Esto es un ejemplo, queremos solucionarlo para la siguiente entrega
    //var sessionId = intent.getStringExtra("email")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pass_and_files)

        val intent = intent
        val emailAux = intent.getStringExtra("email")
        if (emailAux != null) {
            setEmail(emailAux)
        }

        /*val bundle = Bundle()
        bundle.putString("email", email.toString())
        val frag = PasswordView()
        frag.arguments = bundle */
        setupActionBarWithNavController(findNavController(R.id.fragment))
    }

    private fun setEmail(email: String) {
        this.email = email
    }

    fun getMyData(): String? {
        return email
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController =findNavController(R.id.fragment);
        return navController.navigateUp() ||super.onSupportNavigateUp()
    }




}