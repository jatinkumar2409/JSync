package com.example.jsync.core.helpers

import android.util.Log
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
        Log.d("error" , "Rotating access token...")
        val refreshToken = manageToken.getRefreshToken() ?: ""
        val accessToken = manageToken.getAccessToken() ?: run {
            throw Exception("Access token not found")
        }
        if (refreshToken.trim().isEmpty()){
            throw Exception("Refresh token not found")
        }
        try {
            val response = client.get("/refresh_token"){
                header("Authorization" , "Bearer $refreshToken")
            }
            if (response.status.isSuccess()){
                val body: Map<String, String> = response.body()
                val newAccessToken = body["new_token"] ?: run {
                    throw Exception("New access token not found")
                }
                Log.d("error2" , "Access token rotated successfully with $newAccessToken")
                manageToken.saveTokens(access = newAccessToken , refresh = refreshToken)
                return newAccessToken
            }
            else{
                val error = response.bodyAsText()
                Log.d("error2" ,  " Error is $error")
                throw Exception(error)
            }
        }
        catch (e : Exception){
            Log.d("error2" , "Access token rotated successfully with ${e.message}")
            throw Exception("Failed to rotate token : ${e.message}")
        }

    }

}