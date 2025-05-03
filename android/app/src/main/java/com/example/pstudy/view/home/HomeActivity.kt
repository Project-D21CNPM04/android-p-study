package com.example.pstudy.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.base.ui.base.BindingActivity
import com.example.base.utils.startActivityAsRoot
import com.example.pstudy.R
import com.example.pstudy.data.firebase.FirebaseAuthHelper
import com.example.pstudy.databinding.ActivityHomeBinding
import com.example.pstudy.databinding.ItemHomeTabLayoutBinding
import com.example.pstudy.view.home.adapter.PagerAdapter
import com.example.pstudy.view.home.fragment.AllFragment
import com.example.pstudy.view.home.fragment.FolderFragment
import com.example.pstudy.view.home.fragment.SharedFragment
import com.example.pstudy.view.home.viewmodel.HomeViewModel
import com.example.pstudy.view.input.InputActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BindingActivity<ActivityHomeBinding>() {
    override fun inflateBinding(layoutInflater: LayoutInflater) =
        ActivityHomeBinding.inflate(layoutInflater)

    private val viewModel by viewModels<HomeViewModel>()
    private val pagerAdapter by lazy { PagerAdapter(this) }

    override fun getStatusBarColor() = R.color.black

    override fun updateUI(savedInstanceState: Bundle?) {
        initView()
        setupPager()
        handleOnClick()
    }

    private fun initView() {
        binding.tvGreeting.text = buildString {
            append(getString(R.string.home_greeting))
            append(" ")
            append(FirebaseAuthHelper.getCurrentUserName())
        }
    }

    private fun setupPager() {
        pagerAdapter.apply {
            addFragment(AllFragment())
            addFragment(FolderFragment())
            addFragment(SharedFragment())
            binding.viewPager.adapter = this
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabBinding = ItemHomeTabLayoutBinding.inflate(LayoutInflater.from(this))
            tabBinding.tvTitle.text = viewModel.getItemTab(this, position)
            tab.customView = tabBinding.root
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) =
                updateTabStyle(tab, R.color.primary_blue)

            override fun onTabUnselected(tab: TabLayout.Tab?) =
                updateTabStyle(tab, android.R.color.transparent)

            override fun onTabReselected(tab: TabLayout.Tab?) {} // No action needed
        })

        updateTabStyle(binding.tabLayout.getTabAt(0), R.color.primary_blue)
    }

    private fun updateTabStyle(tab: TabLayout.Tab?, colorRes: Int) {
        tab?.customView?.findViewById<TextView>(R.id.tvTitle)?.apply {
            backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
            isSingleLine = true
        }
    }

    private fun handleOnClick() {
        with(binding) {
            icAdd.setOnClickListener {
                addFeature.isVisible = !addFeature.isVisible
            }

            icFile.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_FILE)
                })
                addFeature.isVisible = false
            }

            icLink.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_LINK)
                })
                addFeature.isVisible = false
            }

            icSound.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_TEXT)
                })
                addFeature.isVisible = false
            }
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivityAsRoot(HomeActivity::class.java)
        }
    }
}