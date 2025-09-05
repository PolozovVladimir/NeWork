package ru.netology.nework.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemMentionedUserBinding
import ru.netology.nework.model.User

class EventUsersAdapter : ListAdapter<User, EventUsersAdapter.EventUserViewHolder>(EventUserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventUserViewHolder {
        val binding = ItemMentionedUserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventUserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventUserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventUserViewHolder(
        private val binding: ItemMentionedUserBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.apply {
                name.text = user.name
                login.text = "@${user.login}"

                // TODO: Load avatar with Glide
            }
        }
    }

    class EventUserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}





