package com.example.ecom.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ecom.data.User
import com.example.ecom.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (
    private val firebaseAuth: FirebaseAuth
    ): ViewModel() {

        private val _register: MutableStateFlow<Resource<FirebaseUser>> = MutableStateFlow(Resource.Unspecified())
        val register: Flow<Resource<FirebaseUser>> = _register.asStateFlow()


        fun createAccountWithEmailAndPassword(user: User, password: String) {

            _register.value = Resource.Loading()

//            runBlocking {
//                _register.emit(Resource.Loading())
//            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        _register.value = Resource.Success(it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
            }
        }

    }