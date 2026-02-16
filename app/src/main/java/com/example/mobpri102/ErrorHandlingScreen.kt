package com.example.mobpri102

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun ErrorHandlingScreen() {
    var result by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Область для результатов
        result?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Область для ошибок
        errorMessage?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "Ошибка: $it",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Кнопки
        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(true)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Успешная операция")
        }

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    try {
                        val res = riskyOperation(false)
                        result = res
                    } catch (e: Exception) {
                        errorMessage = e.message
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Операция с ошибкой")
        }

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    riskyFlow().collect { value ->
                        result = value
                        delay(500)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Flow с обработкой ошибок")
        }

        Button(
            onClick = {
                result = null
                errorMessage = null
                scope.launch {
                    val safeResult = safeOperation(false)
                    safeResult.fold(
                        onSuccess = { result = it },
                        onFailure = { errorMessage = it.message ?: "Неизвестная ошибка" }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Безопасная операция")
        }
    }
}

// Функции для демонстрации обработки ошибок
suspend fun riskyOperation(success: Boolean): String {
    delay(1000)
    if (!success) {
        throw IllegalStateException("Операция не удалась")
    }
    return "Операция выполнена успешно"
}

fun riskyFlow(): Flow<String> = flow {
    emit("Шаг 1")
    delay(500)
    emit("Шаг 2")
    delay(500)
    throw RuntimeException("Ошибка на шаге 3!")
    emit("Шаг 3")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}

suspend fun safeOperation(success: Boolean): Result<String> {
    return try {
        delay(1000)
        if (!success) {
            Result.failure(IllegalStateException("Операция не удалась"))
        } else {
            Result.success("Операция выполнена успешно")
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}