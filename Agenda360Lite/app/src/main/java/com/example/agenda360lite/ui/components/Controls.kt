package com.example.agenda360lite.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, leadingIcon: ImageVector? = null) {
    ElevatedButton(onClick = onClick, modifier = modifier, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)) {
        if (leadingIcon != null) { Icon(leadingIcon, contentDescription = null) }
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun TonalButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, leadingIcon: ImageVector? = null) {
    FilledTonalButton(onClick = onClick, modifier = modifier, colors = ButtonDefaults.filledTonalButtonColors()) {
        if (leadingIcon != null) { Icon(leadingIcon, contentDescription = null) }
        Text(text)
    }
}

