package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import com.uc3m.whatthepass.views.passwordGeneration.PasswordGeneratorActivity
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class PasswordInfoFragment : Fragment() {
    private lateinit var binding: FragmentPasswordInfoBinding
    private lateinit var userViewModel: UserViewModel
    private val passwordViewModel: PasswordViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
        val adapter = ListAdapter(passwordViewModel)

        if (email == null) {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }

        if (email != "Online") {
            var userLogin: User?
            lifecycleScope.launch {

                userLogin = email.let { userViewModel.findUserByEmail(it) }

                if (userLogin == null) {
                    Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
                    exitProcess(-1)
                } else {
                    binding.createPassButton.setOnClickListener { v ->
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        insertPassword(email, userLogin!!.masterPass)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        } else if (email == "Online") {
            binding.createPassButton.setOnClickListener { v ->
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                database = FirebaseDatabase.getInstance()
                val myRef = database.getReference("Users/" + auth.currentUser?.uid + "/masterPass")
                myRef.get().addOnSuccessListener {
                    val masterPassOnline = it.getValue(String::class.java)
                    if (masterPassOnline != null) {
                        insertPasswordOnline(masterPassOnline)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }

        binding.clearCreateInputs.setOnClickListener {
            clearData()
        }

        binding.generatePasswordOnCreate.setOnClickListener {
            val intent = Intent(requireContext(), PasswordGeneratorActivity::class.java)
            activity?.startActivity(intent)
        }

        return view
    }

    // Función para crear la contraseña y guardarla en Firebase
    private fun insertPasswordOnline(masterPass: String) {
        val inputTitle = binding.titleInput.text.toString()
        val inputEmail = binding.emailInput.text.toString()
        val inputUsername = binding.usernameInput.text.toString()
        val inputPassword = binding.passwordInput.text.toString()
        val inputUrl = binding.urlInput.text.toString()
        database = FirebaseDatabase.getInstance()
        when (checkInputs(inputTitle, inputPassword, inputEmail, inputUrl)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> Toast.makeText(requireContext(), "It is not an email!", Toast.LENGTH_LONG).show()
            4 -> Toast.makeText(requireContext(), "Url field must be a valid url", Toast.LENGTH_LONG).show()
            5 -> {
                if (auth.currentUser != null) {
                    val currentDateTime = System.currentTimeMillis()
                    val myRef = database.getReference("Users/" + auth.currentUser?.uid + "/passwords/" + currentDateTime)
                    val en = Hash.encrypt(inputPassword, masterPass)

                    val p = auth.currentUser?.email?.let { Password(currentDateTime, inputTitle, it, inputEmail, inputUsername, en, inputUrl) }
                    myRef.setValue(p)
                    Toast.makeText(requireContext(), "Password created!", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_passwordInfoFragment_to_passwordView)
                }
            }
        }
    }

    // Función para crear la contraseña y guardarla en Room
    private fun insertPassword(email: String, masterPass: String) {
        val inputTitle = binding.titleInput.text.toString()
        val inputEmail = binding.emailInput.text.toString()
        val inputUsername = binding.usernameInput.text.toString()
        val inputPassword = binding.passwordInput.text.toString()
        val inputUrl = binding.urlInput.text.toString()

        when (checkInputs(inputTitle, inputPassword, inputEmail, inputUrl)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> Toast.makeText(requireContext(), "Email field must be an email", Toast.LENGTH_LONG).show()
            4 -> Toast.makeText(requireContext(), "Url field must be a valid url", Toast.LENGTH_LONG).show()
            5 -> {
                passwordViewModel.addPassword(inputTitle, email, inputEmail, inputUsername, inputPassword, inputUrl, masterPass)
                Toast.makeText(requireContext(), "Password created!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_passwordInfoFragment_to_passwordView)
            }
        }
    }

    // Función que limpia los inputs
    private fun clearData() {
        binding.titleInput.text.clear()
        binding.emailInput.text.clear()
        binding.usernameInput.text.clear()
        binding.passwordInput.text.clear()
        binding.urlInput.text.clear()
    }

    // Función que se encarga de la lógica de los inputs a la hora de crear una contraseña
    private fun checkInputs(title: String, pass: String, email: String, url: String): Int {
        return when {
            title.isEmpty() -> {
                1
            }
            pass.isEmpty() -> {
                2
            }
            email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                3
            }
            url.isNotEmpty() && !Patterns.WEB_URL.matcher(url).matches() -> {
                4
            }
            else -> {
                5
            }
        }
    }
}
