package ru.netology.nework.ui.posts

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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.databinding.FragmentPostDetailBinding
import ru.netology.nework.util.MapUtils
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PostDetailFragment : Fragment(), OnMapReadyCallback {

    private val viewModel: PostDetailViewModel by viewModels()
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(ru.netology.nework.R.id.map_fragment) as? com.google.android.gms.maps.SupportMapFragment
        mapFragment?.getMapAsync(this)

        val mentionedUsersAdapter = MentionedUsersAdapter()
        binding.mentionedUsersRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.mentionedUsersRecycler.adapter = mentionedUsersAdapter

        binding.likeButton.setOnClickListener {
            viewModel.likePost()
        }

        binding.link.setOnClickListener {
            viewModel.post.value?.link?.let { link ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                startActivity(intent)
            }
        }

        viewModel.post.observe(viewLifecycleOwner) { post ->
            post?.let { bindPost(it) }
        }

        viewModel.mentionedUsers.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                binding.mentionedUsersCard.visibility = View.VISIBLE
                mentionedUsersAdapter.submitList(users)
            } else {
                binding.mentionedUsersCard.visibility = View.GONE
            }
        }
        viewModel.loadPost(1L) // TODO: Use args.postId when navigation is fixed
    }

    private fun bindPost(post: ru.netology.nework.model.Post) {
        binding.apply {
            author.text = post.author
            published.text = formatDate(post.published)
            content.text = post.content
            likeCount.text = post.likeOwnerIds.size.toString()
            
            authorJob.text = post.authorJob ?: "В поиске работы"
            if (post.authorAvatar != null) {
                Glide.with(avatar)
                    .load(post.authorAvatar)
                    .circleCrop()
                    .into(avatar)
            }

            if (post.attachment != null) {
                attachment.visibility = View.VISIBLE
                Glide.with(attachment)
                    .load(post.attachment.url)
                    .into(attachment)
            } else {
                attachment.visibility = View.GONE
            }
            if (!post.link.isNullOrBlank()) {
                link.visibility = View.VISIBLE
                link.text = post.link
            } else {
                link.visibility = View.GONE
            }

            if (MapUtils.isValidCoordinates(post.coords)) {
                mapCard.visibility = View.VISIBLE
                googleMap?.let { map ->
                    MapUtils.addMarkerAndMoveCamera(
                        map = map,
                        coordinates = post.coords!!,
                        title = "Местоположение поста"
                    )
                }
            } else {
                mapCard.visibility = View.GONE
            }

            likeButton.setImageResource(
                if (post.likedByMe) ru.netology.nework.R.drawable.ic_like_filled else ru.netology.nework.R.drawable.ic_like
            )
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        MapUtils.setupMap(map)
        
        viewModel.post.value?.coords?.let { coords ->
            if (MapUtils.isValidCoordinates(coords)) {
                MapUtils.addMarkerAndMoveCamera(
                    map = map,
                    coordinates = coords,
                    title = "Местоположение поста"
                )
            }
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
