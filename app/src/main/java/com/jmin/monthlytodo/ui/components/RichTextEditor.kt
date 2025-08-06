package com.jmin.monthlytodo.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.jmin.monthlytodo.R
import com.mohamedrejeb.richeditor.ui.material3.RichText
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RichTextEditorDialog(
    initialHtml: String = "",
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val richTextState = rememberRichTextState()
    val context = LocalContext.current
    
    // 初始化富文本内容
    LaunchedEffect(initialHtml) {
        if (initialHtml.isNotEmpty()) {
            richTextState.setHtml(initialHtml)
        }
    }
    
    // 图片选择器
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            // 将图片转换为Base64并插入到编辑器
//            val base64Image = convertImageToBase64(context, it)
//            if (base64Image != null) {
//                // 插入图片HTML标签（使用Base64数据URI）
//                val imageHtml = "<img src=\"data:image/jpeg;base64,$base64Image\" style=\"max-width: 100%; height: auto;\" />"
//                // 获取当前HTML内容并添加图片
//                val currentHtml = richTextState.toHtml()
//                val newHtml = if (currentHtml.isEmpty()) {
//                    imageHtml
//                } else {
//                    "$currentHtml<br/>$imageHtml"
//                }
//                richTextState.setHtml(newHtml)
//            }
//        }
//    }
    
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // 标题栏
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Row {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.cancel))
                        }
                        
                        Button(
                            onClick = {
                                onSave(richTextState.toHtml())
                                onDismiss()
                            }
                        ) {
                            Text(stringResource(R.string.save))
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 工具栏
                RichTextToolbar(
                    richTextState = richTextState
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 富文本编辑器
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    RichTextEditor(
                        state = richTextState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        placeholder = {
                            Text(
                                text = "Please input your description",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // 状态信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "font size: ${richTextState.annotatedString.text.length}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (richTextState.annotatedString.text.isNotEmpty()) {
                        TextButton(
                            onClick = { richTextState.clear() }
                        ) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RichTextToolbar(
    richTextState: com.mohamedrejeb.richeditor.model.RichTextState
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 粗体按钮
            ToolbarButton(
                icon = Icons.Default.FormatBold,
                onClick = {
                    richTextState.toggleSpanStyle(
                        androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold)
                    )
                }
            )

            // 斜体按钮
            ToolbarButton(
                icon = Icons.Default.FormatItalic,
                onClick = {
                    richTextState.toggleSpanStyle(
                        androidx.compose.ui.text.SpanStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    )
                }
            )

            // 下划线按钮
            ToolbarButton(
                icon = Icons.Default.FormatUnderlined,
                onClick = {
                    richTextState.toggleSpanStyle(
                        androidx.compose.ui.text.SpanStyle(textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                    )
                }
            )
        }
    }
}

@Composable
fun ToolbarButton(
    icon: ImageVector,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}



// 将图片转换为Base64编码
private fun convertImageToBase64(context: Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)

        // 压缩图片以减少Base64大小
        val scaledBitmap = scaleBitmap(bitmap, 800, 600)

        val byteArrayOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        Base64.encodeToString(byteArray, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// 缩放图片以优化性能
private fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxWidth && height <= maxHeight) {
        return bitmap
    }

    val scaleWidth = maxWidth.toFloat() / width
    val scaleHeight = maxHeight.toFloat() / height
    val scale = minOf(scaleWidth, scaleHeight)

    val newWidth = (width * scale).toInt()
    val newHeight = (height * scale).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}
