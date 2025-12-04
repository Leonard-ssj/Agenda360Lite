package com.example.agenda360lite.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EmptyState(message: String, ctaText: String? = null, onCta: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (ctaText != null && onCta != null) {
            Button(onClick = onCta, modifier = Modifier.padding(top = 8.dp)) { Text(ctaText) }
        }
    }
}

