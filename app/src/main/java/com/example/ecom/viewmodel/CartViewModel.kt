package com.example.ecom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecom.data.CartProduct
import com.example.ecom.firebase.FirebaseCommon
import com.example.ecom.helper.getProductPrice
import com.example.ecom.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon,
): ViewModel() {

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    var cartProducts = _cartProducts.asStateFlow()

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    val productsPrice = cartProducts.map {
        when(it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }else -> null
        }
    }

    private fun calculatePrice(data: List<CartProduct>): Float? {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price) * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    init {
        getCartProducts()
    }

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        val documentId = cartProductDocuments[index!!].id
        firestore.collection("user")
            .document(auth.uid!!).collection("cart")
            .document(documentId).delete()
    }

    private fun getCartProducts() {
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener{ value, error ->
                if(error != null || value == null) {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                }else{
                    cartProductDocuments = value.documents
                    val product = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Success(product))
                    }
                }
        }

    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging,
    ) {



        val index = cartProducts.value.data?.indexOf(cartProduct)

        if(index == null && index == -1) {
            Log.e("Error", "Index not found")
            viewModelScope.launch {
                _cartProducts.emit(Resource.Error("Product not found"))
            }
        }else{
            val documentId = cartProductDocuments[index!!].id
            when(quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    increaseQuantity(documentId)
                }
                else -> {
                    if(cartProduct.quantity == 1) {
                        viewModelScope.launch {
                            _deleteDialog.emit(cartProduct)
                        }
                        return
                    }

                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Loading())
                    }
                    decreaseQuantity(documentId)
                }
            }
        }


    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId) {result, exception ->
            if(exception != null){
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){result, exception ->
            if(exception != null) {
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
            }
        }
    }


}