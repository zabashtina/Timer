package com.example.first

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimerApp()
        }
    }
}

@Composable
fun TimerApp() {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var seconds by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf("00:00:00") }
    var progress by remember { mutableStateOf(1f) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var remainingMillis by remember { mutableStateOf(0L) }
    var timer: CountDownTimer? by remember { mutableStateOf(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ввод времени
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumberInput("Часы", value = hours) { hours = it }
            Spacer(modifier = Modifier.width(8.dp))
            NumberInput("Минуты", value = minutes) { minutes = it }
            Spacer(modifier = Modifier.width(8.dp))
            NumberInput("Секунды", value = seconds) { seconds = it }
        }

        // Текст таймера
        Text(
            text = timeLeft,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        // Прогресс бар
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Кнопки
        Row {
            Button(
                onClick = {
                    val totalTimeInMillis =
                        if (!isPaused) (hours * 3600 + minutes * 60 + seconds) * 1000L else remainingMillis
                    progress = 1f
                    timer?.cancel()
                    timer = object : CountDownTimer(totalTimeInMillis, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            remainingMillis = millisUntilFinished
                            val h = (millisUntilFinished / 1000) / 3600
                            val m = ((millisUntilFinished / 1000) % 3600) / 60
                            val s = (millisUntilFinished / 1000) % 60
                            timeLeft = String.format("%02d:%02d:%02d", h, m, s)
                            progress = millisUntilFinished / totalTimeInMillis.toFloat()
                        }

                        override fun onFinish() {
                            timeLeft = "00:00:00"
                            progress = 0f
                            isRunning = false
                            isPaused = false
                        }
                    }.start()
                    isRunning = true
                    isPaused = false
                },
                enabled = !isRunning,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Запуск")
            }

            Button(
                onClick = {
                    timer?.cancel()
                    isRunning = false
                    isPaused = true
                },
                enabled = isRunning,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text("Остановить")
            }

            Button(
                onClick = {
                    timer?.cancel()
                    isRunning = false
                    isPaused = false
                    timeLeft = "00:00:00"
                    progress = 1f
                    // Сбрасываем значения
                    hours = 0
                    minutes = 0
                    seconds = 0
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Сброс")
            }
        }
    }
}

@Composable
fun NumberInput(label: String, value: Int, onValueChange: (Int) -> Unit) {
    var text by remember { mutableStateOf(value.toString()) }  // Инициализируем текст из value

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        TextField(
            value = text,
            onValueChange = { newValue ->
                text = newValue.filter { it.isDigit() } // Убираем нецифровые символы
                onValueChange(text.toIntOrNull() ?: 0)
            },
            modifier = Modifier
                .width(80.dp)
                .clickable { text = "" }, // Очищаем поле при каждом нажатии
            singleLine = true,
            placeholder = { Text("0") }
        )
    }

    // Синхронизация text с value после изменений
    LaunchedEffect(value) {
        text = value.toString()
    }
}