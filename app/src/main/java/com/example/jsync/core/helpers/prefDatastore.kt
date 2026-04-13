package com.example.jsync.core.helpers

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map


val Context.datastore by preferencesDataStore("pref_data")
class prefDatastore(val context: Context) {
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val TASKS_TO_BE_DELETED_KEY= stringSetPreferencesKey("tasks_to_be_deleted")
    private val TASK_COMPLETIONS_TO_BE_DELETED_KEY = stringSetPreferencesKey("task_completions_to_be_deleted")

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
    suspend fun saveToBeDeletedTasks(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TASKS_TO_BE_DELETED_KEY] ?: emptySet()
            it[TASKS_TO_BE_DELETED_KEY] = currentSet + id
        }
    }
    suspend fun saveToBeDeletedTaskCompletions(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TASK_COMPLETIONS_TO_BE_DELETED_KEY] ?: emptySet()
            it[TASK_COMPLETIONS_TO_BE_DELETED_KEY] = currentSet + id
        }
    }

    suspend fun removeToBeDeletedTasks(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TASKS_TO_BE_DELETED_KEY] ?: emptySet()
            it[TASKS_TO_BE_DELETED_KEY] = currentSet - id
        }
    }
    suspend fun removeToBeDeletedTaskCompletions(id : String){
        context.datastore.edit { it ->
            val currentSet : Set<String> = it[TASK_COMPLETIONS_TO_BE_DELETED_KEY] ?: emptySet()
            it[TASK_COMPLETIONS_TO_BE_DELETED_KEY] = currentSet - id
        }
    }
    fun getToBeDeletedTaskCompletions() = context.datastore.data.map { it ->
        it[TASK_COMPLETIONS_TO_BE_DELETED_KEY] ?: emptySet()
    }

     fun getToBeDeletedTasks()  =  context.datastore.data.map { it ->
            it[TASKS_TO_BE_DELETED_KEY] ?: emptySet()
        }

    suspend fun clearUserId(){
        context.datastore.edit {
            it.remove(USER_ID_KEY)
            it.remove(TASKS_TO_BE_DELETED_KEY)
            it.remove(TASK_COMPLETIONS_TO_BE_DELETED_KEY)
        }

    }



}