package com.anubis.vectorforge.ui.screen

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    fun generateImage(prompt: String) {
        println("Generando imagen con prompt: $prompt")
        // Aquí irá la llamada al backend después
    }
}
