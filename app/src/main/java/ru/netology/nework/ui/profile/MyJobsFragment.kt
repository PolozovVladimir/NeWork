package ru.netology.nework.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentMyJobsBinding

@AndroidEntryPoint
class MyJobsFragment : Fragment() {

    private val viewModel: MyJobsViewModel by viewModels()
    private var _binding: FragmentMyJobsBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_USER_ID = "user_id"

        fun newInstance(userId: Long): MyJobsFragment {
            val fragment = MyJobsFragment()
            val args = Bundle().apply {
                putLong(ARG_USER_ID, userId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getLong(ARG_USER_ID) ?: 0L
        viewModel.loadJobs(userId)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val adapter = MyJobsAdapter()
        binding.jobsList.layoutManager = LinearLayoutManager(requireContext())
        binding.jobsList.adapter = adapter

        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            adapter.submitList(jobs)
        }
    }

    private fun observeViewModel() {
        viewModel.error.observe(viewLifecycleOwner) { error ->
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}