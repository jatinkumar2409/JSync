package com.example.jsync.domain.auth.usecases

import com.example.jsync.core.helpers.manageToken
import com.example.jsync.domain.auth.repos.AuthRepository

class SignUpUseCase(private val authRepo : AuthRepository , private val manageToken : manageToken) {
    suspend fun signUp(name : String , email : String , password : String , onSuccess : () -> Unit , onError : (String) -> Unit){
        try {
            val result = authRepo.signUp(name , email , password)
            result.onSuccess { it ->
                manageToken.saveTokens(it.accessToken , it.refreshToken)
                onSuccess()
            }
            result.onFailure { it ->
                onError(it.message.toString())
            }
        }
        catch (e : Exception){
            onError(e.message.toString())
        }
    }
}