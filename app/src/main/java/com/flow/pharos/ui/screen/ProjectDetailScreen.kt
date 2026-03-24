package com.flow.pharos.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flow.pharos.core.llm.JsonParser
import com.flow.pharos.ui.viewmodel.ProjectsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(projectId: String, viewModel: ProjectsViewModel, onBack: () -> Unit) {
    val projects by viewModel.projects.collectAsState()
    val projectFiles by viewModel.projectFiles.collectAsState()
    val fileAnalyses by viewModel.fileAnalyses.collectAsState()
    val project = projects.find { it.id == projectId }

    LaunchedEffect(projectId) { viewModel.loadProjectFiles(projectId) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(project?.name ?: "Project") },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } })

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            project?.let { p ->
                item {
                    Text(p.description, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${projectFiles.size} files", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
            items(projectFiles) { file ->
                val analysis = fileAnalyses[file.id]
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(file.name, style = MaterialTheme.typography.titleSmall)
                        analysis?.let { a ->
                            Text(a.summary, style = MaterialTheme.typography.bodySmall, maxLines = 2, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val topics = JsonParser.fromJsonArray(a.topics)
                            if (topics.isNotEmpty()) Text("Topics: ${topics.joinToString(", ")}", style = MaterialTheme.typography.labelSmall)
                        } ?: Text("Not yet analyzed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}
