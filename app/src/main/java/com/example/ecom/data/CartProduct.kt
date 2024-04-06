package com.example.ecom.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity: Int,
    var selectedColor: Int? = null,
    var selectedSize: String? = null,
) : Parcelable {
    constructor() : this(Product(), 1, null, null)
}
