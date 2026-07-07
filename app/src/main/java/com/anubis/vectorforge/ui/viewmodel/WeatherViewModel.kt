package com.anubis.vectorforge.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anubis.vectorforge.data.model.WeatherResponse
import com.anubis.vectorforge.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class WeatherUiState {
    object Loading : WeatherUiState()
    data class Success(val weather: WeatherResponse) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
    object Idle : WeatherUiState()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchWeatherByCity(cityName: String) {
        if (cityName.isBlank()) {
            _weatherState.value = WeatherUiState.Error("Ingresa un nombre de ciudad")
            return
        }

        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val result = weatherRepository.getCurrentWeatherByCity(cityName)
            _weatherState.value = result.fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = { WeatherUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun searchWeatherByCoordinates(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            val result = weatherRepository.getCurrentWeatherByCoordinates(latitude, longitude)
            _weatherState.value = result.fold(
                onSuccess = { WeatherUiState.Success(it) },
                onFailure = { WeatherUiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }

    fun clearError() {
        _weatherState.value = WeatherUiState.Idle
    }
}
