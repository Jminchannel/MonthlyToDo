// 备用的简单全屏编辑器实现
// 如果当前实现仍有问题，可以使用这个更简单的版本

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleFullScreenDescriptionEditor(
    description: String,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // 最简单的实现 - 使用AlertDialog
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("编辑描述")
        },
        text = {
            Column {
                Text(
                    text = "字数: ${description.length}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("任务描述") },
                    placeholder = { Text("输入详细描述...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp), // 固定高度，避免溢出
                    singleLine = false
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        },
        dismissButton = {
            if (description.isNotEmpty()) {
                TextButton(onClick = { onDescriptionChange("") }) {
                    Text("清空")
                }
            }
        }
    )
}

// 或者更简单的内联编辑方案
@Composable
fun ExpandableDescriptionField(
    description: String,
    onDescriptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("描述") },
            modifier = Modifier.fillMaxWidth(),
            minLines = if (isExpanded) 8 else 2,
            maxLines = if (isExpanded) 12 else 4,
            singleLine = false,
            trailingIcon = {
                IconButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "收起" else "展开"
                    )
                }
            }
        )
        
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "字数: ${description.length}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (description.isNotEmpty()) {
                    TextButton(
                        onClick = { onDescriptionChange("") }
                    ) {
                        Text("清空")
                    }
                }
            }
        }
    }
}
