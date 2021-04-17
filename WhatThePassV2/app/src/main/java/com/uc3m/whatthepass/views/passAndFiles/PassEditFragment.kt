package com.uc3m.whatthepass.views.passAndFiles

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uc3m.whatthepass.R
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
    private var passwordID: Long = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userLoginOn: User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPassEditBinding.inflate(inflater, container, false)
        val view = binding.root
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        auth = FirebaseAuth.getInstance()

        // Inicializamos el adapter que se va a encargar de notificar que se han hecho cambios en la lista de contraseñas
        val adapter = ListAdapter(passwordViewModel)

        // Obtenemos el email del usuario logueado
        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
        if (!email.equals("Online")) {
            lifecycleScope.launch {
                val userLogin: User? = email?.let { userViewModel.findUserByEmail(it) }
                // Si no se ha encontrado al usuario logueado, error
                if (userLogin == null) {
                    Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
                    exitProcess(-1)
                } else {

                    // Guardar los cambios de una entrada
                    binding.saveChangesButton.setOnClickListener { v ->
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                        imm?.hideSoftInputFromWindow(v.windowToken, 0)
                        if (email != null) {
                            editPassword(email, userLogin.masterPass)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            // Función de visibilidad de la contraseña
            binding.viewButton3.setOnClickListener {
                val passInputType = binding.passwordDetailInput2.inputType
                if (passInputType == 129) {
                    binding.passwordDetailInput2.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    binding.passwordDetailInput2.inputType = 129
                }
            }
        } else if (email.equals("Online")) {
            database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users/" + auth.currentUser.uid + "/masterPass")
            val masterPassListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val masterPassOnline = dataSnapshot.getValue(String::class.java)
                    if (masterPassOnline != null) {
                        val passId = passwordViewModel.message.value?.id
                        if (passId != null) {
                            binding.saveChangesButton.setOnClickListener { v ->
                                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.hideSoftInputFromWindow(v.windowToken, 0)
                                if (email != null) {
                                    editPasswordOnline(auth.currentUser.email, masterPassOnline, passId)
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            myRef.addValueEventListener(masterPassListener)
            // Función de visibilidad de la contraseña
            binding.viewButton3.setOnClickListener {
                val passInputType = binding.passwordDetailInput2.inputType
                if (passInputType == 129) {
                    binding.passwordDetailInput2.inputType = InputType.TYPE_CLASS_TEXT
                } else {
                    binding.passwordDetailInput2.inputType = 129
                }
            }
        } else {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            return view
        }

        return view
    }

    private fun editPasswordOnline(email: String, masterPass: String, idPass: Long) {
        val inputTitle = binding.titleDetail2.text.toString()
        val inputEmail = binding.emailDetail2.text.toString()
        val inputUsername = binding.usernameDetail2.text.toString()
        val inputPassword = binding.passwordDetailInput2.text.toString()
        val inputURL = binding.URIDetail2.text.toString()
        // database= FirebaseDatabase.getInstance()
        when (checkInputs(inputTitle, inputPassword, inputEmail)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> Toast.makeText(requireContext(), "It is not an email!", Toast.LENGTH_LONG).show()
            4 -> {
                userLoginOn = User(email, masterPass)
                val myRef = database.getReference("Users/" + auth.currentUser.uid + "/passwords/" + idPass)
                val en = Hash.encrypt(inputPassword, masterPass)
                val p = Password(idPass, inputTitle, auth.currentUser.email, inputEmail, inputUsername, en, inputURL)
                myRef.setValue(p)
                Toast.makeText(requireContext(), "Password updated!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_passEditFragment_to_passwordView)
            }
        }
    }

    private fun editPassword(email: String, masterPass: String) {
        val inputTitle = binding.titleDetail2.text.toString()
        val inputEmail = binding.emailDetail2.text.toString()
        val inputUsername = binding.usernameDetail2.text.toString()
        val inputPassword = binding.passwordDetailInput2.text.toString()
        val inputURL = binding.URIDetail2.text.toString()

        when (checkInputs(inputTitle, inputPassword, inputEmail)) {
            1 -> Toast.makeText(requireContext(), "Title field must be filled", Toast.LENGTH_LONG).show()
            2 -> Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            3 -> Toast.makeText(requireContext(), "It is not an email!", Toast.LENGTH_LONG).show()
            4 -> {
                passwordViewModel.updatePassword(passwordID, inputTitle, email, inputEmail, inputUsername, inputPassword, inputURL, masterPass)
                Toast.makeText(requireContext(), "Password updated!", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_passEditFragment_to_passwordView)
            }
        }
    }

    private fun insertFields(email: String, password: Password) {
        binding.titleDetail2.setText(password.name)
        binding.emailDetail2.setText(password.inputEmail)
        binding.usernameDetail2.setText(password.inputUser)
        if (email != "Online") {
            lifecycleScope.launch {
                val userLogin: User? = userViewModel.findUserByEmail(email)
                if (userLogin != null) {
                    val realPass = Hash.decrypt(password.hashPassword, userLogin.masterPass)
                    binding.passwordDetailInput2.setText(realPass)
                } else {
                    Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG)
                        .show()
                    exitProcess(-1)
                }
            }
        } else {
            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("Users/" + auth.currentUser.uid + "/masterPass")
            val masterPassListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val masterPassOnline = dataSnapshot.getValue(String::class.java)
                    if (masterPassOnline != null) {
                        lifecycleScope.launch {
                            val realPass = Hash.decrypt(password.hashPassword, masterPassOnline)
                            binding.passwordDetailInput2.setText(realPass)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                }
            }
            myRef.addValueEventListener(masterPassListener)
        }
        binding.URIDetail2.setText(password.url)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sp = requireActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE)
        val email = sp.getString("loginEmail", null)
        lateinit var pass: Password
        if (email != null) {
            passwordViewModel.message.observe(
                viewLifecycleOwner,
                object : Observer<Password> {
                    override fun onChanged(o: Password?) {
                        if (o != null) {
                            pass = o
                            passwordID = o.id
                            insertFields(email, pass)
                        }
                    }
                }
            )
        } else {
            Toast.makeText(requireContext(), "An error has occurred!", Toast.LENGTH_LONG).show()
            exitProcess(-1)
        }

        binding.cancelChangesButton.setOnClickListener {
            passwordViewModel.sentPassword(pass)
            findNavController().navigate(R.id.action_passEditFragment_to_passDetailFragment)
        }
    }

    private fun checkInputs(title: String, pass: String, email: String): Int {
        return when {
            title.isEmpty() -> {
                1
            }
            pass.isEmpty() -> {
                2
            }
            email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                3
            }
            else -> {
                4
            }
        }
    }
}
