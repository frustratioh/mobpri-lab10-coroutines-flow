package com.example.mobpri102

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
fun FlowScreen() {
    var flowValues by remember { mutableStateOf<List<String>>(emptyList()) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Список значений
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(flowValues) { value ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = value,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Кнопки управления
        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    numberFlow().collect { value ->
                        flowValues = flowValues + "Число: $value"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить Flow")
        }

        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    transformedFlow(numberFlow()).collect { value ->
                        flowValues = flowValues + "Квадрат четного: $value"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить преобразованный Flow")
        }

        Button(
            onClick = {
                flowValues = emptyList()
                scope.launch {
                    errorFlow().collect { value ->
                        flowValues = flowValues + value
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Запустить Flow с ошибкой")
        }
    }
}

// Flow функции
fun numberFlow(): Flow<Int> = flow {
    for (i in 1..10) {
        delay(500)
        emit(i)
    }
}

fun transformedFlow(flow: Flow<Int>): Flow<Int> = flow
    .map { it * it }
    .filter { it % 2 == 0 }

fun errorFlow(): Flow<String> = flow {
    emit("Первое значение")
    delay(500)
    emit("Второе значение")
    delay(500)
    throw RuntimeException("Произошла ошибка!")
}.catch { exception ->
    emit("Ошибка обработана: ${exception.message}")
}