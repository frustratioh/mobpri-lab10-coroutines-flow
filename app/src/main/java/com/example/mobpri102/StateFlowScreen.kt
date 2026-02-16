package com.example.mobpri102

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@Composable
fun StateFlowScreen() {
    // Создаем StateFlow
    val counterStateFlow = remember { MutableStateFlow(0) }
    val counter: StateFlow<Int> = counterStateFlow.asStateFlow()
    val counterValue by counter.collectAsState()

    val isAutoIncrementingStateFlow = remember { MutableStateFlow(false) }
    val isAutoIncrementing: StateFlow<Boolean> = isAutoIncrementingStateFlow.asStateFlow()
    val isAutoIncrementingValue by isAutoIncrementing.collectAsState()

    val scope = rememberCoroutineScope()
    var autoIncrementJob by remember { mutableStateOf<Job?>(null) }

    // Функции для управления счетчиком
    fun increment() {
        counterStateFlow.value += 1
    }

    fun decrement() {
        counterStateFlow.value -= 1
    }

    fun reset() {
        counterStateFlow.value = 0
    }

    fun incrementBy(value: Int) {
        counterStateFlow.value += value
    }

    fun toggleAutoIncrement() {
        if (isAutoIncrementingValue) {
            // Останавливаем автоинкремент
            isAutoIncrementingStateFlow.value = false
            autoIncrementJob?.cancel()
            autoIncrementJob = null
        } else {
            // Запускаем автоинкремент
            isAutoIncrementingStateFlow.value = true
            autoIncrementJob = scope.launch {
                while (true) {
                    delay(1000)
                    counterStateFlow.value += 1
                }
            }
        }
    }

    // Отменяем корутину при выходе из композиции
    DisposableEffect(Unit) {
        onDispose {
            autoIncrementJob?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Отображение счетчика
        Text(
            text = counterValue.toString(),
            style = MaterialTheme.typography.displayLarge,
            fontSize = 64.sp
        )

        // Индикатор автоинкремента
        if (isAutoIncrementingValue) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                Text("Автоинкремент активен")
            }
        }

        // Кнопки управления
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = { decrement() }) { Text("-1") }
            Button(onClick = { increment() }) { Text("+1") }
        }

        Button(
            onClick = { reset() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сброс")
        }

        Button(
            onClick = { incrementBy(5) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+5")
        }

        Button(
            onClick = { toggleAutoIncrement() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAutoIncrementingValue)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isAutoIncrementingValue) "Остановить" else "Запустить автоинкремент")
        }
    }
}