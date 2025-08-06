package com.jmin.monthlytodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText

@Composable
fun RichTextDisplay(
    html: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    val richTextState = rememberRichTextState()

    LaunchedEffect(html) {
        if (html.isNotEmpty()) {
            richTextState.setHtml(html)
        }
    }

    if (html.isNotEmpty()) {
        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            RichText(
                state = richTextState,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    } else {
        Text(
            text = "No description",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
    }
}

@Composable
fun RichTextPreview(
    html: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 3
) {
    val richTextState = rememberRichTextState()
    
    LaunchedEffect(html) {
        if (html.isNotEmpty()) {
            richTextState.setHtml(html)
        }
    }
    
    if (html.isNotEmpty()) {
        // 获取纯文本用于预览
        val plainText = richTextState.annotatedString.text
        
        if (plainText.isNotEmpty()) {
            Text(
                text = plainText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = maxLines,
                modifier = modifier
            )
        } else {
            Text(
                text = "包含富文本内容",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier
            )
        }
    }
}
