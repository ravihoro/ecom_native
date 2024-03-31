package com.example.ecom.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecom.databinding.ViewPagerImageItemBinding

class ViewPager2Images : RecyclerView.Adapter<ViewPager2Images.ViewPager2ImagesViewHolder>() {

    inner class ViewPager2ImagesViewHolder(val binding: ViewPagerImageItemBinding) : ViewHolder(binding.root) {
        fun bind( image: String) {
            Glide.with(itemView).load(image).into(binding.imageProductDetails)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }


    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImagesViewHolder {
        return ViewPager2ImagesViewHolder(
            ViewPagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPager2ImagesViewHolder, position: Int) {
        var image = differ.currentList[position]
        holder.bind(image)
    }

}