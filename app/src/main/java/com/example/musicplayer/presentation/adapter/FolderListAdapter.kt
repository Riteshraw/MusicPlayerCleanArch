package com.example.musicplayer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.presentation.adapter.FolderListAdapter.FolderViewHolder
import com.example.musicplayer.data.dao.Folder
import com.example.musicplayer.databinding.RecyclerviewItemBinding

class FolderListAdapter(private val clickListener: FolderClickListener) :
    ListAdapter<Folder, FolderViewHolder>(FolderComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        return FolderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem, position, clickListener)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    class FolderViewHolder(private val binding: RecyclerviewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(folder: Folder?, position: Int, clickListener: FolderClickListener) {
            binding.folder = folder
            binding.position = position
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): FolderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewItemBinding.inflate(layoutInflater, parent, false)
                return FolderViewHolder(binding)
            }
        }

    }

    class FolderComparator : DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Folder, newItem: Folder): Boolean {
            return oldItem == newItem
        }
    }
}

class FolderClickListener(val clickLister: (folder: Folder, position: Integer) -> Unit) {
    fun onClick(folder: Folder, position: Integer) = clickLister(folder, position)
}