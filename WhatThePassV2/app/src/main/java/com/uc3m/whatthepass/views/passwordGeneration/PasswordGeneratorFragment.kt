package com.uc3m.whatthepass.views.passwordGeneration

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.uc3m.whatthepass.databinding.FragmentPasswordGeneratorBinding
import com.uc3m.whatthepass.util.PasswordGenerator.generatePassword
import kotlinx.coroutines.launch


class PasswordGeneratorFragment : Fragment() {
    private lateinit var binding: FragmentPasswordGeneratorBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPasswordGeneratorBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.generateNewPassword.setOnClickListener{
            lifecycleScope.launch {
                binding.generatedPassword.text = createPassword(binding.AZButton.isChecked,
                        binding.azButton.isChecked,
                        binding.numericButton.isChecked,
                        binding.specialCharButton.isChecked,
                        binding.seekBar.progress,
                        binding.numNumerical.text.toString().toInt(),
                        binding.numSpecial.text.toString().toInt())
            }

        }

        binding.moreNumericButtons.setOnClickListener{
            val n= binding.numNumerical.text.toString();
            val nn=n.toInt()+1;
            binding.numNumerical.setText(nn.toString())
        }
        binding.moreSpecialButton.setOnClickListener {
            val n = binding.numSpecial.text.toString();
            val nn = n.toInt() + 1;
            binding.numSpecial.setText(nn.toString())
        }

        binding.lessSpecialButton.setOnClickListener {
            val n = binding.numSpecial.text.toString();
            val nn = n.toInt() -1;
            if(nn>=0){
            binding.numSpecial.setText(nn.toString())
            }else{
                binding.numNumerical.setText(0.toString())
            }
        }
        binding.lessNumericButtons.setOnClickListener {
            val n = binding.numNumerical.text.toString();
            val nn = n.toInt() -1;
            if(nn>=0){
                binding.numNumerical.setText(nn.toString())
            }else{
                binding.numNumerical.setText(0.toString())
            }

        }

        binding.copyGeneratedPassword.setOnClickListener {
            lifecycleScope.launch {
                val pas= binding.generatedPassword.text.toString()
                copyPass(pas)
            }
        }

        return view
    }
    private suspend fun createPassword(isCapital: Boolean, isLower: Boolean, isNumeric: Boolean,
                                       isSpecial: Boolean, length: Int, minNumNumeric: Int, minNumSpecial: Int):String{
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

    private suspend fun copyPass(password:String){
        val clipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", password)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Contraseña copiada", Toast.LENGTH_LONG).show()
    }

}