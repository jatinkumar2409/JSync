package com.example.jsync.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val prefDatastore: prefDatastore , private val manageToken : manageToken
) : ViewModel() {
    private val uiMode_ = MutableStateFlow(2)
    val uiMode = uiMode_.asStateFlow()
 init {
     getUiMode()
 }
    fun changeUiMode(uiMode: Int){
        viewModelScope.launch(Dispatchers.IO) {
            prefDatastore.saveUiMode(uiMode)
        }

    }
    fun getUiMode(){
        viewModelScope.launch(Dispatchers.IO) {
            prefDatastore.uiMode.collect { it ->
               uiMode_.value = it ?: 2
            }
        }
    }
    fun onLogOut(){
        viewModelScope.launch {
            prefDatastore.clearUserId()
            manageToken.clearToken()
        }
    }

}

