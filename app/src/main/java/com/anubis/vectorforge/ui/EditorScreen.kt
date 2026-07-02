package com.anubis.vectorforge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.anubis.vectorforge.viewmodel.EditorViewModel

@Composable
fun EditorScreen(viewModel: EditorViewModel = hiltViewModel()) {
    var promptText by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = { Text("Prompt de IA") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { viewModel.generate(promptText) },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.loading) "Generando..." else "Generar Imagen")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (state.loading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
            } else if (state.image != null) {
                AsyncImage(
                    model = state.image,
                    contentDescription = "Imagen generada",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("No hay imagen generada")
            }
        }
    }
}
