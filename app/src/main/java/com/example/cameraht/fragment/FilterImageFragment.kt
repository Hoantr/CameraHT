package com.example.cameraht.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraht.adapter.FilterAdapter
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.example.cameraht.R
import com.example.cameraht.data.FilterItem
import ja.burhanrashid52.photoeditor.PhotoEditor
import android.view.LayoutInflater
import android.view.ViewGroup


class FilterImageFragment  : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FilterAdapter
    private lateinit var mPhotoEditor: PhotoEditor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_filter_image, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewFilters)
        setupRecyclerView()

        return view
    }

    private fun setupRecyclerView() {
        val imageSimple=R.drawable.filter_simple
        val filters = listOf(
            FilterItem(imageSimple, PhotoFilter.NONE),
            FilterItem(imageSimple, PhotoFilter.BRIGHTNESS),
            FilterItem(imageSimple, PhotoFilter.CONTRAST),
            FilterItem(imageSimple, PhotoFilter.FLIP_HORIZONTAL),
            FilterItem(imageSimple, PhotoFilter.SHARPEN),
            FilterItem(imageSimple, PhotoFilter.SEPIA)
        )

        adapter = FilterAdapter(filters) { selectedFilter ->
            applyFilter(selectedFilter)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
    }

    private fun applyFilter(filter: PhotoFilter) {
        mPhotoEditor.setFilterEffect(filter)
    }

    fun setPhotoEditor(mPhotoEditor: PhotoEditor){
        this.mPhotoEditor=mPhotoEditor
    }
}