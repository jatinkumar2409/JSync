package com.example.jsync.core.helpers.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jsync.data.models.AiConversationBlock
import com.example.jsync.ui.theme.blue20
import com.example.jsync.ui.theme.blue40
import com.example.jsync.ui.theme.blue80
import dev.jeziellago.compose.markdowntext.MarkdownText

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AskAiModal(
    onDismiss : () -> Unit = {} ,
    sendEnabled : Boolean = true ,
    messages : List<AiConversationBlock> = listOf(
        AiConversationBlock(isUser = true , message = "HI this is the message from AI that i will convo with AI"),
        AiConversationBlock(isUser = false , message = "HI this is the message from AI that i will convo with AI"),
        AiConversationBlock(isUser = true , message = "HI this is the message from AI that i will convo with AI"),

    ) , onSend : (String) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var currentMessage by rememberSaveable {
        mutableStateOf("")
    }
    ModalBottomSheet(
        onDismissRequest = onDismiss ,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f) ,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = messages
                ){ messageBlock ->
                     AiConversationMessage(messageBlock)
                }

            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                shape = CircleShape,
                value = currentMessage , onValueChange = {
                    currentMessage = it
                } , maxLines = 1
                , trailingIcon = {
                    IconButton(enabled = sendEnabled , onClick = {
                         currentMessage = ""
                         onSend(currentMessage)
                    } , colors = IconButtonDefaults.iconButtonColors(
                        containerColor = if (sendEnabled) blue40 else Color.Gray , contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )) {
                        if (sendEnabled) {
                            Icon(
                                imageVector = Icons.Default.Send, contentDescription = "send"
                            )
                        }
                        else{
                            CircularProgressIndicator()
                        }
                    }
                } ,
                placeholder = {
                    Text(
                        text = "Ask AI to manage your tasks"
                    )
                }
            )
        }
    }
}

@Composable
fun AiConversationMessage(messageBlock : AiConversationBlock = AiConversationBlock(isUser = true , message = "HI this is the message from AI that i will convo with AI")) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        Row(
            modifier = Modifier.fillMaxWidth() ,
            horizontalArrangement = if (messageBlock.isUser) Arrangement.End else Arrangement.Start
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.7f), colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) blue20 else blue80,
                    contentColor = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            ) {
                if (messageBlock.isUser) {
                    Text(
                        text = messageBlock.message, modifier = Modifier.padding(8.dp)
                    )
                }
                else{
                    MarkdownText(
                        markdown = messageBlock.message,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
