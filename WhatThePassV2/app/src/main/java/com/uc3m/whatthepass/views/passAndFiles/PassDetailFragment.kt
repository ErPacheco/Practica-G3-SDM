package com.uc3m.whatthepass.views.passAndFiles

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
    private var masterPassOnline: String? = ""
    private val passwordViewModel: PasswordViewModel by activityViewModels()
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        auth = FirebaseAuth.getInstance()
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

        // Cuando toquemos la pantalla, el popup desaparecerá
        viewWindow.setOnTouchListener({ _, _ ->
            popupWindow.dismiss()
            true
        })

        // Sombra del popup
        popupWindow.elevation = 20F

        // Cambio de visibilidad de la contraseña
        binding.viewButton.setOnClickListener {
            val passInputType = binding.passwordDetailInput.inputType
            if (passInputType == 129) {
                binding.passwordDetailInput.inputType = InputType.TYPE_CLASS_TEXT
            } else {
                binding.passwordDetailInput.inputType = 129
            }
        }

        // Botón de copiar contraseña
        binding.copyButton.setOnClickListener {
            val passToCopy = binding.passwordDetailInput.text

            val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", passToCopy)
            clipboardManager.setPrimaryClip(clipData)

            Toast.makeText(requireContext(), "Password copied to clipboard!", Toast.LENGTH_LONG).show()
        }

        // Botón de consumo de la api XposedOrNot
        binding.passBreaches.setOnClickListener {
            binding.progressBarAPI.visibility = View.VISIBLE
            val passToInspect = binding.passwordDetailInput.text.toString()
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
                }
            )
        }

        return view
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

    // Función para insertar los datos de la contraseña en los campos de la vista
    private fun insertFields(email: String, password: Password) {
        binding.titleDetail.text = password.name
        binding.emailDetail.text = password.inputEmail
        binding.usernameDetail.text = password.inputUser

        // Para mostrar la contraseña de la entrada, necesitamos desencriptarla de la base de datos
        lifecycleScope.launch {
            // Para ello, buscamos el usuario que está logueado en la base de datos para obtener su contraseña maestra
            val userLogin: User? = userViewModel.findUserByEmail(email)
            if (userLogin != null) { // Una vez encontrada la masterPass del usuario logueado, lo desencriptamos para mostrarla en los detalles
                val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
                binding.passwordDetailInput.text = realPass
            } else { // Si por algun casual no encontramos en la base de datos el usuario logueado, error
                Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
                exitProcess(-1)
            }
        }
        binding.URIDetail.text = password.url
    }

    private fun insertFieldsOnline(password: Password) {
        binding.titleDetail.text = password.name
        binding.emailDetail.text = password.inputEmail
        binding.usernameDetail.text = password.inputUser
        database = FirebaseDatabase.getInstance()
        var uid = auth.currentUser.uid
        val myRef = database.getReference("Users/$uid/masterPass")
        val masterPassListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                masterPassOnline = dataSnapshot.getValue(String::class.java)
                if (masterPassOnline != null) {
                    val realPass = Hash.decrypt(password.hashPassword, masterPassOnline!!)
                    binding.passwordDetailInput.text = realPass
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        myRef.addValueEventListener(masterPassListener)

        binding.URIDetail.text = password.url
    }

    // Cuando la view se haya creado
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtenemos el email del usuario logueado
        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)

        lateinit var actualPassword: Password
        if (email != null) {
            // A través de un MutableData del viewModel de Password, obtenemos la contraseña que vamos a ver en detalle
            passwordViewModel.message.observe(
                viewLifecycleOwner,
                { o ->
                    if (o != null) {
                        actualPassword = o
                        // Procedemos a insertar los datos en los campos de la entrada de la contraseña
                        if (email != "Online") {
                            insertFields(email, o)
                        } else {
                            insertFieldsOnline(o)
                        }
                    } else {
                        // Si por algún casual no obtuviéramos la contraseña a ver, es que ha ocurrido un error interno
                        Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
                        exitProcess(-1)
                    }
                }
            )

            // Comportamiento del botón de editar la entrada
            binding.editPasswordButton.setOnClickListener {
                passwordViewModel.sentPassword(actualPassword)
                findNavController().navigate(R.id.action_passDetailFragment_to_passEditFragment)
            }
        } else {
            // Si por algún casual no obtuviéramos el email logueado, es que ha ocurrido un error interno
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }
    }
}
