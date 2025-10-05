package ru.netology.nework.ui.users

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nework.R
import ru.netology.nework.databinding.ItemUserSelectBinding
import ru.netology.nework.model.User

class SelectUsersAdapter(
    private val onUserSelected: (User, Boolean) -> Unit
) : ListAdapter<User, SelectUsersAdapter.UserViewHolder>(UserDiffCallback()) {

    private val selectedUsers = mutableSetOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserSelectBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getSelectedUsers(): List<User> {
        return currentList.filter { selectedUsers.contains(it.id) }
    }

    inner class UserViewHolder(
        private val binding: ItemUserSelectBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.name.text = user.name
            binding.job.text = user.login

            if (user.avatar != null) {
                Glide.with(binding.avatar)
                    .load(user.avatar)
                    .circleCrop()
                    .into(binding.avatar)
            } else {
                binding.avatar.setImageResource(R.drawable.ic_avatar_placeholder)
            }

            binding.checkbox.isChecked = selectedUsers.contains(user.id)

            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedUsers.add(user.id)
                } else {
                    selectedUsers.remove(user.id)
                }
                onUserSelected(user, isChecked)
            }

            binding.root.setOnClickListener {
                binding.checkbox.isChecked = !binding.checkbox.isChecked
            }
        }
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
