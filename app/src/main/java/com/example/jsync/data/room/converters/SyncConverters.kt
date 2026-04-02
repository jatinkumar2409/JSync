package com.example.jsync.data.room.converters

import androidx.room.TypeConverter
import com.example.jsync.data.room.entities.SYNC_STATE

class SyncConverters {
    @TypeConverter
    fun from_sync_state_to_string(value : SYNC_STATE) = value.name

    @TypeConverter
    fun from_string_to_sync_state(value : String) = SYNC_STATE.valueOf(value)
}