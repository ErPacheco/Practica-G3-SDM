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


class PassDetailFragment : Fragment() {
    private lateinit var binding: FragmentPassDetailBinding
    private  val passwordViewModel: PasswordViewModel by activityViewModels()


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordViewModel.message.observe(viewLifecycleOwner, object : Observer<Password> {
            override fun onChanged(o: Password?) {
                if(o!=null){
                   val password=o
                    Log.d("Mensaje enviado", password.user)
                    // aqui introducir los datos de password en los campos correspondientes
                }

            }
        })
    }
}