package com.example.ecom.fragments.shopping

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import com.bumptech.glide.Glide
import com.example.ecom.R
import com.example.ecom.activities.LoginRegisterActivity
import com.example.ecom.databinding.FragmentProfileBinding
import com.example.ecom.util.Resource
import com.example.ecom.util.showBottomNavigationView
import com.example.ecom.viewmodel.ProfileViewModel
import com.google.firebase.database.collection.BuildConfig
import com.google.firebase.database.collection.LLRBNode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root

    }

    @SuppressLint("RestrictedApi", "UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
        }

        binding.linearAllOrders.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }

        binding.linearBilling.setOnClickListener{
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray())
            findNavController().navigate(action)
        }

        binding.linearLogOut.setOnClickListener{
            viewModel.logout()
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest {
                    when(it) {
                        is Resource.Error -> {
                            binding.progressbarSettings.visibility = View.GONE
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            binding.progressbarSettings.visibility = View.VISIBLE
                        }
                        is Resource.Success -> {
                            binding.progressbarSettings.visibility = View.GONE
                            Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(
                                Color.BLACK)).into(binding.imageUser)

                            binding.tvUserName.text = "${it.data.firstName} ${it.data.lastName}"

                        }
                        else -> Unit
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }


}