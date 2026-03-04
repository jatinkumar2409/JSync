package com.example.jsync.domain.auth.repos

import com.example.jsync.data.models.AuthTokens

interface AuthRepository {
    suspend fun signUp(name : String , email : String , password : String) : Result<AuthTokens>
    suspend fun signIn(email : String , password: String) : Result<AuthTokens>
}