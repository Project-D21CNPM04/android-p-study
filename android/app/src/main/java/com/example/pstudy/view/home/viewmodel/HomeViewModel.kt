package com.example.pstudy.view.home.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.pstudy.R
import com.example.pstudy.view.home.uistate.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class HomeViewModel : ViewModel() {
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    init {
        initData()
    }

    fun getItemTab(context: Context, position: Int) =
        context.getString(_homeUiState.value.tabLayoutItems[position])

    private fun initData() {
        _homeUiState.update {
            it.copy(
                tabLayoutItems = listOf(
                    R.string.home_tab_all,
                    R.string.home_tab_folders,
                    R.string.home_tab_shared
                )
            )
        }
    }
}