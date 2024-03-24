package com.example.ecom.fragments.loginRegister

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ecom.R
import com.example.ecom.data.User
import com.example.ecom.databinding.FragmentRegisterBinding
import com.example.ecom.util.RegisterValidation
import com.example.ecom.util.Resource
import com.example.ecom.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment: Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoYouHaveAnAccount.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener{
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim(),
                )
                val password = edPasswordRegister.text.toString()

                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.register.collect{
                    when(it){
                        is Resource.Loading -> {
                            binding.buttonRegisterRegister.startAnimation()
                        }
                        is Resource.Error -> {
                            Log.e("error", it.data.toString())
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        is Resource.Success -> {
                            Log.d(TAG, it.message.toString())
                            binding.buttonRegisterRegister.revertAnimation()
                        }
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.validation.collect{ validation ->
                    if(validation.firstName is RegisterValidation.Failed){
                        withContext(Dispatchers.Main){
                            binding.edFirstNameRegister.apply {
                                requestFocus()
                                error = validation.firstName.message
                            }
                        }
                    }

                    if(validation.lastName is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edLastNameRegister.apply {
                                requestFocus()
                                error = validation.lastName.message
                            }
                        }
                    }


                    if(validation.email is RegisterValidation.Failed){
                        withContext(Dispatchers.Main) {
                            binding.edEmailRegister.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                        }
                    }


                    if(validation.password is RegisterValidation.Failed) {
                        withContext(Dispatchers.Main) {
                            binding.edPasswordRegister.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                        }
                    }
                }
            }
        }


    }
}