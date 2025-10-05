package ru.netology.nework.ui.events

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventDetailBinding
import ru.netology.nework.util.MapUtils
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class EventDetailFragment : Fragment() {
    private val args: Bundle by lazy { arguments ?: Bundle() }
    private val eventId: Long by lazy { args.getLong("eventId", 0L) }

    private val viewModel: EventDetailViewModel by viewModels()
    private var _binding: FragmentEventDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val speakersAdapter = EventUsersAdapter()
        binding.speakersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.speakersRecycler.adapter = speakersAdapter

        val participantsAdapter = EventUsersAdapter()
        binding.participantsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.participantsRecycler.adapter = participantsAdapter

        binding.likeButton.setOnClickListener {
            viewModel.likeEvent()
        }

        binding.participateButton.setOnClickListener {
            viewModel.participateInEvent()
        }

        binding.link.setOnClickListener {
            viewModel.event.value?.link?.let { link ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(intent)
            }
        }

        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let { bindEvent(it) }
        }

        viewModel.speakers.observe(viewLifecycleOwner) { speakers ->
            if (speakers.isNotEmpty()) {
                binding.speakersCard.visibility = View.VISIBLE
                speakersAdapter.submitList(speakers)
            } else {
                binding.speakersCard.visibility = View.GONE
            }
        }

        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            if (participants.isNotEmpty()) {
                binding.participantsCard.visibility = View.VISIBLE
                participantsAdapter.submitList(participants)
            } else {
                binding.participantsCard.visibility = View.GONE
            }
        }

        viewModel.loadEvent(eventId)
    }

    private fun bindEvent(event: ru.netology.nework.model.Event) {
        binding.apply {
            author.text = event.author
            published.text = formatDate(event.published)
            eventDatetime.text = "Событие: ${formatDate(event.datetime)}"
            eventType.text = if (event.type.name == "ONLINE") "Онлайн" else "Офлайн"
            content.text = event.content
            likeCount.text = event.likeOwnerIds.size.toString()
            participantsCount.text = event.participantsIds.size.toString()
            
            authorJob.text = event.authorJob ?: "В поиске работы"

            if (event.authorAvatar != null) {
                Glide.with(avatar)
                    .load(event.authorAvatar)
                    .circleCrop()
                    .into(avatar)
            }

            if (event.attachment != null) {
                attachment.visibility = View.VISIBLE
                Glide.with(attachment)
                    .load(event.attachment.url)
                    .into(attachment)
            } else {
                attachment.visibility = View.GONE
            }

            if (!event.link.isNullOrBlank()) {
                link.visibility = View.VISIBLE
                link.text = event.link
            } else {
                link.visibility = View.GONE
            }

            if (MapUtils.isValidCoordinates(event.coords)) {
                mapCard.visibility = View.VISIBLE
            } else {
                mapCard.visibility = View.GONE
            }

            likeButton.setImageResource(
                if (event.likedByMe) ru.netology.nework.R.drawable.ic_like_filled else ru.netology.nework.R.drawable.ic_like
            )

            participateButton.setImageResource(
                if (event.participatedByMe) ru.netology.nework.R.drawable.ic_participate_filled else ru.netology.nework.R.drawable.ic_participate
            )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
