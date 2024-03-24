package com.example.ecom.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ecom.data.User
import com.example.ecom.util.RegisterFieldsState
import com.example.ecom.util.RegisterValidation
import com.example.ecom.util.Resource
import com.example.ecom.util.validateEmail
import com.example.ecom.util.validateField
import com.example.ecom.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor (
    private val firebaseAuth: FirebaseAuth
    ): ViewModel() {

        private val _register: MutableStateFlow<Resource<FirebaseUser>> = MutableStateFlow(Resource.Unspecified())
        val register: Flow<Resource<FirebaseUser>> = _register.asStateFlow()

        private val _validation = Channel<RegisterFieldsState>()
        val validation = _validation.receiveAsFlow()

        fun createAccountWithEmailAndPassword(user: User, password: String) {

            var shouldRegister = checkValidation(user, password)

            if(shouldRegister) {
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
            }else{
                val registerFieldState = RegisterFieldsState(validateField(user.firstName), validateField(user.lastName), validateEmail(user.email), validatePassword(password))
                runBlocking {
                    _validation.send(registerFieldState)
                }
            }
        }

    private fun checkValidation(user: User, password: String): Boolean {
        val firstNameValidation = validateField(user.firstName)
        val lastNameValidation = validateField(user.lastName)
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        val shouldRegister = firstNameValidation is RegisterValidation.Success
                && lastNameValidation is RegisterValidation.Success
                && emailValidation is RegisterValidation.Success
                && passwordValidation is RegisterValidation.Success

        return shouldRegister
    }

}