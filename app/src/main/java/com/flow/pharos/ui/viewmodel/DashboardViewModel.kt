package com.flow.pharos.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flow.pharos.core.storage.repository.FileRepository
import com.flow.pharos.core.storage.repository.FolderRepository
import com.flow.pharos.core.storage.repository.SettingsRepository
import com.flow.pharos.usecase.AnalysisProgress
import com.flow.pharos.usecase.AnalysisUseCase
import com.flow.pharos.usecase.MasterfileUseCase
import com.flow.pharos.usecase.ProjectClusteringUseCase
import com.flow.pharos.usecase.ScanProgress
import com.flow.pharos.usecase.ScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val folderRepository: FolderRepository,
    private val fileRepository: FileRepository,
    private val settingsRepository: SettingsRepository,
    private val scanUseCase: ScanUseCase,
    private val analysisUseCase: AnalysisUseCase,
    private val projectClusteringUseCase: ProjectClusteringUseCase,
    private val masterfileUseCase: MasterfileUseCase
) : ViewModel() {

    val totalFileCount: StateFlow<Int> = fileRepository.getTotalFileCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val changedFileCount: StateFlow<Int> = fileRepository.getChangedFileCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val analyzedFileCount: StateFlow<Int> = fileRepository.getAnalyzedFileCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val hasApiKey: StateFlow<Boolean> = settingsRepository.hasApiKey

    private val _scanProgress = MutableStateFlow<ScanProgress?>(null)
    val scanProgress: StateFlow<ScanProgress?> = _scanProgress.asStateFlow()

    private val _analysisProgress = MutableStateFlow<AnalysisProgress?>(null)
    val analysisProgress: StateFlow<AnalysisProgress?> = _analysisProgress.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _isUpdatingMasterfiles = MutableStateFlow(false)
    val isUpdatingMasterfiles: StateFlow<Boolean> = _isUpdatingMasterfiles.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var scanJob: Job? = null
    private var analysisJob: Job? = null

    fun scanFolders() {
        if (_isScanning.value) return
        scanJob = viewModelScope.launch {
            _isScanning.value = true; _statusMessage.value = null
            try {
                val folders = folderRepository.getAllFoldersList()
                if (folders.isEmpty()) { _statusMessage.value = "No folder configured. Go to Folders tab."; return@launch }
                folders.forEach { folder ->
                    val treeUri = Uri.parse(folder.treeUri)
                    scanUseCase.scanFolder(folder.id, treeUri) { progress -> _scanProgress.value = progress }
                }
                _statusMessage.value = "Scan completed."
            } catch (e: Exception) { _statusMessage.value = "Scan failed: ${e.message}"
            } finally { _isScanning.value = false; _scanProgress.value = null }
        }
    }

    fun startAnalysis() {
        if (_isAnalyzing.value) return
        analysisJob = viewModelScope.launch {
            _isAnalyzing.value = true; _statusMessage.value = null
            try {
                val result = analysisUseCase.analyzeFiles { progress -> _analysisProgress.value = progress }
                projectClusteringUseCase.clusterProjects()
                _statusMessage.value = "Analysis: ${result.succeeded} OK, ${result.failed} failed, ${result.skipped} skipped."
            } catch (e: Exception) { _statusMessage.value = "Analysis failed: ${e.message}"
            } finally { _isAnalyzing.value = false; _analysisProgress.value = null }
        }
    }

    fun updateMasterfiles() {
        if (_isUpdatingMasterfiles.value) return
        viewModelScope.launch {
            _isUpdatingMasterfiles.value = true; _statusMessage.value = null
            try {
                val result = masterfileUseCase.updateMasterfiles { }
                _statusMessage.value = "${result.filesWritten} masterfiles updated."
            } catch (e: Exception) { _statusMessage.value = "Masterfile update failed: ${e.message}"
            } finally { _isUpdatingMasterfiles.value = false }
        }
    }

    fun cancelScan() { scanJob?.cancel(); _isScanning.value = false; _scanProgress.value = null }
    fun cancelAnalysis() { analysisJob?.cancel(); _isAnalyzing.value = false; _analysisProgress.value = null }
    fun clearStatusMessage() { _statusMessage.value = null }
}
