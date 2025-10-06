package com.example.base.viewmodel

data class UIState (
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)