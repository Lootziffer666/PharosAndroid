package com.flow.pharos.core.storage.repository

import com.flow.pharos.core.model.entity.AnalysisEntity
import com.flow.pharos.core.storage.db.dao.AnalysisDao
import kotlinx.coroutines.flow.Flow

class AnalysisRepository(private val analysisDao: AnalysisDao) {
    suspend fun getLatestAnalysisForFile(fileId: String): AnalysisEntity? = analysisDao.getLatestAnalysisForFile(fileId)
    fun getLatestAnalysisForFileFlow(fileId: String): Flow<AnalysisEntity?> = analysisDao.getLatestAnalysisForFileFlow(fileId)
    suspend fun getAllAnalyses(): List<AnalysisEntity> = analysisDao.getAllAnalyses()
    suspend fun insertAnalysis(analysis: AnalysisEntity) = analysisDao.insertAnalysis(analysis)
    suspend fun deleteAnalysesForFile(fileId: String) = analysisDao.deleteAnalysesForFile(fileId)
}
