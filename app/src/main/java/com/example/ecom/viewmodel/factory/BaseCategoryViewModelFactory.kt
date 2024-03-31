package com.example.ecom.viewmodel.factory

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecom.data.Category
import com.example.ecom.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactory (
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModelProvider.Factory {

    override fun<T : ViewModel> create(modelClass: Class<T>): T {
       return CategoryViewModel(firestore, category) as T
    }

}