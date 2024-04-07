package com.example.ecom.viewmodel

import android.os.Debug
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecom.data.User
import com.example.ecom.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
) :ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }


    fun getUser() {
        viewModelScope.launch { _user.emit(Resource.Loading()) }


        Log.e("Firestore", auth.uid!!)

        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("Firestore", "Error retrieving user data: ${error.message}")
                    viewModelScope.launch { _user.emit(Resource.Error(error?.message.toString())) }
                } else {
                    if (value == null) {
                        Log.d("Firestore", "Document does not exist for user: ${auth.uid}")
                    } else {
                        val user = value.toObject(User::class.java)
                        if (user == null) {
                            Log.d("Firestore", "User is null")
                        } else {
                            viewModelScope.launch { _user.emit(Resource.Success(user)) }
                            Log.d("Firestore", "User data retrieved: $user")
                        }
                    }
                }
            }
//        firestore.collection("user").document(auth.uid!!)
//            .addSnapshotListener{ value, error ->
//                if(error != null){
//                    Log.e("FirestoreError", "Error retrieving user data: ${error.message}")
//                    viewModelScope.launch { _user.emit(Resource.Error(error?.message.toString())) }
//                }else{
//                    val user = value?.toObject(User::class.java)
//                    user?.let {
//                        viewModelScope.launch { _user.emit(Resource.Success(user)) }
//                        Log.d("FirestoreSuccess", "User data retrieved: $user")
//                    }
//                }
//        }
    }

    fun logout() {
        auth.signOut()
    }


}