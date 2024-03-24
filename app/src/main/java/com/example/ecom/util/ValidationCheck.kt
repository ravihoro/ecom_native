package com.example.ecom.util

import android.util.Patterns

fun validateField(value: String): RegisterValidation {
    if(value.isEmpty())
        return RegisterValidation.Failed("Field cannot be empty")

    return RegisterValidation.Success
}

fun validateEmail(email: String): RegisterValidation {
    if(email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")

    if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password: String): RegisterValidation {
    if(password.isEmpty())
        return RegisterValidation.Failed("Password cannot be empty")

    if(password.length < 6)
        return RegisterValidation.Failed("Password should contain 6 characters")

    return RegisterValidation.Success
}