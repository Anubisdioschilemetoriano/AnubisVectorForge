package com.anubis.vectorforge.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.anubis.vectorforge.data.Todo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "todos")

class TodoLocalDataSource(private val context: Context) {
    private val gson = Gson()
    private val TODOS_KEY = stringSetPreferencesKey("todos_list")

    fun getTodos(): Flow<List<Todo>> = context.dataStore.data.map { preferences ->
        val todosJson = preferences[TODOS_KEY] ?: emptySet()
        todosJson.mapNotNull { json ->
            try {
                gson.fromJson(json, Todo::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveTodo(todo: Todo) {
        context.dataStore.edit { preferences ->
            val currentTodos = preferences[TODOS_KEY]?.toMutableSet() ?: mutableSetOf()
            // Remove existing todo with same id if it exists
            currentTodos.removeIf { json ->
                try {
                    gson.fromJson(json, Todo::class.java).id == todo.id
                } catch (e: Exception) {
                    false
                }
            }
            // Add updated todo
            currentTodos.add(gson.toJson(todo))
            preferences[TODOS_KEY] = currentTodos
        }
    }

    suspend fun deleteTodo(todoId: String) {
        context.dataStore.edit { preferences ->
            val currentTodos = preferences[TODOS_KEY]?.toMutableSet() ?: mutableSetOf()
            currentTodos.removeIf { json ->
                try {
                    gson.fromJson(json, Todo::class.java).id == todoId
                } catch (e: Exception) {
                    false
                }
            }
            preferences[TODOS_KEY] = currentTodos
        }
    }

    suspend fun deleteAllTodos() {
        context.dataStore.edit { preferences ->
            preferences[TODOS_KEY] = emptySet()
        }
    }

    suspend fun updateTodo(todo: Todo) {
        saveTodo(todo)
    }
}
