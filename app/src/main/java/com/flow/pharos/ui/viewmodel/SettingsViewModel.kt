package com.flow.pharos.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flow.pharos.core.llm.AiApiProvider
import com.flow.pharos.core.storage.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val aiApiProvider: AiApiProvider
) : ViewModel() {
    val hasApiKey: StateFlow<Boolean> = settingsRepository.hasApiKey

    private val _testResult = MutableStateFlow<String?>(null)
    val testResult: StateFlow<String?> = _testResult.asStateFlow()

    private val _isTesting = MutableStateFlow(false)
    val isTesting: StateFlow<Boolean> = _isTesting.asStateFlow()

    val onlyChangedFiles: Boolean get() = settingsRepository.getOnlyChangedFiles()

    fun saveApiKey(key: String) { settingsRepository.saveApiKey(key) }
    fun deleteApiKey() { settingsRepository.deleteApiKey() }
    fun setOnlyChangedFiles(value: Boolean) { settingsRepository.setOnlyChangedFiles(value) }

    fun testApiKey() {
        viewModelScope.launch {
            _isTesting.value = true; _testResult.value = null
            try {
                val key = settingsRepository.getApiKey() ?: throw IllegalStateException("No API key")
                aiApiProvider.testApiKey(key)
                _testResult.value = "✓ API key is valid"
            } catch (e: Exception) { _testResult.value = "✗ ${e.message}" }
            _isTesting.value = false
        }
    }
}
