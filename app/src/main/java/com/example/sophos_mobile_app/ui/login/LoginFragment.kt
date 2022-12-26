package com.example.sophos_mobile_app.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.sophos_mobile_app.R
import com.example.sophos_mobile_app.databinding.FragmentLoginBinding
import com.example.sophos_mobile_app.utils.UserDataStore
import com.example.sophos_mobile_app.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var userDataStore: UserDataStore

    companion object {
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val BIOMETRIC_INTENTION = "biometric_intention"
        const val BIOMETRIC_EMAIL = "biometric_email"
        const val BIOMETRIC_PASSWORD = "biometric_password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBiometricAccess()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        userDataStore = UserDataStore(requireContext()                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      )
        isUserLogged()
        setListeners()
        observeViewModel()
        return binding.root
    }

    private fun isUserLogged() {
        lifecycleScope.launch(Dispatchers.IO) {
            userDataStore.getDataStorePreferences().collect() { userPreferences ->
                println(userPreferences)
                if (userPreferences.email.isNotEmpty()) {
                    withContext(Dispatchers.Main) {
                        val action = LoginFragmentDirections.actionToMenuFragmentDestination(
                            userPreferences.email,
                            userPreferences.name
                        )
                        findNavController().navigate(action)
                    }
                }
            }
        }
    }

    private fun observeViewModel() {
        val userEmail = binding.etvLoginEmail.text.toString()
        val userPassword = binding.etvLoginPassword.text.toString()
        loginViewModel.user.observe(viewLifecycleOwner) { user ->
            lifecycleScope.launch(Dispatchers.IO) {
                userDataStore.getDataStorePreferences()?.collect(){ userPreferences ->
                    if (userPreferences.biometricIntention){
                        userDataStore.saveBiometricData(userEmail,userPassword)
                    }
                    println("UPref when fp is true $userPreferences")
                }
                userDataStore.saveUserInDataStore(userEmail, userPassword, user.name)
            }
            val action =
                LoginFragmentDirections.actionToMenuFragmentDestination(user.name, userEmail)
            findNavController().navigate(action)
        }
    }

    private fun setListeners() {
        binding.btnLoginLogin.setOnClickListener {
            val email = binding.etvLoginEmail.text.toString()
            val password = binding.etvLoginPassword.text.toString()
            validateEmail(email)
            validatePassword(password)
            if (validateEmail(email) && validatePassword(password)) {
                loginViewModel.login(email, password)
            }
        }
        binding.btnLoginFingerprint.setOnClickListener {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    private fun setupBiometricAccess() {
        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    lifecycleScope.launch(Dispatchers.IO){
                        userDataStore.getDataStorePreferences().collect(){ userPreferences ->
                            if (!userPreferences.biometricIntention) userDataStore.setBiometricIntention()
                            if (userPreferences.biometricEmail.isEmpty()){
                                withContext(Dispatchers.Main){
                                    Toast.makeText(
                                        requireContext(),
                                        "You must authenticate with password first", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            else{
                                userDataStore.getDataStorePreferences()?.collect(){ userPreferences ->
                                    loginViewModel.login(userPreferences.biometricEmail,userPreferences.biometricPassword)
                                }
                            }
                        }
                    }
                    Toast.makeText(
                        requireContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.fingerprint_auth))
            .setSubtitle(getString(R.string.use_biometric_as_authentication))
            .setNegativeButtonText(getString(R.string.use_password_auth))
            .build()
    }

    private fun validatePassword(password: String): Boolean {
        return if (Validation.isFieldEmpty(password)) {
            binding.tilLoginPassword.error = getString(R.string.password_not_empy)
            false
        } else {
            binding.tilLoginPassword.error = null
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (Validation.isFieldEmpty(email)) {
            binding.tilLoginEmail.error = getString(R.string.email_not_empty)
            false
        } else if (!Validation.isEmailValid(email)) {
            binding.tilLoginEmail.error = getString(R.string.invalid_email)
            false
        } else {
            binding.tilLoginEmail.error = null
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}