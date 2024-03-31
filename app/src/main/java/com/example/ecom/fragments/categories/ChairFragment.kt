package com.example.ecom.fragments.categories

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ecom.data.Category
import com.example.ecom.util.Resource
import com.example.ecom.viewmodel.CategoryViewModel
import com.example.ecom.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChairFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.Chair)
    }

    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.offerProducts.collectLatest {
                    when(it) {
                        is Resource.Error -> {
                            hideOfferLoading()
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Loading -> {
                            showOfferLoading()
                        }
                        is Resource.Success -> {
                            offerAdapter.differ.submitList(it.data)
                            hideOfferLoading()
                        }
                        is Resource.Unspecified ->{
                            Unit
                        }
                    }
                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bestProducts.collectLatest {
                    when(it) {
                        is Resource.Error -> {
                            hideBestProductsLoading()
                            Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                        }
                        is Resource.Loading -> {
                            showBestProductsLoading()
                        }
                        is Resource.Success -> {
                            hideBestProductsLoading()
                            bestProductsAdapter.differ.submitList(it.data)
                        }
                        is Resource.Unspecified -> {
                            Unit
                        }
                    }
                }
            }
        }



    }

}