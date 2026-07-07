package com.anubis.vectorforge.data.repository

import com.anubis.vectorforge.data.Todo
import com.anubis.vectorforge.data.local.TodoLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val localDataSource: TodoLocalDataSource
) {
    fun getTodos(): Flow<List<Todo>> = localDataSource.getTodos()

    suspend fun addTodo(todo: Todo) {
        localDataSource.saveTodo(todo)
    }

    suspend fun updateTodo(todo: Todo) {
        localDataSource.updateTodo(todo)
    }

    suspend fun deleteTodo(todoId: String) {
        localDataSource.deleteTodo(todoId)
    }

    suspend fun toggleTodoCompletion(todoId: String, todos: List<Todo>) {
        val todo = todos.find { it.id == todoId }
        todo?.let {
            localDataSource.updateTodo(it.copy(isCompleted = !it.isCompleted))
        }
    }

    suspend fun deleteAllTodos() {
        localDataSource.deleteAllTodos()
    }
}
