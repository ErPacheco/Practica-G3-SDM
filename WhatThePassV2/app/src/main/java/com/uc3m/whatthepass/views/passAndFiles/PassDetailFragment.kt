package com.uc3m.whatthepass.views.passAndFiles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.uc3m.whatthepass.databinding.FragmentPassDetailBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.viewModels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class PassDetailFragment : Fragment() {
    private lateinit var binding: FragmentPassDetailBinding
    private lateinit var userViewModel: UserViewModel
    private val passwordViewModel: PasswordViewModel by activityViewModels()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        return view
    }

    private fun insertFields(email: String, password: Password) {
        binding.titleDetail.text = password.name
        binding.emailDetail.text = password.inputEmail
        binding.usernameDetail.text = password.inputUser
        lateinit var userLogin: User
        lifecycleScope.launch{
            userLogin = userViewModel.findUserByEmail(email)
            val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
            binding.passwordDetailInput.text = realPass
        }
        binding.URIDetail.text = password.url
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sp = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp?.getString("loginEmail", null)

        if(email != null) {
            passwordViewModel.message.observe(viewLifecycleOwner, object : Observer<Password> {
                override fun onChanged(o: Password?) {
                    if(o!=null){
                        Log.d("Mensaje enviado", o.user)
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