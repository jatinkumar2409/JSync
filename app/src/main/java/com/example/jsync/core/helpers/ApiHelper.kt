package com.example.jsync.core.helpers

import io.ktor.client.plugins.ClientRequestException
import kotlinx.coroutines.delay

object ApiHelper {
    suspend fun  <T> callWithRetry(
        authenticator : TokenAuthenticator ,
        maxRetries : Int = 3 ,
        initialDelay : Long = 1000 ,
        block : suspend () -> T
    ) : T {
         while (true){
         var retries = 0
         var delayTime = initialDelay
         try {
             return block()
         }
         catch (e : ClientRequestException){
            if(e.response.status.value == 401){
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
          throw e
        }
        retries++
        delay(delayTime)
        delayTime *= 2
    }
        }
}