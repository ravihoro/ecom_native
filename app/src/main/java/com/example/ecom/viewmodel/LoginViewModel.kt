package com.example.ecom.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecom.data.User
import com.example.ecom.util.LoginFieldsState
import com.example.ecom.util.LoginValidation
import com.example.ecom.util.Resource
import com.example.ecom.util.validateEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
):ViewModel() {
    private val _login : MutableStateFlow<Resource<FirebaseUser>> = MutableStateFlow(Resource.Unspecified())
    public val login = _login.asSharedFlow()

//    private val _validation = Channel<LoginFieldsState>()
//    val validation = _validation.receiveAsFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    fun login(email: String, password: String) {
        _login.value = Resource.Loading()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("login success::::::::", "${it?.user?.uid}")
                viewModelScope.launch {

                    it.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }

            }.addOnFailureListener{
                Log.d("login failure ::::::::", "login failed")
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }


        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
        }.addOnFailureListener{
            viewModelScope.launch {
                _resetPassword.emit(Resource.Error(it.message.toString()))
            }
        }

    }

}