package com.example.ecom.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.ecom.R
import com.example.ecom.data.Address
import com.example.ecom.databinding.FragmentAddressBinding
import com.example.ecom.util.Resource
import com.example.ecom.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddressFragment: Fragment(R.layout.fragment_address) {

    private lateinit var binding: FragmentAddressBinding

    val viewModel by viewModels<AddressViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.addNewAddress.collectLatest {
                    when(it) {
                        is Resource.Error -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                        is Resource.Loading -> {
                            binding.progressbarAddress.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarAddress.visibility = View.INVISIBLE
                            findNavController().navigateUp()

                        }
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.error.collectLatest {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener{
                val addressTitle = edAddressTitle.text.toString().trim()
                val city = edCity.text.toString().trim()
                val fullName = edFullName.toString().trim()
                val state = edState.text.toString().trim()
                val phone = edPhone.text.toString().trim()
                val street = edStreet.text.toString().trim()

                val address = Address(addressTitle, fullName, street, phone, city, state)

                viewModel.addAddress(address)
            }
        }





    }

}