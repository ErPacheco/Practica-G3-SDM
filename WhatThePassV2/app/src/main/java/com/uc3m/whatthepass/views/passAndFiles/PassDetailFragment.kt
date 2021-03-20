package com.uc3m.whatthepass.views.passAndFiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.uc3m.whatthepass.databinding.FragmentPassDetailBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.passwordApi.PassInfoViewModel
import com.uc3m.whatthepass.passwordApi.PassInfoViewModelFactory
import com.uc3m.whatthepass.passwordApi.repository.Repository
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.Hash.kekHashSubstring
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

        val repository = Repository()
        val passViewModelFactory = PassInfoViewModelFactory(repository)
        val passViewModel = ViewModelProvider(this, passViewModelFactory).get(PassInfoViewModel::class.java)

        binding.viewButton.setOnClickListener{
            val passInputType = binding.passwordDetailInput.inputType
            if(passInputType == 129) {
                binding.passwordDetailInput.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                binding.passwordDetailInput.inputType = 129
            }
        }

        binding.copyButton.setOnClickListener{
            val passToCopy = binding.passwordDetailInput.text

            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", passToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "Password copied to clipboard!", Toast.LENGTH_LONG).show()
        }

        binding.passBreaches.setOnClickListener{
            binding.progressBarAPI.visibility = View.VISIBLE
            val passToInspect = binding.passwordDetailInput.text.toString()
            val passSubstring = kekHashSubstring(passToInspect)
            passViewModel.getPasswordInfo(passSubstring)

            passViewModel.myPasswordResponse.observe(viewLifecycleOwner, Observer{response ->
                if(response.isSuccessful) {
                    val breachesCount = response.body()?.passData?.count.toString()
                    val countInt = breachesCount.toInt()
                    binding.progressBarAPI.visibility = View.INVISIBLE
                    if(countInt in 1..99) {
                        Toast.makeText(requireContext(), "Your password has appeared in some data breaches, it should be improved", Toast.LENGTH_LONG).show()
                    } else if (countInt >= 100) {
                        Toast.makeText(requireContext(), "Your password has been seen $countInt times before!! You must change it now!!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    binding.progressBarAPI.visibility = View.INVISIBLE
                    Toast.makeText(requireContext(), "No password breach has been found in the database!!", Toast.LENGTH_LONG).show()
                }
            })
        }

        return view
    }

    private fun insertFields(email: String, password: Password) {
        binding.titleDetail.setText(password.name)
        binding.emailDetail.setText(password.inputEmail)
        binding.usernameDetail.setText( password.inputUser)
        lateinit var userLogin: User
        lifecycleScope.launch{
            userLogin = userViewModel.findUserByEmail(email)
            val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
            binding.passwordDetailInput.setText(realPass)
        }
        binding.URIDetail.setText(password.url)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)

        lateinit var actualPassword: Password
        if(email != null) {
            passwordViewModel.message.observe(viewLifecycleOwner, object : Observer<Password> {
                override fun onChanged(o: Password?) {
                    if(o!=null){
                        actualPassword = o
                        insertFields(email, o)
                    }
                }
            })

            binding.editPasswordButton.setOnClickListener{
                passwordViewModel.sentPassword(actualPassword)
                findNavController().navigate(R.id.action_passDetailFragment_to_passEditFragment)
            }

        } else {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }
    }
}