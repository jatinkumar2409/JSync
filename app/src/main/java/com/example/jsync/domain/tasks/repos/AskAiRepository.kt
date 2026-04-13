package com.example.jsync.domain.tasks.repos

import com.example.jsync.data.models.AiRequestDTO
import com.example.jsync.data.models.AiResponseDTO

interface AskAiRepository {
    suspend fun askAi(aiRequest : AiRequestDTO) : Result<AiResponseDTO>
}