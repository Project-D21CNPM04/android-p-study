package com.example.pstudy.view.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import com.example.base.ui.base.BindingActivity
import com.example.pstudy.R
import com.example.pstudy.data.model.StudyMaterials
import com.example.pstudy.databinding.ActivityResultBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : BindingActivity<ActivityResultBinding>() {

    private val viewModel: ResultViewModel by viewModels()

    companion object {
        private const val EXTRA_STUDY_MATERIALS = "extra_study_materials"

        fun start(context: Context, studyMaterials: StudyMaterials) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(EXTRA_STUDY_MATERIALS, studyMaterials)
            context.startActivity(intent)
        }
    }

    override fun getStatusBarColor() = R.color.black

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityResultBinding {
        return ActivityResultBinding.inflate(layoutInflater)
    }

    override fun updateUI(savedInstanceState: Bundle?) {
        setupToolbar()
        setupViewPagerAndTabs()
        observeViewModel()
        val studyMaterials = getStudyMaterialsFromIntent()
        if (studyMaterials != null) {
            viewModel.initializeWithStudyMaterials(studyMaterials)
        }
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
        binding.viewPager.isUserInputEnabled = false

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
            1 -> getString(R.string.tab_mind_map)
            2 -> getString(R.string.tab_flashcards)
            3 -> getString(R.string.tab_quizs)
            else -> null
        }
    }

    private fun getStudyMaterialsFromIntent(): StudyMaterials? {
        @Suppress("DEPRECATION")
        val serializable = intent.getSerializableExtra(EXTRA_STUDY_MATERIALS)
        return serializable as? StudyMaterials
    }
}