package com.uc3m.whatthepass.views.passwordGeneration

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordGeneratorBinding
import com.uc3m.whatthepass.util.PasswordGenerator
import com.uc3m.whatthepass.util.PasswordGenerator.generatePassword


class PasswordGeneratorFragment : Fragment() {
    private lateinit var binding: FragmentPasswordGeneratorBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordGeneratorBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.generateNewPassword.setOnClickListener{
            binding.generatedPassword.text=createPassword(binding.AZButton.isChecked,
                    binding.azButton.isChecked,
                    binding.numericButton.isChecked,
                    binding.specialCharButton.isChecked,
                    binding.seekBar.progress,
                    binding.numNumerical.text.toString().toInt(),
                    binding.numSpecial.text.toString().toInt())
        }


        return view
    }
    private fun createPassword(isCapital:Boolean,isLower:Boolean,isNumeric:Boolean,
                                       isSpecial:Boolean,length:Int, minNumNumeric:Int,minNumSpecial:Int):String{
        var password =""
        if(length>0 && ((minNumNumeric<=length || minNumSpecial<=length) || (minNumNumeric+minNumSpecial<=length))){
            password= generatePassword(isCapital, isLower, isNumeric, isSpecial, length, minNumNumeric, minNumSpecial)
        }else if(length<=0){
            Toast.makeText(requireContext(), "La contraseña debe tener una longitud mayor a 0", Toast.LENGTH_LONG).show()
        }else if(minNumNumeric>=length){
            Toast.makeText(requireContext(), "El número mínimo de caracteres númericos debe ser menor a " +
                    "la longitud de la contraseña a generar", Toast.LENGTH_LONG).show()
        }else if(minNumSpecial>=length){
            Toast.makeText(requireContext(), "El número mínimo de caracteres especiales debe ser menor a" +
                    " la longitud de la contraseña a generar", Toast.LENGTH_LONG).show()

        }else if(minNumNumeric+minNumSpecial>=length){
            Toast.makeText(requireContext(), "El número mínimo de caracteres especiales y numéricos " +
                    "debe ser menor que la longitud de la contraseña a generar", Toast.LENGTH_LONG).show()
        }
        return password


    }
}