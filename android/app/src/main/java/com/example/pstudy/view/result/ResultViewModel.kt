package com.example.pstudy.view.result

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResultViewModel : ViewModel() {

    // Example LiveData for shared data - replace with actual data later
    private val _viewState = MutableStateFlow<ResultViewState>(ResultViewState())
    val viewState = _viewState.asStateFlow()

    // TODO: Add LiveData/StateFlow for data needed by Summary, Record, Material, etc.
    // For example:
    // private val _summaryData = MutableLiveData<SummaryInfo>()
    // val summaryData: LiveData<SummaryInfo> get() = _summaryData

    fun loadResultData() {
        // TODO: Implement logic to load data for all result sections
    }
}

data class ResultViewState(
    val isLoading: Boolean = true,
    val resultTitle: String = "Processing Result",
    val result: String? = null,
    val currentTab: Int = 0,
)