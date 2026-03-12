package com.example.jsync.core.helpers

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class manageToken(context : Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context , "auth_prefs" , MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build() ,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV ,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    fun saveTokens(access : String , refresh : String , userId : String){
        prefs.edit {
            putString("access_token", access)
            putString("refresh_token", refresh)
            putString("user_id" , userId)
        }

    }
    fun getAccessToken() = prefs.getString("access_token" , null)
    fun getRefreshToken() = prefs.getString("refresh_token" , null)
    fun getUserId() = prefs.getString("user_id" , null)
    fun clearToken(){
        prefs.edit { clear() }
    }
}