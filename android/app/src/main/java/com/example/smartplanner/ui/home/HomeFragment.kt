package com.example.smartplanner.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartplanner.R
import com.example.smartplanner.data.NewsRepository
import java.util.concurrent.Executors

class HomeFragment : Fragment() {
    private val executor = Executors.newSingleThreadExecutor()
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadNews()
            handler.postDelayed(this, 120_000)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView
    private lateinit var adapter: NewsAdapter
    private lateinit var repository: NewsRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.recyclerNews)
        progressBar = view.findViewById(R.id.progressNews)
        errorText = view.findViewById(R.id.textNewsError)
        repository = NewsRepository(requireContext())

        adapter = NewsAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val cached = repository.cachedArticles()
        if (cached.isNotEmpty()) {
            adapter.submitList(cached)
            progressBar.visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        loadNews()
        handler.postDelayed(refreshRunnable, 120_000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(refreshRunnable)
    }

    private fun loadNews() {
        if (adapter.itemCount == 0) {
            progressBar.visibility = View.VISIBLE
        }
        errorText.visibility = View.GONE

        executor.execute {
            val result = runCatching { repository.refreshArticles() }
            if (!isAdded) return@execute

            requireActivity().runOnUiThread {
                progressBar.visibility = View.GONE
                result.onSuccess { articles ->
                    adapter.submitList(articles)
                    errorText.visibility = View.GONE
                }.onFailure {
                    if (adapter.itemCount == 0) {
                        errorText.visibility = View.VISIBLE
                        errorText.text = "Не удалось загрузить новости"
                    }
                }
            }
        }
    }
}
