package com.example.jsync.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jsync.data.models.AiConversationBlock
import com.example.jsync.data.models.AiRequestDTO
import com.example.jsync.data.models.AiResponseDTO
import com.example.jsync.data.models.TaskDTO
import com.example.jsync.domain.tasks.repos.AskAiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AskAiViewModel(
    private val askAiRepository: AskAiRepository
) : ViewModel() {
    private val messages_ = MutableStateFlow<List<AiConversationBlock>>(emptyList())
    val messages = messages_.asStateFlow()
    val sendEnabled = mutableStateOf(true)
    fun askAi(message : String , tasks : List<TaskDTO> , onSuccess : () -> Unit , onError : (String) -> Unit){
        messages_.value += AiConversationBlock(isUser = true , message = message)
      viewModelScope.launch(Dispatchers.IO) {
          try {
              val response = askAiRepository.askAi(
                  AiRequestDTO(
                      tasks = tasks,
                      message = message
                  )
              )
              response.onSuccess { aiResponseDTO ->
                  messages_.value += AiConversationBlock(isUser = false , message = aiResponseDTO.response)
                  onSuccess()
              }
          }catch (e : Exception){
              onError(e.message.toString())
          }
      }
    }
    fun changeSendEnabled(state : Boolean){
        sendEnabled.value = state
    }
}