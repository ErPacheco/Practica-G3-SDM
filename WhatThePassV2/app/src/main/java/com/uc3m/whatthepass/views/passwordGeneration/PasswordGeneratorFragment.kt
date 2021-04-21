package com.uc3m.whatthepass.views.passwordGeneration

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPasswordGeneratorBinding
import com.uc3m.whatthepass.passwordApi.PassInfoViewModel
import com.uc3m.whatthepass.passwordApi.PassInfoViewModelFactory
import com.uc3m.whatthepass.passwordApi.repository.Repository
import com.uc3m.whatthepass.util.Hash.kekHashSubstring
import com.uc3m.whatthepass.util.PasswordGenerator.generatePassword
import kotlinx.coroutines.launch

class PasswordGeneratorFragment : Fragment() {
    private lateinit var binding: FragmentPasswordGeneratorBinding
    private var popupMsg: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordGeneratorBinding.inflate(inflater, container, false)
        val view = binding.root

        // Link the number next to the seekbar to the real number indicated by the seekbar
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.lengthNumber.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do something
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do something
            }
        })

        // Import repository password breaches API
        val repository = Repository()
        val passViewModelFactory = PassInfoViewModelFactory(repository)
        val passViewModel = ViewModelProvider(this, passViewModelFactory).get(PassInfoViewModel::class.java)

        // Popup
        val viewWindow = layoutInflater.inflate(R.layout.popup_window, null)
        val popupText: TextView = viewWindow.findViewById<View>(R.id.popup_count_text) as TextView
        val width = LinearLayout.LayoutParams.WRAP_CONTENT
        val height = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true
        val popupWindow = PopupWindow(viewWindow, width, height, focusable)

        binding.generateNewPassword.setOnClickListener {
            if (binding.AZButton.isChecked || binding.azButton.isChecked ||
                binding.numericButton.isChecked || binding.specialCharButton.isChecked
            ) {
                lifecycleScope.launch {
                    binding.generatedPassword.text = createPassword(
                        binding.AZButton.isChecked,
                        binding.azButton.isChecked,
                        binding.numericButton.isChecked,
                        binding.specialCharButton.isChecked,
                        binding.seekBar.progress,
                        binding.numNumerical.text.toString().toInt(),
                        binding.numSpecial.text.toString().toInt()
                    )
                }
            } else {
                Toast.makeText(requireContext(), "You need to specify at least one type of character to generate the password", Toast.LENGTH_LONG).show()
            }
        }

        binding.exposedPassword.setOnClickListener {
            binding.exposedProgressBar.visibility = View.VISIBLE
            val passToInspect = binding.generatedPassword.text.toString()
            val passSubstring = kekHashSubstring(passToInspect)
            lifecycleScope.launch {
                passViewModel.getPasswordInfo(passSubstring)
            }

            passViewModel.myPasswordResponse.observe(
                viewLifecycleOwner,
                { response ->
                    if (response.isSuccessful) {
                        // Number of times that the password appears in the database
                        val breachesCount = response.body()?.passData?.count.toString()
                        val countInt = breachesCount.toInt()
                        // Information about the password such as number of digits, letters, special characters and length
                        val passInfo = response.body()?.passData?.char.toString()

                        if (countInt in 1..99) {
                            popupMsg += "Your password has appeared in some data breaches, it should be improved. "
                            passInformation(passInfo)
                            popupText.text = popupMsg
                            binding.exposedProgressBar.visibility = View.INVISIBLE
                            popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                            popupMsg = ""
                        } else if (countInt >= 100) {
                            popupMsg += "Your password has been seen $countInt times before!! You must change it now!! "
                            passInformation(passInfo)
                            popupText.text = popupMsg
                            binding.exposedProgressBar.visibility = View.INVISIBLE
                            popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                            popupMsg = ""
                        }
                    } else {
                        popupText.text = "No password breach has been found in the database!!"
                        binding.exposedProgressBar.visibility = View.INVISIBLE
                        popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                        popupMsg = ""
                    }
                }
            )
        }

        binding.moreNumericButtons.setOnClickListener {
            val n = binding.numNumerical.text.toString()
            val nn = n.toInt() + 1
            binding.numNumerical.setText(nn.toString())
        }
        binding.moreSpecialButton.setOnClickListener {
            val n = binding.numSpecial.text.toString()
            val nn = n.toInt() + 1
            binding.numSpecial.setText(nn.toString())
        }

        binding.lessSpecialButton.setOnClickListener {
            val n = binding.numSpecial.text.toString()
            val nn = n.toInt() - 1
            if (nn >= 0) {
                binding.numSpecial.setText(nn.toString())
            } else {
                binding.numNumerical.setText(0.toString())
            }
        }
        binding.lessNumericButtons.setOnClickListener {
            val n = binding.numNumerical.text.toString()
            val nn = n.toInt() - 1
            if (nn >= 0) {
                binding.numNumerical.setText(nn.toString())
            } else {
                binding.numNumerical.setText(0.toString())
            }
        }

        binding.copyGeneratedPassword.setOnClickListener {
            lifecycleScope.launch {
                val pas = binding.generatedPassword.text.toString()
                copyPass(pas)
            }
        }

        return view
    }
    private suspend fun createPassword(
        isCapital: Boolean,
        isLower: Boolean,
        isNumeric: Boolean,
        isSpecial: Boolean,
        length: Int,
        minNumNumeric: Int,
        minNumSpecial: Int
    ): String {
        var password = ""
        if (length <= 0) {
            Toast.makeText(requireContext(), "La contraseña debe tener una longitud mayor a 0", Toast.LENGTH_LONG).show()
        } else if (minNumNumeric >= length) {
            Toast.makeText(
                requireContext(),
                "El número mínimo de caracteres númericos debe ser menor a " +
                    "la longitud de la contraseña a generar",
                Toast.LENGTH_LONG
            ).show()
        } else if (minNumSpecial >= length) {
            Toast.makeText(
                requireContext(),
                "El número mínimo de caracteres especiales debe ser menor a" +
                    " la longitud de la contraseña a generar",
                Toast.LENGTH_LONG
            ).show()
        } else if (minNumNumeric + minNumSpecial >= length) {
            Toast.makeText(
                requireContext(),
                "El número mínimo de caracteres especiales y numéricos " +
                    "debe ser menor que la longitud de la contraseña a generar",
                Toast.LENGTH_LONG
            ).show()
        } else {
            password = generatePassword(isCapital, isLower, isNumeric, isSpecial, length, minNumNumeric, minNumSpecial)
            binding.exposedPassword.visibility = View.VISIBLE
        }
        return password
    }

    private suspend fun copyPass(password: String) {
        val clipboardManager = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", password)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Contraseña copiada", Toast.LENGTH_LONG).show()
    }

    private fun passInformation(str: String) {
        val firstSplit = str.split(';')
        val info = arrayOfNulls<String>(4)
        for ((index, item) in firstSplit.withIndex()) {
            val itemDivide = item.split(':')
            info[index] = itemDivide[1]
            when (index) {
                0 -> { // Dígitos
                    if (info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one digit. "
                    }
                }
                1 -> { // Letras del alfabeto
                    if (info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one letter. "
                    }
                }
                2 -> { // Caracteres especiales
                    if (info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one special character. "
                    }
                }
                3 -> { // Longitud de la contraseña
                    if (info[index]?.toInt()!! < 8) {
                        popupMsg += "The password should be at least 8 characters long. "
                    }
                }
            }
        }
    }
}
