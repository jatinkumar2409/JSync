package com.example.jsync.data.auth.impls
import android.util.Log
import com.example.jsync.core.helpers.GetClient
import com.example.jsync.data.models.AuthTokens
import com.example.jsync.data.models.ErrorResponse
import com.example.jsync.domain.auth.repos.AuthRepository
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

class AuthRepoImplementation() : AuthRepository {
    private val client = GetClient.getClient(connectionTimeout = 15_000L , requestTimeout = 12_000L , socketTimeout = 12_000L)
    override suspend fun signUp(
        name: String,
        email: String,
        password: String
    ): Result<AuthTokens> {
        try {
           val response = client.post("/sign_up"){
               contentType(io.ktor.http.ContentType.Application.Json)
               setBody(SignUpObject(name , email , password))
           }
            Log.d("tag" , "response is $response")
            if(response.status.isSuccess()){
                return Result.success(response.body<AuthTokens>())
            }
            else{
                val error = response.bodyAsText()
                return Result.failure(Exception(error))
            }
        }
        catch (e : Exception){
            Log.d("tag" , e.message.toString())
          return Result.failure(e)
        }
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthTokens> {
        try {
            val response = client.post("/sign_in"){
                contentType(io.ktor.http.ContentType.Application.Json)
                setBody(SignInObject(email , password))
            }
            if(response.status.isSuccess()){
                return Result.success(response.body<AuthTokens>())
            }
            else {
                val error = response.body<ErrorResponse>()
                Log.d("tag" ,error.detail)
                return Result.failure(Exception(error.detail))
            }
        }
        catch (e : Exception){
            Log.d("tag" , e.message.toString())
            return Result.failure(e)
        }
    }
}

@Serializable
private data class SignUpObject(
    val name : String , val email : String , val password: String
)

@Serializable
private data class SignInObject(
    val email : String , val password: String
)


