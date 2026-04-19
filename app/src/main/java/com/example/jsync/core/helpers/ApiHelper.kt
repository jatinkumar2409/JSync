package com.example.jsync.core.helpers

import android.util.Log
import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.delay

object RetryRequest {
    suspend fun  <T> callWithRetry(
        authenticator : TokenAuthenticator ,
        maxRetries : Int = 3 ,
        initialDelay : Long = 1000 ,
        block : suspend () -> T
    ) : T {
        var retries = 0
        var delayTime = initialDelay
         while (true){
         try {
             return block()
         }
         catch (e : ClientRequestException){
             Log.d("error1" , "error is ${e.message}")
            if(e.response.status.value == 401){
                Log.d("tag" , "401 error coming...")
              val token =   authenticator.rotateAccessToken();

                if(token.trim().isNotEmpty()) {
                    return block()
                }
                else{
                    throw Exception("Failed to rotate token")
                }
            }
             if(e.response.status.value in 402..409){
                 throw e
             }
             if(retries >= maxRetries) throw e
         }
        catch (e : Exception){
            Log.d("error" , "error is ${e.message}")
          throw e
        }
        retries++
        delay(delayTime)
        delayTime *= 2
    }
        }
}