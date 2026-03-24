package com.flow.pharos.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flow.pharos.core.model.FileStatus
import com.flow.pharos.core.model.entity.FileEntity
import com.flow.pharos.ui.viewmodel.FilesViewModel

@Composable
fun FilesScreen(viewModel: FilesViewModel, onFileClick: (String) -> Unit) {
    val files by viewModel.files.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Files", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("${files.size} files indexed", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(16.dp))

        if (files.isEmpty()) {
            Text("No files indexed. Scan a folder from the Dashboard.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(files) { file ->
                    FileListItem(file = file, onClick = { onFileClick(file.id) })
                }
            }
        }
    }
}

@Composable
private fun FileListItem(file: FileEntity, onClick: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(statusIcon(file.status), contentDescription = file.status.name, tint = statusColor(file.status), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(file.name, style = MaterialTheme.typography.bodyLarge)
                Text(file.mimeType, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(file.status.name, style = MaterialTheme.typography.labelSmall, color = statusColor(file.status))
        }
    }
}

private fun statusIcon(status: FileStatus) = when (status) {
    FileStatus.NEVER -> Icons.Default.FiberNew
    FileStatus.UP_TO_DATE -> Icons.Default.CheckCircle
    FileStatus.STALE -> Icons.Default.Update
    FileStatus.FAILED -> Icons.Default.Error
    FileStatus.UNSUPPORTED -> Icons.Default.Block
}

private fun statusColor(status: FileStatus) = when (status) {
    FileStatus.NEVER -> Color(0xFF2196F3)
    FileStatus.UP_TO_DATE -> Color(0xFF4CAF50)
    FileStatus.STALE -> Color(0xFFF57C00)
    FileStatus.FAILED -> Color(0xFFE53935)
    FileStatus.UNSUPPORTED -> Color.Gray
}
