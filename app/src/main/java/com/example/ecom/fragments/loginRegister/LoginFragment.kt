package com.example.ecom.fragments.loginRegister

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ecom.R
import com.example.ecom.activities.ShoppingActivity
import com.example.ecom.databinding.FragmentLoginBinding
import com.example.ecom.dialog.setupBottomSheetDialog
import com.example.ecom.util.Resource
import com.example.ecom.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


private const val TAG = "LoginFragment"
@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.tvForgotPasswordLogin.setOnClickListener{
            setupBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        binding.apply {
            buttonLoginLogin.setOnClickListener{
                val email = edEmailLogin.text.toString().trim()
                val password = edPasswordLogin.text.toString()
                viewModel.login(email, password)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resetPassword.collect {
                    when(it) {
                        is Resource.Error -> {
                            Snackbar.make(requireView(), "Error: ${it.message}", Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Loading -> {

                        }
                        is Resource.Success -> {
                            Snackbar.make(requireView(), "Reset link sent to email", Snackbar.LENGTH_LONG).show()
                        }
                        else -> Unit
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.login.collect{
                    when(it) {
                        is Resource.Error -> {
                            Log.e("error", it.data.toString())
                            Toast.makeText(requireContext(), "it.message", Toast.LENGTH_SHORT).show()
                            binding.buttonLoginLogin.revertAnimation()
                        }
                        is Resource.Loading -> {
                            binding.buttonLoginLogin.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonLoginLogin.revertAnimation()
                            Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }


    }


}