package ru.netology.nework.ui.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import android.widget.PopupMenu
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentEventsBinding

@AndroidEntryPoint
class EventsFragment : Fragment() {

    private val viewModel: EventsViewModel by viewModels()
    private var _binding: FragmentEventsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventsAdapter(
            onEventClick = { event ->
                val bundle = Bundle().apply {
                    putLong("eventId", event.id)
                }
                findNavController().navigate(R.id.action_eventsFragment_to_eventDetailFragment, bundle)
            },
            onLikeClick = { event ->
                viewModel.likeEvent(event.id)
            },
            onParticipateClick = { event ->
                viewModel.participateInEvent(event.id)
            },
            onMenuClick = { anchorView, event ->
                val popup = PopupMenu(requireContext(), anchorView)
                popup.menu.add(0, 1, 0, "Удалить")
                popup.setOnMenuItemClickListener { item ->
                    if (item.itemId == 1) {
                        viewModel.deleteEvent(event.id)
                        true
                    } else false
                }
                popup.show()
            }
        )

        binding.eventsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.eventsRecycler.adapter = adapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_eventsFragment_to_createEventFragment)
        }

        viewModel.events.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }


        viewModel.deletedEventId.observe(viewLifecycleOwner) { deletedId ->
            val current = viewModel.events.value ?: return@observe
            adapter.submitList(current.filter { it.id != deletedId })
        }



        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrBlank()) {
                android.widget.Toast.makeText(requireContext(), error, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
