package ru.netology.nework.ui.posts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import ru.netology.nework.databinding.ItemPostBinding
import ru.netology.nework.model.Post
import java.text.SimpleDateFormat
import java.util.*

class PostsAdapter(
    private val onPostClick: (Post) -> Unit,
    private val onLikeClick: (Post) -> Unit,
    private val onMenuClick: (android.view.View, Post) -> Unit
) : ListAdapter<Post, PostsAdapter.PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostClick, onLikeClick, onMenuClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(
        private val binding: ItemPostBinding,
        private val onPostClick: (Post) -> Unit,
        private val onLikeClick: (Post) -> Unit,
        private val onMenuClick: (android.view.View, Post) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                author.text = post.author
                published.text = formatDate(post.published)
                content.text = post.content
                likeCount.text = post.likeOwnerIds.size.toString()
                likeButton.setImageResource(
                    if (post.likedByMe) ru.netology.nework.R.drawable.ic_like_filled else ru.netology.nework.R.drawable.ic_like
                )

                if (post.authorAvatar != null) {
                    Glide.with(avatar)
                        .load(post.authorAvatar)
                        .circleCrop()
                        .into(avatar)
                } else {
                    avatar.setImageResource(ru.netology.nework.R.drawable.ic_avatar_placeholder)
                }

                if (post.attachment != null) {
                    attachmentContainer.visibility = android.view.View.VISIBLE
                    Glide.with(attachmentImage)
                        .load(post.attachment.url)
                        .centerCrop()
                        .into(attachmentImage)
                } else {
                    attachmentContainer.visibility = android.view.View.GONE
                }

                if (!post.link.isNullOrBlank()) {
                    binding.link.visibility = android.view.View.VISIBLE
                    binding.linkUrl.text = post.link
                    binding.link.setOnClickListener {
                        val context = it.context
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.link))
                        context.startActivity(intent)
                    }
                } else {
                    binding.link.visibility = android.view.View.GONE
                    binding.link.setOnClickListener(null)
                }

                root.setOnClickListener { onPostClick(post) }
                likeButton.setOnClickListener { onLikeClick(post) }
                menuButton.setOnClickListener { view -> onMenuClick(view, post) }
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(dateString)
                outputFormat.format(date ?: Date())
            } catch (e: Exception) {
                dateString
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
