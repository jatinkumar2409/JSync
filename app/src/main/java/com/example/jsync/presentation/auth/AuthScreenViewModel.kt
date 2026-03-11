package com.example.jsync.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.core.helpers.NetworkObserver
import com.example.jsync.domain.auth.repos.AuthRepository
import com.example.jsync.domain.auth.usecases.SignInUseCase
import com.example.jsync.domain.auth.usecases.SignUpUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AuthScreenViewModel(
private val signUpUseCase : SignUpUseCase ,
private val signInUseCase : SignInUseCase ,
private val networkObserver : NetworkObserver
) : ViewModel(){
    private val _networkStatus = MutableStateFlow(false)
    val networkStatus = _networkStatus.asStateFlow()

    init {
        observeNetwork()
    }
     fun signUp(name : String , email : String , password : String , onSuccess : () -> Unit , onError : (String) -> Unit){
        viewModelScope.launch {
            signUpUseCase.signUp(name , email , password , onSuccess , onError)
        }
    }
    fun signIn(email : String , password: String , onSuccess: () -> Unit , onError: (String) -> Unit){
        viewModelScope.launch {
            signInUseCase.signIn(email , password , onSuccess , onError)
        }
    }
    fun observeNetwork(){
        viewModelScope.launch(Dispatchers.IO) {
            networkObserver.observeNetwork().collect { it ->
                _networkStatus.value = it
            }
        }
    }
}