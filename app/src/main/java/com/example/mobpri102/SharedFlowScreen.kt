package com.example.mobpri102

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

@Composable
fun SharedFlowScreen() {
    // Создаем SharedFlow с replay = 3
    val eventsSharedFlow = remember { MutableSharedFlow<String>(replay = 3) }
    val eventsFlow: SharedFlow<String> = eventsSharedFlow.asSharedFlow()

    var events by remember { mutableStateOf<List<String>>(emptyList()) }
    var eventCount by remember { mutableStateOf(0) }
    var eventCounter by remember { mutableStateOf(0) }
    var isAutoGenerating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var autoGenerationJob by remember { mutableStateOf<Job?>(null) }

    // Собираем события из SharedFlow
    LaunchedEffect(Unit) {
        eventsFlow.collect { event ->
            events = (events + event).takeLast(10) // Храним только последние 10
            eventCount++
        }
    }

    // Функция для эмита события
    fun emitEvent(message: String) {
        scope.launch {
            eventsSharedFlow.emit(message)
        }
    }

    fun startAutoGeneration() {
        if (autoGenerationJob?.isActive == true) return
        isAutoGenerating = true
        autoGenerationJob = scope.launch {
            while (true) {
                delay(2000)
                eventCounter++
                val randomNumber = Random.nextInt(1, 101)
                emitEvent("Событие #$eventCounter: $randomNumber")
            }
        }
    }

    fun stopAutoGeneration() {
        isAutoGenerating = false
        autoGenerationJob?.cancel()
        autoGenerationJob = null
    }

    // Отменяем корутину при выходе из композиции
    DisposableEffect(Unit) {
        onDispose {
            autoGenerationJob?.cancel()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Индикатор количества событий
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "Всего событий: $eventCount",
                modifier = Modifier.padding(16.dp)
            )
        }

        // Список событий
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(events.reversed()) { event ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = event,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }

        // Кнопки управления
        Button(
            onClick = {
                emitEvent("Ручное событие #${eventCount + 1}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сгенерировать событие")
        }

        Button(
            onClick = {
                if (isAutoGenerating) {
                    stopAutoGeneration()
                } else {
                    startAutoGeneration()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAutoGenerating)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isAutoGenerating) "Остановить автогенерацию" else "Запустить автогенерацию")
        }
    }
}