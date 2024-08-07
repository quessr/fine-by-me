package com.example.finebyme.ui.favoriteList

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.finebyme.data.datasource.UnSplashDataSource
import com.example.finebyme.data.datasource.UserDataSource
import com.example.finebyme.data.db.FavoritePhotosDatabase
import com.example.finebyme.data.db.Photo
import com.example.finebyme.data.network.RetrofitInstance
import com.example.finebyme.data.repository.PhotoRepository
import com.example.finebyme.databinding.FragmentFavoriteListBinding
import com.example.finebyme.di.AppViewModelFactory
import com.example.finebyme.ui.photoList.PhotoAdapter
import com.example.finebyme.utils.IntentUtils.newPhotoDetail
class FavoriteListFragment : Fragment() {

    private lateinit var photoAdapter: PhotoAdapter
    private val favoriteListViewModel: FavoriteListViewModel by activityViewModels {
        val application = requireActivity().application
        val photoDao = FavoritePhotosDatabase.getDatabase(application).PhotoDao()
        val retrofitService = RetrofitInstance.retrofitService
        val unSplashDataSource = UnSplashDataSource(retrofitService)
        val userDataSource = UserDataSource(photoDao)
        val photoRepository = PhotoRepository(unSplashDataSource, userDataSource)
        AppViewModelFactory(application, photoRepository)
    }
    private var recyclerViewState: Parcelable? = null

    private var _binding: FragmentFavoriteListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private var isPass = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerview()
        setupObservers()
        setupListeners()

    }

    private fun setupRecyclerview() {
        photoAdapter = PhotoAdapter(favoriteListViewModel)
        recyclerView = binding.recyclerView

        val numberOfColumns = 2
        val layoutManager =
            StaggeredGridLayoutManager(numberOfColumns, LinearLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }

        recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = photoAdapter
    }

    private fun setupObservers() {
        favoriteListViewModel.photos.observe(
            viewLifecycleOwner
        ) { photos ->
            photoAdapter.submitList(photos) {
                if (recyclerViewState != null) {
                    recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
                }
                else {
                    recyclerView.scrollToPosition(0)
                }
            }
            binding.tvEmpty.isVisible = photos.isEmpty()
        }
    }

    private fun setupListeners() {
        photoAdapter.setOnPhotoClickListener(object : PhotoAdapter.OnPhotoClickListener {
            override fun onPhotoClick(photo: Photo) {
                val intent = newPhotoDetail(requireContext(), photo)
                startActivity(intent)
            }
        })
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onResume() {
        super.onResume()
        if (isPass) {
            isPass = false
            return
        }
        favoriteListViewModel.onResumeScreen()
    }

    override fun onPause() {
        super.onPause()
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}