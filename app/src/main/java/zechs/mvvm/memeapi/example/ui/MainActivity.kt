package zechs.mvvm.memeapi.example.ui

import android.os.Bundle
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import zechs.mvvm.memeapi.example.adapter.MemeAdapter
import zechs.mvvm.memeapi.example.databinding.ActivityMainBinding
import zechs.mvvm.memeapi.example.repository.MemeRepository
import zechs.mvvm.memeapi.example.utils.Resource


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MemeViewModel
    private lateinit var memeAdapter: MemeAdapter

    private val thisTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        val filesRepository = MemeRepository()
        val viewModelProviderFactory = MemeViewModelProviderFactory(application, filesRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MemeViewModel::class.java)

        setContentView(binding.root)
        setupRecyclerView()

        binding.refreshMemes.apply {
            setOnClickListener {
                viewModel.getMemes()
                binding.memeList.smoothScrollToPosition(0)
            }
        }

        viewModel.memeList.observe(this, { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    //TransitionManager.beginDelayedTransition(binding.root)
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
                        Log.e(thisTag, "An error occurred: $message")
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

    private fun setupRecyclerView() {
        memeAdapter = MemeAdapter()
        binding.memeList.apply {
            adapter = memeAdapter
            layoutManager = LinearLayoutManager(applicationContext)
        }
    }
}