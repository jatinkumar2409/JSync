package com.example.jsync.core.helpers

import com.example.jsync.data.models.ErrorResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

class TokenAuthenticator(
    private val manageToken: manageToken
) {
    val client = GetClient.getClient(
        connectionTimeout = 12_000L , requestTimeout = 15_000 , socketTimeout = 15_000
    )
    suspend fun rotateAccessToken() : String{
        val refreshToken = manageToken.getRefreshToken() ?: ""
        if (refreshToken.trim().isEmpty()){
            throw Exception("Refresh token not found")
        }
        try {
            val response = client.get("/rotate_token"){
                header("Authorization" , "Bearer $refreshToken")
            }
            if (response.status.isSuccess()){
                val newAccessToken = response.bodyAsText()
                manageToken.saveTokens(access = newAccessToken , refresh = refreshToken)
                return newAccessToken
            }
            else{
                val error = response.body<ErrorResponse>()
                throw Exception(error.detail)
            }
        }
        catch (e : Exception){
            throw Exception("Failed to rotate token : ${e.message}")
        }

    }

}