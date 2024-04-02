package com.example.ecom.data

data class CartProduct(
    val product: Product,
    val quantity: Int,
    var selectedColor: Int? = null,
    var selectedSize: String? = null,
) {
    constructor() : this(Product(), 1, null, null)
}
