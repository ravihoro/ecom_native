package com.example.ecom.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecom.R
import com.example.ecom.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView () {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        com.example.ecom.R.id.bottomNavigation
    )

    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView() {
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation
    )

    bottomNavigationView.visibility = View.VISIBLE
}