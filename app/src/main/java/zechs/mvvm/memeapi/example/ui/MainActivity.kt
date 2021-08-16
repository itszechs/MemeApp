package zechs.mvvm.memeapi.example.ui

import android.os.Bundle
import android.transition.TransitionManager
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import zechs.mvvm.memeapi.example.adapter.MemeAdapter
import zechs.mvvm.memeapi.example.databinding.ActivityMainBinding
import zechs.mvvm.memeapi.example.repository.MemeRepository
import zechs.mvvm.memeapi.example.utils.Resource


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MemeViewModel
    private lateinit var memeAdapter: MemeAdapter

    val tag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val filesRepository = MemeRepository()
        val viewModelProviderFactory = MemeViewModelProviderFactory(application, filesRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MemeViewModel::class.java)

        setContentView(binding.root)

        setupRecyclerView()

        viewModel.memeList.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    TransitionManager.beginDelayedTransition(binding.root)
                    response.data?.let { logsResponse ->
                        memeAdapter.differ.submitList(logsResponse.memes.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(
                            applicationContext,
                            "An error occurred: $message",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.e(tag, "An error occurred: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }

    private fun hideProgressBar() {
        binding.loadingList.visibility = INVISIBLE
    }

    private fun showProgressBar() {
        binding.loadingList.visibility = VISIBLE
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val isNotAtBeginning = firstVisibleItemPosition >= 0

            if (isNotAtBeginning) {
                binding.loadMore.apply {
                    visibility = VISIBLE
                    setOnClickListener {
                        viewModel.getMemes()
                        binding.memeList.smoothScrollToPosition(memeAdapter.itemCount - 1)
                    }
                }
            } else {
                binding.loadMore.apply {
                    visibility = INVISIBLE
                }
                Log.d(tag, "Not paginating")
                binding.memeList.setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun setupRecyclerView() {
        memeAdapter = MemeAdapter()
        binding.memeList.apply {
            adapter = memeAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addOnScrollListener(this@MainActivity.scrollListener)
        }
    }
}