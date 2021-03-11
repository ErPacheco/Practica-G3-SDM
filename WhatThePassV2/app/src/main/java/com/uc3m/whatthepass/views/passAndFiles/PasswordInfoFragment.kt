package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordInfoBinding
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.UnknownServiceException

class PasswordInfoFragment : Fragment() {
    private lateinit var binding: FragmentPasswordInfoBinding
    private  val passwordViewModel:PasswordViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordInfoBinding.inflate(inflater, container, false)
        val view = binding.root

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)


        val sp = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp?.getString("loginEmail", null);
       // passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        val adapter = ListAdapter(passwordViewModel)
        binding.createPassButton.setOnClickListener{
            if (email != null) {
                insertPassword(email)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            }
        }

        binding.clearCreateInputs.setOnClickListener{
            clearData()
        }

        return view
    }

    private fun insertPassword(email: String, masterPass: String) {
        val input_title = binding.titleInput.text.toString()
        val input_email = binding.emailInput.text.toString()
        val input_username = binding.usernameInput.text.toString()
        val input_password = binding.passwordInput.text.toString()
        val input_url = binding.urlInput.text.toString()

        when(checkInputs(input_title, input_password)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> {
                passwordViewModel.addPassword(input_title, email, input_email, input_username, input_password, input_url, masterPass)
                Toast.makeText(requireContext(), "Password created!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_passwordInfoFragment_to_passwordView)
            }
        }

    }

    private fun clearData() {
        binding.titleInput.text.clear()
        binding.passwordInput.text.clear()
        binding.urlInput.text.clear()
    }

    private fun checkInputs(title: String, pass: String):Int {
        return when {
            title.isEmpty() -> {
                1
            }
            pass.isEmpty() -> {
                2
            }
            else -> {
                3
            }
        }
    }
}