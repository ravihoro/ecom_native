package com.example.ecom.fragments.shopping

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecom.R
import com.example.ecom.adapters.CartProductAdapter
import com.example.ecom.databinding.FragmentCartBinding
import com.example.ecom.firebase.FirebaseCommon
import com.example.ecom.util.Resource
import com.example.ecom.util.VerticalItemDecoration
import com.example.ecom.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CartFragment: Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding

    private val cartProductAdapter by lazy { CartProductAdapter() }

    private val viewModel by activityViewModels<CartViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCartRv()

        var totalPrice = 0f

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.productsPrice.collectLatest { price ->
                    price?.let {
                        totalPrice = it
                        binding.tvTotalPrice.text = "$ $price"
                    }
                }
            }
        }

        binding.buttonCheckout.setOnClickListener{
            val action = CartFragmentDirections
                .actionCartFragmentToBillingFragment(totalPrice, cartProductAdapter.differ.currentList.toTypedArray())

            findNavController().navigate(action)
        }

        cartProductAdapter.onProductClick = {
            val b = Bundle().apply{putParcelable("product", it.product)}
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        cartProductAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartProductAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deleteDialog.collectLatest {
                    val alertDialog = AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete item from cart")
                        setMessage("Do you want to delete item from your cart?")
                        setNegativeButton("Cancel") {dialog, _ ->
                            dialog.dismiss()
                        }
                        setPositiveButton("Yes") {dialog, _ ->
                            viewModel.deleteCartProduct(it)
                            dialog.dismiss()
                        }
                    }

                    alertDialog.create()
                    alertDialog.show()

                }
            }
        }


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cartProducts.collectLatest {
                    when(it) {
                        is Resource.Success -> {
                            binding.progressBarCart.visibility = View.INVISIBLE
                            if(it.data!!.isEmpty()){
                                showEmptyCart()
                                hideOtherViews()
                            } else{
                                hideEmptyCart()
                                showOtherViews()

                                cartProductAdapter.differ.submitList(it.data)
                            }
                        }
                        is Resource.Error -> {
                            binding.progressBarCart.visibility = View.INVISIBLE
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        is Resource.Loading -> {
                            binding.progressBarCart.visibility = View.VISIBLE
                        }
                        else -> Unit
                    }
                }
            }
        }

    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            totalBoxContainer.visibility = View.VISIBLE
            buttonCheckout.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.INVISIBLE
            totalBoxContainer.visibility = View.INVISIBLE
            buttonCheckout.visibility = View.INVISIBLE

        }
    }

    private fun hideEmptyCart() {
        binding.layoutCartEmpty.visibility = View.INVISIBLE
    }

    private fun showEmptyCart() {
        binding.layoutCartEmpty.visibility = View.VISIBLE
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartProductAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }


}