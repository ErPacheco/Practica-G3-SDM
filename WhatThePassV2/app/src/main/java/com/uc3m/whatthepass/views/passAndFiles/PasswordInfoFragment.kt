package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordInfoBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.viewModels.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class PasswordInfoFragment : Fragment() {
    private lateinit var binding: FragmentPasswordInfoBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val passwordViewModel: PasswordViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        auth= FirebaseAuth.getInstance()

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
       // passwordViewModel = ViewModelProvider(this).get(PasswordViewModel::class.java)
        val adapter = ListAdapter(passwordViewModel)
        lateinit var userLogin: User
        if(email != null) {
            lifecycleScope.launch{
                userLogin = userViewModel.findUserByEmail(email)
            }
        } else {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            return view
        }

        binding.createPassButton.setOnClickListener{ v ->
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(v.windowToken, 0)
            if(auth.currentUser==null){
                insertPassword(email, userLogin.masterPass)
                adapter.notifyDataSetChanged()
            }else{
                insertPasswordOnline(email, "hola")
            }

        }

        binding.clearCreateInputs.setOnClickListener{
            clearData()
        }

        return view
    }

    private fun insertPasswordOnline(email: String, masterPass: String) {
        val inputTitle = binding.titleInput.text.toString()
        val inputEmail = binding.emailInput.text.toString()
        val inputUsername = binding.usernameInput.text.toString()
        val inputPassword = binding.passwordInput.text.toString()
        val inputUrl = binding.urlInput.text.toString()
        database= FirebaseDatabase.getInstance()
        when(checkInputs(inputTitle, inputPassword)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> {
                val currentDateTime = System.currentTimeMillis();
                val myRef = database.getReference("Users/"+auth.currentUser.uid+"/passwords/"+currentDateTime)
                val en= Hash.encrypt(inputPassword, "masterPass")

                val p= Password(currentDateTime,inputTitle,auth.currentUser.email,inputEmail,inputUsername,en,inputUrl)
                myRef.setValue(p);
                Toast.makeText(requireContext(), "Password created!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_passwordInfoFragment_to_passwordView)
            }
        }

    }

    private fun insertPassword(email: String, masterPass: String) {
        val inputTitle = binding.titleInput.text.toString()
        val inputEmail = binding.emailInput.text.toString()
        val inputUsername = binding.usernameInput.text.toString()
        val inputPassword = binding.passwordInput.text.toString()
        val inputUrl = binding.urlInput.text.toString()

        when(checkInputs(inputTitle, inputPassword)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> {
                passwordViewModel.addPassword(inputTitle, email, inputEmail, inputUsername, inputPassword, inputUrl, masterPass)
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