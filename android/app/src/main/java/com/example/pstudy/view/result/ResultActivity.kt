package com.example.pstudy.view.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.databinding.ActivityResultBinding
import com.example.pstudy.view.result.ResultPagerAdapter
import com.example.pstudy.view.result.ResultViewModel
import com.google.android.material.tabs.TabLayoutMediator

class ResultActivity : BindingActivity<ActivityResultBinding>() {

    private val viewModel: ResultViewModel by viewModels()

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ResultActivity::class.java)
        }
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        setupToolbar()
        setupViewPagerAndTabs()
        observeViewModel()
        //viewModel.loadResultData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.app_name)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupViewPagerAndTabs() {
        val adapter = ResultPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun observeViewModel() {
        // Observe shared ViewModel LiveData if needed for Activity-level UI changes
        // Example:
        // viewModel.resultTitle.observe(this) { title ->
        //     supportActionBar?.title = title
        // }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            0 -> getString(R.string.tab_summary)
            1 -> getString(R.string.tab_record)
            2 -> getString(R.string.tab_material)
            3 -> getString(R.string.tab_mind_map)
            4 -> getString(R.string.tab_flashcards)
            5 -> getString(R.string.tab_quizs)
            else -> null
        }
    }
}