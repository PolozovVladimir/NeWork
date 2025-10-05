package ru.netology.nework.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentSelectUsersBinding
import ru.netology.nework.model.User

@AndroidEntryPoint
class SelectUsersFragment : Fragment() {

    private val viewModel: SelectUsersViewModel by viewModels()
    private var _binding: FragmentSelectUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SelectUsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupToolbar()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = SelectUsersAdapter { user, isSelected ->
        }

        binding.usersList.layoutManager = LinearLayoutManager(requireContext())
        binding.usersList.adapter = adapter
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupFab() {
        binding.confirmButton.setOnClickListener {
            val selectedUsers = adapter.getSelectedUsers()
            val result = Bundle().apply {
                putParcelableArray("selected_users", selectedUsers.toTypedArray())
            }
            parentFragmentManager.setFragmentResult("users_selected", result)
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
