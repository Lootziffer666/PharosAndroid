package com.flow.pharos.core.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.flow.pharos.core.model.FileStatus

@Entity(
    tableName = "files",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("folderId"), Index("documentUri", unique = true)]
)
data class FileEntity(
    @PrimaryKey
    val id: String,
    val folderId: String,
    val documentUri: String,
    val name: String,
    val size: Long,
    val lastModified: Long,
    val contentHash: String? = null,
    val mimeType: String,
    val status: FileStatus = FileStatus.NEVER,
    val lastAnalyzedAt: Long? = null,
    val failReason: String? = null
)
