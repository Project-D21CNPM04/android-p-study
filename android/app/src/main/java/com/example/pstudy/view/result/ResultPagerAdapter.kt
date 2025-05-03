package com.example.pstudy.view.result

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pstudy.view.result.fragment.FlashcardsFragment
import com.example.pstudy.view.result.fragment.MaterialFragment
import com.example.pstudy.view.result.fragment.MindMapFragment
import com.example.pstudy.view.result.fragment.QuizzesFragment
import com.example.pstudy.view.result.fragment.RecordFragment
import com.example.pstudy.view.result.fragment.SummaryFragment

class ResultPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    companion object {
        private const val NUM_TABS = 4
    }

    override fun getItemCount(): Int {
        return NUM_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SummaryFragment.newInstance()
            1 -> MindMapFragment.newInstance()
            2 -> FlashcardsFragment.newInstance()
            3 -> QuizzesFragment.newInstance()
            else -> throw IllegalArgumentException("Invalid tab position: $position")
        }
    }
}