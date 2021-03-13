package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.uc3m.whatthepass.databinding.FragmentPassEditBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.viewModels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class PassEditFragment : Fragment() {
    private lateinit var binding: FragmentPassEditBinding
    private lateinit var userViewModel: UserViewModel
    private val passwordViewModel: PasswordViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassEditBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.viewButton3.setOnClickListener{
            val passInputType = binding.passwordDetailInput2.inputType
            if(passInputType == 129) {
                binding.passwordDetailInput2.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                binding.passwordDetailInput2.inputType = 129
            }
        }

        return view
    }

    private fun insertFields(email: String, password: Password) {
        binding.titleDetail2.setText(password.name)
        binding.emailDetail2.setText(password.inputEmail)
        binding.usernameDetail2.setText(password.inputUser)
        lateinit var userLogin: User
        lifecycleScope.launch{
            userLogin = userViewModel.findUserByEmail(email)
            val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
            binding.passwordDetailInput2.setText(realPass)
        }
        binding.URIDetail2.setText(password.url)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
        if(email != null) {
            passwordViewModel.message.observe(viewLifecycleOwner, object : Observer<Password> {
                override fun onChanged(o: Password?) {
                    if (o != null) {
                        insertFields(email, o)
                    }
                }
            })
        } else {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }
    }
}
