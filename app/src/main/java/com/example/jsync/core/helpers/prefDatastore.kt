package com.example.jsync.core.helpers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


val Context.datastore by preferencesDataStore("pref_data")
class prefDatastore(val context: Context) {
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    suspend fun saveUserId(userId : String){
        context.datastore.edit {
            it[USER_ID_KEY] = userId
        }

    }
    val userId = context.datastore.data.map {
        it[USER_ID_KEY]
    }
    suspend fun clearUserId(){
        context.datastore.edit {
            it.remove(USER_ID_KEY)
        }

    }


}