package com.example.sophos_mobile_app.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.sophos_mobile_app.databinding.FragmentLoginBinding
import com.example.sophos_mobile_app.utils.Validation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setListeners()
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        val userEmail = binding.etvLoginEmail.text.toString()
        loginViewModel.user.observe(viewLifecycleOwner){ user ->
            val action = LoginFragmentDirections.actionToMenuFragmentDestination(user.name, userEmail)
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
    }

    private fun validatePassword(password: String): Boolean {
        return if (Validation.isFieldEmpty(password)){
            binding.tilLoginPassword.error = "Password can\'t be empty"
            false
        } else{
            binding.tilLoginPassword.error = null
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (Validation.isFieldEmpty(email)){
            binding.tilLoginEmail.error = "Email can\'t be empty"
            false
        } else if (!Validation.isEmailValid(email)){
            binding.tilLoginEmail.error = "Invalid email"
            false
        } else{
            binding.tilLoginEmail.error = null
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}