package com.example.jsync.domain.auth.usecases

import android.util.Log
import com.example.jsync.core.helpers.manageToken
import com.example.jsync.core.helpers.prefDatastore
import com.example.jsync.domain.auth.repos.AuthRepository

class SignInUseCase(private val authRepo : AuthRepository , private val manageToken: manageToken ,
    private val prefDatastore: prefDatastore){
    suspend fun signIn(email : String , password : String , onSuccess : () -> Unit , onError : (String) -> Unit){
        try {
            val result = authRepo.signIn(email , password)
            result.onSuccess { it ->
                Log.d("tag" , it.toString())
                manageToken.saveTokens(it.accessToken , it.refreshToken)
                prefDatastore.saveUserId(it.userId)
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