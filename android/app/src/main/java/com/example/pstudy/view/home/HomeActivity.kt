package com.example.pstudy.view.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
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
import com.example.pstudy.view.settings.SettingsActivity
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
            binding.viewPager.adapter = this
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val tabBinding = ItemHomeTabLayoutBinding.inflate(LayoutInflater.from(this))
            tabBinding.tvTitle.text = viewModel.getItemTab(this, position)
            tab.customView = tabBinding.root
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) =
                updateTabStyle(tab, R.color.color_primary, R.color.color_on_primary)

            override fun onTabUnselected(tab: TabLayout.Tab?) =
                updateTabStyle(tab, android.R.color.transparent, R.color.color_primary)

            override fun onTabReselected(tab: TabLayout.Tab?) {} // No action needed
        })

        updateTabStyle(binding.tabLayout.getTabAt(0), R.color.color_primary, R.color.color_on_primary)
    }

    private fun updateTabStyle(tab: TabLayout.Tab?, colorRes: Int, textColorRes: Int) {
        tab?.customView?.findViewById<TextView>(R.id.tvTitle)?.apply {
            setTextColor(ContextCompat.getColor(context, textColorRes))
            backgroundTintList = ContextCompat.getColorStateList(context, colorRes)
            isSingleLine = true
        }
    }

    private fun handleOnClick() {
        with(binding) {
            icMic.setOnClickListener {
                if (isValidPtitUser()) {
                    startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                        putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_AUDIO)
                    })
                    hideFeatureButtons()
                } else {
                    showRestrictedFeatureToast()
                }
            }

            icAdd.setOnClickListener {
                if (addFeature.isVisible) {
                    hideFeatureButtons()
                } else {
                    showFeatureButtons()
                }
            }

            icFile.setOnClickListener {
                if (isValidPtitUser()) {
                    startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                        putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_FILE)
                    })
                    hideFeatureButtons()
                } else {
                    showRestrictedFeatureToast()
                }
            }

            icImage.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_PHOTO)
                })
                hideFeatureButtons()
            }

            icLink.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_LINK)
                })
                hideFeatureButtons()
            }

            icText.setOnClickListener {
                startActivity(Intent(this@HomeActivity, InputActivity::class.java).apply {
                    putExtra(InputActivity.ARG_INPUT_TYPE, InputActivity.INPUT_TYPE_TEXT)
                })
                hideFeatureButtons()
            }

            ivAvatar.setOnClickListener {
                SettingsActivity.start(this@HomeActivity)
            }

            tvGreeting.setOnClickListener {
                SettingsActivity.start(this@HomeActivity)
            }

            tvSubtitle.setOnClickListener {
                SettingsActivity.start(this@HomeActivity)
            }
        }
    }

    private fun showFeatureButtons() {
        with(binding) {
            icAdd.animate()
                .rotation(45f)
                .setDuration(300)
                .start()

            addFeature.isVisible = true

            icLink.alpha = 0f
            icLink.scaleX = 0f
            icLink.scaleY = 0f
            icLink.translationY = 20f

            icFile.alpha = 0f
            icFile.scaleX = 0f
            icFile.scaleY = 0f
            icFile.translationY = 20f

            icText.alpha = 0f
            icText.scaleX = 0f
            icText.scaleY = 0f
            icText.translationY = 20f

            icImage.alpha = 0f
            icImage.scaleX = 0f
            icImage.scaleY = 0f
            icImage.translationY = 20f

            icLink.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .start()

            icFile.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setStartDelay(50)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .start()

            icText.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .start()

            icImage.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .translationY(0f)
                .setStartDelay(150)
                .setDuration(300)
                .setInterpolator(OvershootInterpolator())
                .start()
        }
    }

    private fun hideFeatureButtons() {
        with(binding) {
            icAdd.animate()
                .rotation(0f)
                .setDuration(300)
                .start()
            icLink.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(20f)
                .setDuration(200)
                .start()

            icFile.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(20f)
                .setStartDelay(50)
                .setDuration(200)
                .start()

            icText.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(20f)
                .setStartDelay(100)
                .setDuration(200)
                .start()

            icImage.animate()
                .alpha(0f)
                .scaleX(0f)
                .scaleY(0f)
                .translationY(20f)
                .setStartDelay(150)
                .setDuration(200)
                .withEndAction {
                    addFeature.isVisible = false
                }
                .start()
        }
    }

    private fun isValidPtitUser(): Boolean {
        val email = FirebaseAuthHelper.getCurrentUserEmail()
        return email?.endsWith("ptit.edu.vn") == true
    }

    private fun showRestrictedFeatureToast() {
        Toast.makeText(
            this,
            "Tính năng này chỉ dành cho người dùng có email ptit.edu.vn",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        fun start(context: Context) {
            context.startActivityAsRoot(HomeActivity::class.java)
        }
    }
}