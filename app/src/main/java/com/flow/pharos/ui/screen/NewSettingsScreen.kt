package com.flow.pharos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.flow.pharos.ui.viewmodel.SettingsViewModel

@Composable
fun NewSettingsScreen(viewModel: SettingsViewModel) {
    val hasApiKey by viewModel.hasApiKey.collectAsState()
    val testResult by viewModel.testResult.collectAsState()
    val isTesting by viewModel.isTesting.collectAsState()
    var apiKeyInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)

        // API Key Section
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI API Key", style = MaterialTheme.typography.titleMedium)
                Text(if (hasApiKey) "✓ API key configured" else "No API key set", color = if (hasApiKey) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)

                OutlinedTextField(value = apiKeyInput, onValueChange = { apiKeyInput = it }, label = { Text("API Key") },
                    visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), singleLine = true)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { if (apiKeyInput.isNotBlank()) { viewModel.saveApiKey(apiKeyInput); apiKeyInput = "" } }, enabled = apiKeyInput.isNotBlank()) { Text("Save") }
                    if (hasApiKey) {
                        OutlinedButton(onClick = { viewModel.testApiKey() }, enabled = !isTesting) { Text(if (isTesting) "Testing..." else "Test") }
                        OutlinedButton(onClick = { viewModel.deleteApiKey() }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Delete") }
                    }
                }

                testResult?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            }
        }

        // Analysis mode
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Analysis Mode", style = MaterialTheme.typography.titleMedium)
                var onlyChanged by remember { mutableStateOf(viewModel.onlyChangedFiles) }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = onlyChanged, onCheckedChange = { onlyChanged = it; viewModel.setOnlyChangedFiles(it) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Only analyze changed files")
                }
            }
        }

        // Privacy notice
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Privacy", style = MaterialTheme.typography.titleMedium)
                Text("• API key encrypted with Android Keystore", style = MaterialTheme.typography.bodySmall)
                Text("• Documents sent to API only on explicit action", style = MaterialTheme.typography.bodySmall)
                Text("• No background syncs or analytics", style = MaterialTheme.typography.bodySmall)
                Text("• All data stored locally", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
