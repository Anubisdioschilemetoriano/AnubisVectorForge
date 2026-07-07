package com.anubis.vectorforge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anubis.vectorforge.data.Todo
import com.anubis.vectorforge.data.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val todoRepository: TodoRepository
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadTodos()
    }

    private fun loadTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            todoRepository.getTodos().collect { todoList ->
                _todos.value = todoList.sortedByDescending { it.createdAt }
                _isLoading.value = false
            }
        }
    }

    fun addTodo(title: String, description: String = "") {
        if (title.isBlank()) return

        viewModelScope.launch {
            val newTodo = Todo(
                title = title.trim(),
                description = description.trim()
            )
            todoRepository.addTodo(newTodo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
        }
    }

    fun toggleTodoCompletion(todoId: String) {
        viewModelScope.launch {
            todoRepository.toggleTodoCompletion(todoId, _todos.value)
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            todoRepository.deleteTodo(todoId)
        }
    }

    fun deleteAllTodos() {
        viewModelScope.launch {
            todoRepository.deleteAllTodos()
        }
    }

    fun getCompletedCount(): Int = _todos.value.count { it.isCompleted }

    fun getPendingCount(): Int = _todos.value.count { !it.isCompleted }
}
