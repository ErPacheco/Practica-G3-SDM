package com.uc3m.whatthepass.views.passAndFiles

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentPassDetailBinding
import com.uc3m.whatthepass.models.Password
import com.uc3m.whatthepass.models.User
import com.uc3m.whatthepass.passwordApi.PassInfoViewModel
import com.uc3m.whatthepass.passwordApi.PassInfoViewModelFactory
import com.uc3m.whatthepass.passwordApi.repository.Repository
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.Hash.kekHashSubstring
import com.uc3m.whatthepass.viewModels.PasswordViewModel
import com.uc3m.whatthepass.viewModels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


class PassDetailFragment : Fragment() {
    private lateinit var binding: FragmentPassDetailBinding
    private lateinit var userViewModel: UserViewModel
    private var popupMsg: String = ""
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private val passwordViewModel: PasswordViewModel by activityViewModels()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreateView(
      inflater: LayoutInflater, container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        auth= FirebaseAuth.getInstance()

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

        viewWindow.setOnTouchListener(OnTouchListener { _, _ ->
            popupWindow.dismiss()
            true
        })

        popupWindow.elevation = 20F;

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
            lifecycleScope.launch {
                passViewModel.getPasswordInfo(passSubstring)
            }

            passViewModel.myPasswordResponse.observe(viewLifecycleOwner, Observer { response ->
                if (response.isSuccessful) {

                    val breachesCount = response.body()?.passData?.count.toString()
                    val countInt = breachesCount.toInt()

                    val passInfo = response.body()?.passData?.char.toString()

                    if (countInt in 1..99) {
                        popupMsg += "Your password has appeared in some data breaches, it should be improved. "
                        passInformation(passInfo)
                        popupText.text = popupMsg
                        binding.progressBarAPI.visibility = View.INVISIBLE
                        popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                        popupMsg = ""
                    } else if (countInt >= 100) {
                        popupMsg += "Your password has been seen $countInt times before!! You must change it now!! "
                        passInformation(passInfo)
                        popupText.text = popupMsg
                        binding.progressBarAPI.visibility = View.INVISIBLE
                        popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                        popupMsg = ""
                    }
                } else {
                    popupText.text = "No password breach has been found in the database!!"
                    binding.progressBarAPI.visibility = View.INVISIBLE
                    popupWindow.showAtLocation(viewWindow, Gravity.CENTER, 0, 0)
                    popupMsg = ""
                }
            })
        }

        return view
    }

    private fun passInformation(str: String) {
        val firstSplit = str.split(';')
        val info = arrayOfNulls<String>(4)
        for((index, item) in firstSplit.withIndex()) {
            val itemDivide = item.split(':')
            info[index] = itemDivide[1]
            Log.d("ITERACION: ", index.toString())
            when(index) {
                0 -> { // Dígitos
                    if(info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one digit. "
                    }
                }
                1 -> { // Letras del alfabeto
                    if(info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one letter. "
                    }
                }
                2 -> { // Caracteres especiales
                    if(info[index] == "0") {
                        popupMsg += "Is it recommended to add at least one special character. "
                    }
                }
                3 -> { // Longitud de la contraseña
                    if(info[index]?.toInt()!! < 8) {
                        popupMsg += "The password should be at least 8 characters long. "
                    }
                }
            }
        }
    }

    private fun insertFields(email: String, password: Password) {
        binding.titleDetail.setText(password.name)
        binding.emailDetail.setText(password.inputEmail)
        binding.usernameDetail.setText(password.inputUser)
        lateinit var userLogin: User
        lifecycleScope.launch{
            userLogin = userViewModel.findUserByEmail(email)
            val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
            binding.passwordDetailInput.setText(realPass)
        }
        binding.URIDetail.setText(password.url)
    }

    private fun insertFieldsOnline(email: String, password: Password) {
        binding.titleDetail.setText(password.name)
        binding.emailDetail.setText(password.inputEmail)
        binding.usernameDetail.setText( password.inputUser)
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/" + auth.currentUser.uid + "/masterPass")

            //val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
            //binding.passwordDetailInput.setText(realPass)

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
                    if (o != null) {
                        actualPassword = o
                        if (auth.currentUser == null) {
                            insertFields(email, o)
                        }else{
                            insertFieldsOnline(email,o)
                        }
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