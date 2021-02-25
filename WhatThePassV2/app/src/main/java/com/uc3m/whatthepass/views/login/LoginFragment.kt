package com.uc3m.whatthepass.views.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.uc3m.whatthepass.databinding.FragmentLoginBinding
import com.uc3m.whatthepass.viewModels.UserViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel

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

        return view
    }

    private fun insertUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()

        userViewModel.addUser(email, masterPassword)
        Toast.makeText(requireContext(), "Usuario creado!", Toast.LENGTH_LONG).show()
        // Aqui debería navegar directamente a la actividad de la lista de contraseñas
    }
}