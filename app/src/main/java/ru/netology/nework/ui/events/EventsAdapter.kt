package ru.netology.nework.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.content.Intent
import android.net.Uri
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onLikeClick: (Event) -> Unit,
    private val onParticipateClick: (Event) -> Unit,
    private val onMenuClick: (android.view.View, Event) -> Unit
) : ListAdapter<Event, EventsAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onEventClick, onLikeClick, onParticipateClick, onMenuClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onEventClick: (Event) -> Unit,
        private val onLikeClick: (Event) -> Unit,
        private val onParticipateClick: (Event) -> Unit,
        private val onMenuClick: (android.view.View, Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.author.text = event.author
            binding.published.text = formatDate(event.published)
            binding.eventDatetime.text = "Событие: ${formatDate(event.datetime)}"
            binding.eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
            binding.content.text = event.content
            binding.likeCount.text = event.likeOwnerIds.size.toString()
            binding.likeButton.setCompoundDrawablesWithIntrinsicBounds(
                if (event.likedByMe) ru.netology.nework.R.drawable.ic_like_filled else ru.netology.nework.R.drawable.ic_like,
                0, 0, 0
            )
            binding.participateButton.setCompoundDrawablesWithIntrinsicBounds(
                if (event.participatedByMe) ru.netology.nework.R.drawable.ic_participate_filled else ru.netology.nework.R.drawable.ic_participate,
                0, 0, 0
            )
            if (event.authorAvatar != null) {
                Glide.with(binding.avatar)
                    .load(event.authorAvatar)
                    .circleCrop()
                    .into(binding.avatar)
            } else {
                binding.avatar.setImageResource(ru.netology.nework.R.drawable.ic_avatar_placeholder)
            }
            if (event.attachment != null) {
                binding.attachment.visibility = android.view.View.VISIBLE
                val attachmentImage = binding.attachment.findViewById<android.widget.ImageView>(ru.netology.nework.R.id.attachmentImage)
                Glide.with(attachmentImage)
                    .load(event.attachment.url)
                    .centerCrop()
                    .into(attachmentImage)
            } else {
                binding.attachment.visibility = android.view.View.GONE
            }
            if (!event.link.isNullOrBlank()) {
                binding.link.visibility = android.view.View.VISIBLE
                val linkUrl = binding.link.findViewById<android.widget.TextView>(ru.netology.nework.R.id.linkUrl)
                linkUrl.text = event.link
                binding.link.setOnClickListener {
                    val context = it.context
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
                    context.startActivity(intent)
                }
            } else {
                binding.link.visibility = android.view.View.GONE
                binding.link.setOnClickListener(null)
            }

            binding.root.setOnClickListener { onEventClick(event) }
            binding.likeButton.setOnClickListener { onLikeClick(event) }
            binding.participateButton.setOnClickListener { onParticipateClick(event) }
            binding.menuButton.setOnClickListener { view -> onMenuClick(view, event) }
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

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }



        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}








