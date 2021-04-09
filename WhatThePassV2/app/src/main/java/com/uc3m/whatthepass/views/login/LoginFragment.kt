package com.uc3m.whatthepass.views.login

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentLoginBinding
import com.uc3m.whatthepass.util.Hash
import com.uc3m.whatthepass.util.PasswordGenerator
import com.uc3m.whatthepass.viewModels.UserViewModel
import com.uc3m.whatthepass.views.passAndFiles.PassAndFilesActivity
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
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        /**************************************************** OAuth*****************************************************/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        Log.d("CURRENT USER", auth.currentUser.uid)
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
        binding.signin.setOnClickListener{
            lifecycleScope.launch{
                insertUser()
            }
        }

        // Comportamiento del botón de inicio de sesión
        binding.login.setOnClickListener{
            lifecycleScope.launch{
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
        if(emailCheck(email)) {
            // Comprobamos si la contraseña cumple con los requisitos
            if(passwordCheck(masterPassword)) {
                /* Registramos al usuario con la función addUser:
                *  Esta función devuelve true si ha conseguido registrarlo.
                *  Devolverá false si el usuario ya existe en la base de datos
                *  */
                val registerFind = withContext(Dispatchers.IO){
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
                Toast.makeText(requireContext(), "Invalid password! It must include lower case and upper case letters, " +
                        "numbers and at least 8 characters long", Toast.LENGTH_LONG).show()
            }
        } else { // Si el campo email no es un email
            Toast.makeText(requireContext(), "The input must be an email", Toast.LENGTH_LONG).show()
        }
    }

    // Función de inicio de sesión
    private suspend fun loginUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()

        // Comprobamos si los campos introducidos coinciden con el de un usuario de la base de datos
        val loginFind = withContext(Dispatchers.IO){
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
                Toast.makeText(requireContext(), "The password is incorrect!", Toast.LENGTH_LONG).show()
                binding.password.text.clear()
            }
            2 -> { // Si el email no existe en la base de datos
              Toast.makeText(requireContext(), "User not registered!", Toast.LENGTH_LONG).show()
              binding.email.text.clear()
              binding.password.text.clear()
            }

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

        val intent = Intent(this@LoginFragment.context, PassAndFilesActivity::class.java)
        activity?.startActivity(intent)
    }

    // Función que comprueba el campo email
    private fun emailCheck(email: String): Boolean {
        return email.isNotEmpty() && Pattern.compile(emailRegex).matcher(email).matches()
    }

    // Función que comprueba que la contraseña cumple X requisitos
    private fun passwordCheck(password: String): Boolean {
        var passValid = true

        if(password.length < 8) {
            passValid = false
        }

        var reg = ".*[0-9].*"
        var pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE)
        var matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[A-Z].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[a-z].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        reg = ".*[~!@#\$%\\^&*()\\-_=+\\|\\[{\\]};:'\",<.>/?].*"
        pattern = Pattern.compile(reg)
        matcher = pattern.matcher(reg)
        if(!matcher.matches()) {
            passValid = false
        }

        return passValid
    }
    /**********************************************************************************************************************/
    /************************************************************FUNCIONES OAUTH*******************************************/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)

                val sp = activity?.getSharedPreferences("Preferences", Context.MODE_PRIVATE) ?: return
                with(sp.edit()) {
                    putString("loginEmail", "Online")//account.email)
                    commit()
                }

                val intent = Intent(this@LoginFragment.context, PassAndFilesActivity::class.java)
                activity?.startActivity(intent)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                Toast.makeText(requireContext(), "Something went wrong when sing in with Google!", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser

                    val database = FirebaseDatabase.getInstance()
                    val myRef = database.getReference("Users/" + user!!.uid + "/masterPass")

                    val masterPassListener = object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // Get Post object and use the values to update the UI
                            val masterPassOnline = dataSnapshot.getValue(String::class.java)

                            if (masterPassOnline == null) {
                                lifecycleScope.launch {
                                    val masterPass = PasswordGenerator.generatePassword(isCapital = true, isLower = true, isNumeric = true, isSpecial = true, length = 42, minNumNumeric = 1, minNumSpecial = 1)
                                    val hashPassword = Hash.bcryptHash(masterPass)
                                    myRef.setValue(hashPassword)
                                }
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Getting Post failed, log a message
                            Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
                        }
                    }
                    myRef.addValueEventListener(masterPassListener)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // [START_EXCLUDE]
                }
            }
    }
    // [END auth_with_google]

    // [START signin]
    private fun signInWithGoogleOauth() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 1
    }
}