package com.flow.pharos.core.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey
    val id: String,
    val treeUri: String,
    val displayName: String,
    val createdAt: Long = System.currentTimeMillis()
)
