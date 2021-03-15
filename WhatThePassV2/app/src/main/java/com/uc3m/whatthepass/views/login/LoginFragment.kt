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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uc3m.whatthepass.R
import com.uc3m.whatthepass.databinding.FragmentLoginBinding
import com.uc3m.whatthepass.viewModels.UserViewModel
import com.uc3m.whatthepass.views.passAndFiles.PassAndFilesActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var userViewModel: UserViewModel
    private val EMAILREGEX = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$"
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        /**************************************************** OAuth*****************************************************/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        /***************************************************Fin OAuth***************************************************/
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.signinGoogle.setOnClickListener {
            signInWithGoogleOauth()
        }
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.signin.setOnClickListener{
            lifecycleScope.launch{
                insertUser()
            }
        }

        binding.login.setOnClickListener{
            lifecycleScope.launch{
                loginUser()
            }
        }

        return view
    }

    /************************************************Inicio de sesión y registro local**************************************/
    private suspend fun insertUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()

        if(emailCheck(email)) {
            if(passwordCheck(masterPassword)) {
                val registerFind = withContext(Dispatchers.IO){
                    userViewModel.addUser(email, masterPassword)
                }
                if (registerFind) {
                    loginView(email)
                    Toast.makeText(requireContext(), "User created!", Toast.LENGTH_LONG).show()
                    binding.email.text.clear()
                    binding.password.text.clear()
                } else {
                    Toast.makeText(requireContext(), "The email is already registered!", Toast.LENGTH_LONG).show()
                    binding.email.text.clear()
                    binding.password.text.clear()
                }

            } else {
                Toast.makeText(requireContext(), "La contraseña está mal introducida! Tiene que incluir minúsculas, " +
                        "mayúsculas y números, con una longitud mínima de 8 caracteres", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(requireContext(), "The input must be an email", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun loginUser() {
        val email = binding.email.text.toString()
        val masterPassword = binding.password.text.toString()
        val loginFind = withContext(Dispatchers.IO){
            userViewModel.loginUser(email, masterPassword)
        }

        if(loginFind) {
            loginView(email)
            Toast.makeText(requireContext(), "Successful login!", Toast.LENGTH_LONG).show()
            binding.email.text.clear()
            binding.password.text.clear()
        } else {
           
            Toast.makeText(requireContext(), "User not registered!", Toast.LENGTH_LONG).show()
            binding.email.text.clear()
            binding.password.text.clear()
        }
    }

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

    private fun emailCheck(email: String): Boolean {
        return email.isNotEmpty() && Pattern.compile(EMAILREGEX).matcher(email).matches()
    }

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
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                // ...
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
        private const val RC_SIGN_IN = 9001
    }
}