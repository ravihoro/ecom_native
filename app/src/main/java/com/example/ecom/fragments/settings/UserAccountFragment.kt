package com.example.ecom.fragments.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecom.R
import com.example.ecom.data.User
import com.example.ecom.databinding.FragmentBillingBinding
import com.example.ecom.databinding.FragmentUserAccountBinding
import com.example.ecom.dialog.setupBottomSheetDialog
import com.example.ecom.util.Resource
import com.example.ecom.viewmodel.UserAccountViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserAccountFragment: Fragment(R.layout.fragment_user_account) {

    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>
    private var imageUrl: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            imageUrl = it.data?.data
            Glide.with(this).load(imageUrl).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
    binding = FragmentUserAccountBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collectLatest {
                    when(it) {
                        is Resource.Error -> {
                            hideUserLoading()
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            showUserLoading()
                        }
                        is Resource.Success -> {
                            hideUserLoading()
                            showUserInformation(it.data!!)
                        }
                        else -> Unit
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editInfo.collectLatest {
                    when(it){
                        is Resource.Error -> {
                            binding.buttonSave.revertAnimation()
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            binding.buttonSave.startAnimation()
                        }
                        is Resource.Success -> {
                            binding.buttonSave.revertAnimation()
                            findNavController().navigateUp()
                        }
                        else -> Unit
                    }
                }
            }
        }

        binding.tvUpdatePassword.setOnClickListener{
            setupBottomSheetDialog {  }
        }


        binding.buttonSave.setOnClickListener{
            binding.apply {
                val firstName = edFirstName.toString().trim()
                val lastName = edLastName.toString().trim()
                val email = edEmail.toString().trim()
                val user = User(firstName, lastName, email)
                viewModel.updateUser(user, imageUrl)
            }
        }

        binding.imageEdit.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }
    }

    private fun showUserInformation(user: User) {
        binding.apply {
            Glide.with(this@UserAccountFragment).load(user.imagePath)
                .error(ColorDrawable(Color.BLACK)).into(imageUser)
            edFirstName.setText(user.firstName)
            edLastName.setText(user.lastName)
            edEmail.setText(user.email)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edFirstName.visibility = View.VISIBLE
            edLastName.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            tvUpdatePassword.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

    private fun showUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.VISIBLE
            imageUser.visibility = View.INVISIBLE
            imageEdit.visibility = View.INVISIBLE
            edFirstName.visibility = View.INVISIBLE
            edLastName.visibility = View.INVISIBLE
            edEmail.visibility = View.INVISIBLE
            tvUpdatePassword.visibility = View.INVISIBLE
            buttonSave.visibility = View.INVISIBLE
        }
    }

}