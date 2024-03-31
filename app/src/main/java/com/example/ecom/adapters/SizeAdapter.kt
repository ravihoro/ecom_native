package com.example.ecom.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.geometry.Size
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecom.databinding.SizeRvItemBinding

class SizeAdapter : RecyclerView.Adapter<SizeAdapter.SizeItemViewHolder>() {

    private var selectedPosition = -1

    inner class SizeItemViewHolder(val binding: SizeRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.tvSize.text = size
            if(position == selectedPosition) {
                binding.apply {
                    imageShadow.visibility = View.VISIBLE

                }
            } else{
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE

                }
            }
        }
    }

    var diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    var differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeItemViewHolder {
        return SizeItemViewHolder(
            SizeRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizeItemViewHolder, position: Int) {
        var size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick:((String) -> Unit)? = null


}