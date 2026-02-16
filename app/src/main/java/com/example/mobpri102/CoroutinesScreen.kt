package com.example.mobpri102

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*

@Composable
fun CoroutinesScreen() {
    var isLoading by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator()
        }

        // Результат (исправлено)
        result?.let { textResult ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = textResult,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Кнопки
        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    val res = simulateLongOperation(2000)
                    result = res
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить долгую операцию")
        }

        Button(
            onClick = {
                isLoading = true
                result = null
                scope.launch {
                    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                    val sum = calculateSum(numbers)
                    result = "Сумма чисел: $sum"
                    isLoading = false
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вычислить сумму")
        }
    }
}

// Suspend функции (добавьте их в этот же файл или создайте отдельный)
suspend fun simulateLongOperation(duration: Long): String {
    delay(duration)
    return "Операция завершена за $duration мс"
}

suspend fun calculateSum(numbers: List<Int>): Int {
    return withContext(Dispatchers.Default) {
        delay(1000) // Имитация вычислений
        numbers.sum()
    }
}