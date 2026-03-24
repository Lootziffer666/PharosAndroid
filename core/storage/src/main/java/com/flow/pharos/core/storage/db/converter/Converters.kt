package com.flow.pharos.core.storage.db.converter

import androidx.room.TypeConverter
import com.flow.pharos.core.model.FileStatus

class Converters {
    @TypeConverter
    fun fromFileStatus(status: FileStatus): String = status.name

    @TypeConverter
    fun toFileStatus(value: String): FileStatus = FileStatus.valueOf(value)
}
