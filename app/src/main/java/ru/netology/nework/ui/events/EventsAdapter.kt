package ru.netology.nework.ui.events

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.databinding.ItemEventBinding
import ru.netology.nework.model.Event
import java.text.SimpleDateFormat
import java.util.*

class EventsAdapter(
    private val onEventClick: (Event) -> Unit,
    private val onLikeClick: (Event) -> Unit,
    private val onParticipateClick: (Event) -> Unit,
    private val onMenuClick: (Event) -> Unit
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
        private val onMenuClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.apply {
                author.text = event.author
                published.text = formatDate(event.published)
                eventDatetime.text = "Событие: ${formatDate(event.datetime)}"
                eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
                content.text = event.content
                likeCount.text = event.likeOwnerIds.size.toString()

                // TODO: Load avatar with Glide
                // TODO: Load attachment if present
                // TODO: Set like button state
                // TODO: Set participate button state

                root.setOnClickListener { onEventClick(event) }
                likeButton.setOnClickListener { onLikeClick(event) }
                participateButton.setOnClickListener { onParticipateClick(event) }
                menuButton.setOnClickListener { onMenuClick(event) }
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

    class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}





