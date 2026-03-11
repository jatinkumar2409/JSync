package com.example.jsync.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.core.helpers.NetworkObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val networkObserver: NetworkObserver) : ViewModel() {
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus = _networkStatus.asStateFlow()
   init {
       observeNetwork()
   }
    fun observeNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
            networkObserver.observeNetwork().collect { it ->
                _networkStatus.value = it
            }
        }
    }
}