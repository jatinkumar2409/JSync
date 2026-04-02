package com.example.jsync.core.helpers

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object GetClient {
    fun getClient(connectionTimeout : Long , requestTimeout : Long , socketTimeout : Long) = HttpClient {
        install(ContentNegotiation){
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
        install(DefaultRequest){
            url {
              protocol = URLProtocol.HTTP
              host = "192.168.40.241"
              port = 8000
            }
        }
        install(HttpTimeout){
            connectTimeoutMillis = connectionTimeout
            requestTimeoutMillis = requestTimeout
            socketTimeoutMillis = socketTimeout
        }
    }
}