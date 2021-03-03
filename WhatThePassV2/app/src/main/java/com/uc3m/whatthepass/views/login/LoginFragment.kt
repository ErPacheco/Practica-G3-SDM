package com.uc3m.whatthepass.views.login

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.uc3m.whatthepass.databinding.FragmentLoginBinding
import com.uc3m.whatthepass.viewModels.UserViewModel
import com.uc3m.whatthepass.views.passAndFiles.PassAndFilesActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel
    private val EMAILREGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.signin.setOnClickListener{
            insertUser()
        }

        binding.login.setOnClickListener{
                lifecycleScope.launch{
                    loginUser()
                }
        }

        return view
    }

    private fun insertUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()

        if(emailCheck(email)) {
            if(passwordCheck(masterPassword)) {
                userViewModel.addUser(email, masterPassword)
                Toast.makeText(requireContext(), "Usuario creado!", Toast.LENGTH_LONG).show()
                loginView(email)
                binding.email.text.clear()
                binding.password.text.clear()
            } else {
                Toast.makeText(requireContext(), "La contraseña está mal introducida! Tiene que incluir minúsculas, " +
                        "mayúsculas y números, con una longitud mínima de 8 caracteres", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "La entrada tiene que ser un email", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun loginUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()
        val loginFind = withContext(Dispatchers.IO){
            userViewModel.loginUser(email, masterPassword)
        }
        if(loginFind) {
            loginView(email)
            Toast.makeText(requireContext(), "Inicio de sesión correcto!", Toast.LENGTH_LONG).show()
            binding.email.text.clear()
            binding.password.text.clear()
        } else {
            Toast.makeText(requireContext(), "Usuario no registrado!", Toast.LENGTH_LONG).show()
            binding.email.text.clear()
            binding.password.text.clear()
        }
    }

    private fun loginView(email:String) {
        val intent = Intent(this@LoginFragment.context, PassAndFilesActivity::class.java)
        intent.putExtra("email", email)



        activity?.startActivity(intent)
    }

    private fun emailCheck(email: String): Boolean {
        return email.isNotEmpty() && Pattern.compile(EMAILREGEX).matcher(email).matches()
    }

    private fun passwordCheck(password: String): Boolean {
        var passValid = true

        if(password.length < 8) {
            passValid = false
        }

        var reg = ".*[0-9].*"
        var pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE)
        var matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[A-Z].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[a-z].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        return passValid
    }
}