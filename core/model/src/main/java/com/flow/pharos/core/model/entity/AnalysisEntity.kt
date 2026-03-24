package com.flow.pharos.core.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "analyses",
    foreignKeys = [
        ForeignKey(
            entity = FileEntity::class,
            parentColumns = ["id"],
            childColumns = ["fileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fileId")]
)
data class AnalysisEntity(
    @PrimaryKey
    val id: String,
    val fileId: String,
    val summary: String,
    val topics: String,
    val projectSuggestions: String,
    val actionItems: String,
    val confidence: Double,
    val rawResponse: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
