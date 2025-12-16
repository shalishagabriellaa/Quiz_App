package com.example.tubes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tubes.domain.repository.HomeRepository

class TestInformationViewModelFactory(
    private val repo: HomeRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TestInformationViewModel(repo) as T
    }
}
