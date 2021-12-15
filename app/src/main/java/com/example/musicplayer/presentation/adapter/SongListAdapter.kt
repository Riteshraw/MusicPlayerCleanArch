package com.example.musicplayer.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.data.dao.Song
import com.example.musicplayer.databinding.RecyclerviewSongItemBinding

class SongListAdapter(private val clickListener: SongClickListener) : ListAdapter<Song, SongListAdapter.SongViewHolder>(SongComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem,position, clickListener)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    class SongViewHolder(private val binding: RecyclerviewSongItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song?, position: Int, clickListener: SongClickListener) {
            binding.song = song
            binding.position = position
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): SongViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerviewSongItemBinding.inflate(layoutInflater, parent, false)
                return SongViewHolder(binding)
            }
        }

    }

    class SongComparator : DiffUtil.ItemCallback<Song>() {
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }

    }
}

class SongClickListener(val clickLister: (song: Song,position: Int) -> Unit) {
    fun onClick(song: Song, position: Int) = clickLister(song, position)
}