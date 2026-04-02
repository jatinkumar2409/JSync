package com.example.jsync.core.helpers

import com.example.jsync.data.models.TaskDTO
import com.example.jsync.data.models.TaskForUi
import com.example.jsync.data.room.entities.SYNC_STATE
import com.example.jsync.data.room.entities.TaskEntity

fun TaskEntity.toTaskDto() = TaskDTO(
    id = this.id,
    taskName = this.taskName,
    userId = this.userId,
    dueAt = this.dueAt,
    type = this.type,
    priority = this.priority,
    hasDone = this.hasDone,
    tags = this.tags,
    updatedAt = this.updatedAt
)

fun TaskDTO.toTaskEntity(syncState : SYNC_STATE = SYNC_STATE.SYNCED) = TaskEntity(
    id = this.id,
    taskName = this.taskName,
    userId = this.userId,
    dueAt = this.dueAt,
    type = this.type,
    priority = this.priority,
    hasDone = this.hasDone,
    tags = this.tags,
    updatedAt = this.updatedAt,
    syncState = syncState
)

fun TaskDTO.toTaskForUi() = TaskForUi(
    id = this.id,
    taskName = this.taskName,
    dueAt = this.dueAt,
    type = this.type,
    priority = this.priority,
    hasDone = this.hasDone,
    tags = this.tags
)

fun TaskEntity.toTaskForUi() = TaskForUi(
    id = this.id,
    taskName = this.taskName,
    dueAt = this.dueAt,
    type = this.type,
    priority = this.priority,
    hasDone = this.hasDone,
    tags = this.tags,
    syncState = this.syncState
)