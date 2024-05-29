package com.example.finebyme.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finebyme.R
import com.example.finebyme.data.network.RetrofitInstance
import com.example.finebyme.data.network.RetrofitService
import com.example.finebyme.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var photoAdapter: PhotoAdapter

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        photoAdapter = PhotoAdapter()
        binding.recyclerView.adapter = photoAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        RetrofitInstance.fetchRandomPhoto{ photos -> photos?.let { photoAdapter.setPhoto(it) } }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}