package ru.netology.nework.ui.posts

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
import ru.netology.nework.databinding.FragmentPostsBinding

@AndroidEntryPoint
class PostsFragment : Fragment() {

    private val viewModel: PostsViewModel by viewModels()
    private var _binding: FragmentPostsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(
            onPostClick = { post ->
                val bundle = Bundle().apply {
                    putLong("postId", post.id)
                }
                findNavController().navigate(R.id.action_postsFragment_to_postDetailFragment, bundle)
            },
            onLikeClick = { post ->
                viewModel.likePost(post.id)
            },
            onMenuClick = { view, post ->
                val popup = PopupMenu(requireContext(), view)
                popup.menu.add(0, 1, 0, "Удалить")
                popup.setOnMenuItemClickListener { item ->
                    if (item.itemId == 1) {
                        viewModel.deletePost(post.id.toLong())
                        true
                    } else false
                }
                popup.show()
            }
        )

        binding.list.layoutManager = LinearLayoutManager(requireContext())
        binding.list.adapter = adapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_postsFragment_to_createPostFragment)
        }

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            android.util.Log.d("PostsFragment", "Posts received: ${posts.size}")
            adapter.submitList(posts)
        }

        viewModel.deletedPostId.observe(viewLifecycleOwner) { deletedId ->
            val current = viewModel.posts.value ?: return@observe
            adapter.submitList(current.filter { it.id != deletedId })
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            android.widget.Toast.makeText(requireContext(), error, android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
