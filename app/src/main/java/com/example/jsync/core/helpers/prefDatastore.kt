package com.example.jsync.core.helpers

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map


val Context.datastore by preferencesDataStore("pref_data")
class prefDatastore(val context: Context) {
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val TO_BE_DELETED_KEY= stringSetPreferencesKey("to_be_deleted")
    suspend fun saveUserId(userId : String){
        Log.d("tag23" , "save user id is called")
        context.datastore.edit {
            it[USER_ID_KEY] = userId
        }

    }
    val userId = context.datastore.data.map {
        it[USER_ID_KEY]
    }.map { it ->
        Log.d("tag23"  , "userid is $it")
        it
    }
    suspend fun saveToBeDeleted(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TO_BE_DELETED_KEY] ?: emptySet()
            it[TO_BE_DELETED_KEY] = currentSet + id
        }
    }

    suspend fun removeToBeDeleted(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TO_BE_DELETED_KEY] ?: emptySet()
            it[TO_BE_DELETED_KEY] = currentSet - id
        }
    }
    suspend fun getToBeDeleted()  =  context.datastore.data.map { it ->
            it[TO_BE_DELETED_KEY] ?: emptySet()
        }

    suspend fun clearUserId(){
        context.datastore.edit {
            it.remove(USER_ID_KEY)
            it.remove(TO_BE_DELETED_KEY)
        }

    }



}