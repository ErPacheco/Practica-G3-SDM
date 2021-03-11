package com.uc3m.whatthepass.views.passAndFiles

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
import at.favre.lib.crypto.bcrypt.BCrypt
import com.uc3m.whatthepass.util.Hash


class PassDetailFragment : Fragment() {
    private lateinit var binding: FragmentPassDetailBinding
    private  val passwordViewModel: PasswordViewModel by activityViewModels()
    private lateinit var password: Password

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        var password: Password? = null;
        passwordViewModel.message.observe(viewLifecycleOwner, object : Observer<Password> {
            override fun onChanged(o: Password?) {
                if(o!=null){
                    password=o
                    Log.d("Mensaje enviado", password.user)
                    // aqui introducir los datos de password en los campos correspondientes
                }

            }
        })

        if(password != null) {
            insertFields(password!!)
        } else {
            Toast.makeText(requireContext(), "Error!!!!!", Toast.LENGTH_LONG).show()
        }
        return view
    }

    private fun insertFields(password: Password) {
        binding.titleDetail.text = password.name
        binding.emailDetail.text = password.inputEmail
        binding.usernameDetail.text = password.inputUser

        binding.passwordDetail.text = password.hashPassword
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}