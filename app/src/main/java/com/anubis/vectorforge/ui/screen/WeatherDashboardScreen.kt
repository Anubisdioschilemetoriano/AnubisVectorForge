package com.anubis.vectorforge.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wind
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.anubis.vectorforge.data.model.WeatherResponse
import com.anubis.vectorforge.ui.viewmodel.WeatherUiState
import com.anubis.vectorforge.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

@Composable
fun WeatherDashboardScreen(
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Dashboard") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search Bar
            SearchWeatherBar(
                searchQuery = searchQuery,
                onSearchQueryChanged = { viewModel.updateSearchQuery(it) },
                onSearch = {
                    viewModel.searchWeatherByCity(searchQuery)
                    keyboardController?.hide()
                }
            )

            // Content based on state
            when (weatherState) {
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Buscando clima...")
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    val weather = (weatherState as WeatherUiState.Success).weather
                    WeatherContent(weather)
                }

                is WeatherUiState.Error -> {
                    val errorMessage = (weatherState as WeatherUiState.Error).message
                    ErrorMessage(
                        message = errorMessage,
                        onRetry = { viewModel.clearError() }
                    )
                }

                is WeatherUiState.Idle -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Busca una ciudad para ver el clima",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchWeatherBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = { Text("Busca una ciudad...") },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        singleLine = true
    )
}

@Composable
fun WeatherContent(weather: WeatherResponse) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // City and Main Condition
        WeatherHeaderCard(weather)

        // Temperature Details
        TemperatureCard(weather.mainInfo)

        // Wind and Visibility
        WindAndVisibilityCard(weather)

        // Additional Info
        AdditionalInfoCard(weather)
    }
}

@Composable
fun WeatherHeaderCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "${weather.cityName}, ${weather.systemInfo.country}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = weather.weatherList.firstOrNull()?.main ?: "N/A",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Text(
                text = weather.weatherList.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Text(
                text = formatDate(weather.timestamp * 1000),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun TemperatureCard(mainInfo: com.anubis.vectorforge.data.model.MainWeatherInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TemperatureItem(
                    label = "Actual",
                    temperature = mainInfo.temperature,
                    modifier = Modifier.weight(1f)
                )
                TemperatureItem(
                    label = "Sensación",
                    temperature = mainInfo.feelsLike,
                    modifier = Modifier.weight(1f)
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TemperatureItem(
                    label = "Mínima",
                    temperature = mainInfo.tempMin,
                    modifier = Modifier.weight(1f)
                )
                TemperatureItem(
                    label = "Máxima",
                    temperature = mainInfo.tempMax,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TemperatureItem(
    label: String,
    temperature: Double,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "${temperature.roundToInt()}°C",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun WindAndVisibilityCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Wind
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Wind,
                    contentDescription = "Wind",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Viento",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${weather.wind.speed.roundToInt()} m/s",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Humidity
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Filled.Cloud,
                    contentDescription = "Humidity",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Humedad",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "${weather.mainInfo.humidity}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

@Composable
fun AdditionalInfoCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoRow("Presión", "${weather.mainInfo.pressure} hPa")
            Divider()
            InfoRow("Visibilidad", "${(weather.visibility / 1000.0).roundToInt()} km")
            Divider()
            InfoRow("Nubosidad", "${weather.clouds.cloudiness}%")
            Divider()
            InfoRow("Amanecer", formatTime(weather.systemInfo.sunrise * 1000))
            InfoRow("Atardecer", formatTime(weather.systemInfo.sunset * 1000))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

@Composable
fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "❌ Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Intentar de nuevo")
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale("es", "ES"))
    return sdf.format(Date(timestamp))
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
