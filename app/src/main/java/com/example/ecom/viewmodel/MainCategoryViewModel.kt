package com.example.ecom.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecom.data.Product
import com.example.ecom.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealProducts: StateFlow<Resource<List<Product>>> = _bestDealProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts: StateFlow<Resource<List<Product>>> = _bestProducts


    init {
        fetchSpecialProducts()
        fetchBestDealProducts()
        fetchBestProducts()
    }

    private fun fetchBestProducts() {
        viewModelScope.launch {
            _bestProducts.emit(Resource.Loading())
        }

        firestore.collection("products").get()
            .addOnSuccessListener { result ->
                val bestProducts = result.toObjects(Product:: class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Success(bestProducts))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun fetchBestDealProducts() {
        viewModelScope.launch {
            _bestDealProducts.emit(Resource.Loading())
        }

        firestore.collection("products").get()
            .addOnSuccessListener {result ->
                val bestDealProducts = result.toObjects(Product:: class.java)
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Success(bestDealProducts))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _bestDealProducts.emit(Resource.Error(it.message.toString()))
                }
            }

    }


    private fun fetchSpecialProducts() {

        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection("products")
            .whereEqualTo("category", "Special Products").get()
            .addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }

            }.addOnFailureListener{
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}