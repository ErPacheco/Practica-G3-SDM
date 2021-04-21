package com.uc3m.whatthepass.views.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentLoginBinding
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.PasswordGenerator
import com.uc3m.whatthepass.viewModels.UserViewModel
import com.uc3m.whatthepass.views.splashScreen.SplashScreenActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel
    private val emailRegex = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /**************************************************** OAuth*****************************************************/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this.requireActivity(), gso)
        googleSignInClient.revokeAccess()

        auth = FirebaseAuth.getInstance()

        /***************************************************Fin OAuth***************************************************/
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root

        // Comportamiento del botón de sign in de google
        binding.signinGoogle.setOnClickListener {
            signInWithGoogleOauth()
        }

        // Uso del view model de User
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Comportamiento del botón de registro
        binding.signin.setOnClickListener {
            lifecycleScope.launch {
                insertUser()
            }
        }

        // Comportamiento del botón de inicio de sesión
        binding.login.setOnClickListener {
            lifecycleScope.launch {
                loginUser()
            }
        }

        return view
    }

    /************************************************Inicio de sesión y registro local**************************************/
    // Función de registro
    private suspend fun insertUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()

        // Comprobamos si el campo introducido es un email
        if (emailCheck(email)) {
            // Comprobamos si la contraseña cumple con los requisitos
            if (masterPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Password field must be filled", Toast.LENGTH_LONG).show()
            } else {
                if (passwordCheck(masterPassword)) {
                    /* Registramos al usuario con la función addUser:
                    *  Esta función devuelve true si ha conseguido registrarlo.
                    *  Devolverá false si el usuario ya existe en la base de datos
                    *  */
                    val registerFind = withContext(Dispatchers.IO) {
                        userViewModel.addUser(email, masterPassword)
                    }

                    // Si el usuario se ha registrado correctamente
                    if (registerFind) {
                        loginView(email)
                        Toast.makeText(requireContext(), "User created!", Toast.LENGTH_LONG).show()
                    } else { // Si el usuario ya existe en la base de datos, es decir, no se ha registrado correctamente
                        Toast.makeText(requireContext(), "The email is already registered!", Toast.LENGTH_LONG).show()
                    }

                    binding.email.text.clear()
                    binding.password.text.clear()
                } else { // Si no cumple con los requisitos
                    Toast.makeText(
                        requireContext(),
                        "Invalid password! It must include lower case and upper case letters, " +
                            "numbers and at least 8 characters long",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else { // Si el campo email no es un email
            Toast.makeText(requireContext(), "The input must be an email", Toast.LENGTH_LONG).show()
        }
    }

    // Función de inicio de sesión
    private suspend fun loginUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()
        if (!TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

            // Comprobamos si los campos introducidos coinciden con el de un usuario de la base de datos
            val loginFind = withContext(Dispatchers.IO) {
                userViewModel.loginUser(email, masterPassword)
            }

            when (loginFind) {
                0 -> { // Si el email existe y la contraseña es correcta
                    loginView(email)
                    Toast.makeText(requireContext(), "Successful login!", Toast.LENGTH_LONG).show()
                    binding.email.text.clear()
                    binding.password.text.clear()
                }
                1 -> { // Si el email existe pero la contraseña no es correcta
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "The password is incorrect!", Toast.LENGTH_LONG).show()
                    }
                    binding.password.text.clear()
                }
                2 -> { // Si el email no existe en la base de datos
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "User not registered!", Toast.LENGTH_LONG).show()
                    }
                    binding.email.text.clear()
                    binding.password.text.clear()
                }
                3 -> { // Si el email no existe en la base de datos
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(), "Fill all the fields", Toast.LENGTH_LONG).show()
                    }
                    binding.email.text.clear()
                    binding.password.text.clear()
                }
            }
        } else {
            Toast.makeText(requireContext(), "This is not an email!", Toast.LENGTH_LONG).show()
        }
    }

    // Función que guarda como almacenamiento clave/valor el email que está logueado y nos envía a la siguiente actividad
    private fun loginView(email: String) {
        // Save preferences
        val sp = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE) ?: return
        with(sp.edit()) {
            putString("loginEmail", email)
            commit()
        }

        val intent = Intent(this@LoginFragment.context, SplashScreenActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
    }

    // Función que comprueba el campo email
    private fun emailCheck(email: String): Boolean {
        return email.isNotEmpty() && Pattern.compile(emailRegex).matcher(email).matches()
    }

    // Función que comprueba que la contraseña cumple X requisitos
    private fun passwordCheck(password: String): Boolean {
        var passValid = true

        if (password.length < 8) {
            passValid = false
        }

        var reg = ".*[0-9].*".toRegex()
        if (!password.matches(reg)) {
            passValid = false
        }

        reg = ".*[A-Z].*".toRegex()
        if (!password.matches(reg)) {
            passValid = false
        }

        reg = ".*[a-z].*".toRegex()
        if (!password.matches(reg)) {
            passValid = false
        }

        reg = ".*[~!@#\$%^&*()\\-_=+|\\[{\\]};:'\",<.>/?].*".toRegex()
        if (!password.matches(reg)) {
            passValid = false
        }

        return passValid
    }
    /**********************************************************************************************************************/
    /************************************************************FUNCIONES OAUTH*******************************************/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Resultado devuelto por el Intent ejecutado de GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // El proceso de google sigin ha sido satisfactorio, por lo que nos autenticamos con Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle")
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // El proceso de google sigin ha fallado
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Something went wrong when sing in with Google!", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función para el proceso de autenticacion por Firebase
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Ha ido bien el registro/inicio de sesion
                    Log.d(TAG, "signInWithCredential:success")
                    // Inicio en Firebase
                    val user = FirebaseAuth.getInstance().currentUser
                    val database = FirebaseDatabase.getInstance() // Instancia de la BBDD de Firebase

                    // Referencia de la contraseña maestra del usuario
                    val myRef = database.getReference("Users/" + user!!.uid + "/masterPass")
                    myRef.get().addOnSuccessListener {
                        // Si existe contraseña maestra, no hace nada
                        // Sin embargo, si no existe se crea una contraseña maestra
                        val masterPassOnline = it.getValue(String::class.java)
                        if (masterPassOnline == null) {
                            lifecycleScope.launch {
                                val masterPass = PasswordGenerator.generatePassword(isCapital = true, isLower = true, isNumeric = true, isSpecial = true, length = 42, minNumNumeric = 1, minNumSpecial = 1)
                                val hashPassword = Hash.bcryptHash(masterPass)
                                myRef.setValue(hashPassword)
                            }
                        }
                    }

                    // En caso de que se haya logueado con éxito, cambiamos a la vista de contraseñas
                    val sp = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE)
                    if (sp != null) {
                        with(sp.edit()) {
                            putString("loginEmail", "Online")
                            commit()
                        }
                    }

                    val intent = Intent(requireContext(), SplashScreenActivity::class.java)
                    activity?.startActivity(intent)
                    activity?.finish()
                } else {
                    // Si el proceso de registro/inicio sesion falla, mostramos un mensaje
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    // Comienzo del inicio de sesión por Google
    private fun signInWithGoogleOauth() {
        val signInIntent = googleSignInClient.signInIntent

        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 1
    }
}
