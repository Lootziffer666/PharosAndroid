package com.flow.pharos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flow.pharos.core.llm.JsonParser
import com.flow.pharos.ui.viewmodel.FilesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailScreen(fileId: String, viewModel: FilesViewModel, onBack: () -> Unit) {
    val files by viewModel.files.collectAsState()
    val analysis by viewModel.selectedFileAnalysis.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val file = files.find { it.id == fileId }

    LaunchedEffect(fileId) { viewModel.loadFileAnalysis(fileId) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(file?.name ?: "File Detail") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } })

        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            file?.let { f ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("File Info", style = MaterialTheme.typography.titleMedium)
                        Text("Name: ${f.name}"); Text("Type: ${f.mimeType}"); Text("Size: ${f.size} bytes"); Text("Status: ${f.status}")
                        f.failReason?.let { Text("Error: $it", color = MaterialTheme.colorScheme.error) }
                    }
                }

                Button(onClick = { viewModel.analyzeSingleFile(fileId) }, enabled = !isAnalyzing, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isAnalyzing) "Analyzing..." else "Analyze This File")
                }

                analysis?.let { a ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Analysis Results", style = MaterialTheme.typography.titleMedium)
                            Text("Summary", style = MaterialTheme.typography.labelLarge)
                            Text(a.summary)
                            val topics = JsonParser.fromJsonArray(a.topics)
                            if (topics.isNotEmpty()) { Text("Topics", style = MaterialTheme.typography.labelLarge); Text(topics.joinToString(", ")) }
                            val actions = JsonParser.fromJsonArray(a.actionItems)
                            if (actions.isNotEmpty()) { Text("Action Items", style = MaterialTheme.typography.labelLarge); actions.forEach { Text("• $it") } }
                            Text("Confidence: ${"%.0f".format(a.confidence * 100)}%", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } ?: Text("File not found")
        }
    }
}
