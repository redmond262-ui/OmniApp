package com.example.ui.screens

import com.example.util.Money

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaskCategory
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.AmberDeal
import com.example.ui.theme.AmberDealContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RosePromo
import com.example.ui.theme.SlateNavy

@Composable
fun DailyHubScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val needs by viewModel.needs.collectAsState()
    val wallet by viewModel.wallet.collectAsState()
    val contacts by viewModel.contacts.collectAsState()
    val newsList by viewModel.storeNews.collectAsState()
    val weatherAlerts by viewModel.weatherAlerts.collectAsState()
    val briefing by viewModel.morningBriefing.collectAsState()
    val attractions by viewModel.touristAttractions.collectAsState()
    val isFloatingPillVisible by viewModel.isFloatingShoppingPillVisible.collectAsState()

    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskTime by remember { mutableStateOf("10:00") }

    Box(modifier = modifier.fillMaxSize().testTag("daily_hub_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Cabecera Personalizada de Bienvenida
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = SlateNavy),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = briefing.dateText,
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Hola, Santiago",
                                    color = Color.White,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Botón de Compartir Estado o App
                            IconButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "¡Hola! Estoy usando OmniHub para organizar mi día, ahorrar en compras del barrio y viajar seguro: https://omnihub.app"
                                        )
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Compartir OmniHub"))
                                },
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), CircleShape)
                                    .size(38.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = "Compartir", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Frase inspiradora matutina
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AmberDeal, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "\"${briefing.motivationalQuote}\"",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 2. Alertas Meteorológicas & Catástrofes (Defensa Civil)
            if (weatherAlerts.isNotEmpty()) {
                val primaryAlert = weatherAlerts.first()
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(primaryAlert.severity.hexColor)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = primaryAlert.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "${primaryAlert.severity.label} • ${primaryAlert.validUntil}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = primaryAlert.description,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 Recomendación: ${primaryAlert.instructions}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                    }
                }
            }

            // 3. Tarjeta Morning Briefing (Notificación Silenciosa de Despertar)
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Resumen de Despertar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SlateNavy
                                )
                            }
                            Surface(
                                color = EmeraldContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Recibido silencioso 06:30 hs",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.WbSunny, contentDescription = null, tint = AmberDeal, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = briefing.temperatureRange,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SlateNavy
                                )
                                Text(
                                    text = briefing.weatherSummary,
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }
            }

            // 4. Métricas de Utilidad Personal (Ahorro del Mes + Billetera + Remises)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Widget Ahorro
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Ahorro Mes", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "$14.250", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateNavy)
                        }
                    }

                    // Widget Billetera OmniPay
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Wallet) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AmberDeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Saldo Billetera", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "$${Money.plain(wallet.balance, 0)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = SlateNavy)
                        }
                    }

                    // Widget Remises
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo(Screen.Contacts) }
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Icon(imageVector = Icons.Default.LocalTaxi, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Remises Zona", fontSize = 11.sp, color = Color.Gray)
                            Text(text = "${contacts.size} listos", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SlateNavy)
                        }
                    }
                }
            }

            // 5. Botón Limpio para Planificar un Viaje o Recorrido
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo(Screen.TripPlanner) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(EmeraldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Planificar Viaje o Recorrido",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDark
                            )
                            Text(
                                text = "¿Vas hacia El Obelisco o tu destino? Traza tu camino",
                                fontSize = 11.sp,
                                color = SlateNavy.copy(alpha = 0.8f)
                            )
                        }
                        Button(
                            onClick = { viewModel.navigateTo(Screen.TripPlanner) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Viajar 🗺️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 6. Novedades Flash de Comercios del Barrio
            if (newsList.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Avisos & Novedades del Barrio", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateNavy)
                            TextButton(onClick = { viewModel.navigateTo(Screen.StoresList) }) {
                                Text("Comercios", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(newsList) { news ->
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.width(260.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                color = AmberDealContainer,
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(text = news.badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberDeal, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                            }
                                            Text(text = news.timeAgoText, fontSize = 10.sp, color = Color.Gray)
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = news.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateNavy, maxLines = 1)
                                        Text(text = news.content, fontSize = 11.sp, color = Color.DarkGray, maxLines = 2, lineHeight = 14.sp)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = "Por: ${news.storeName}", fontSize = 10.sp, color = EmeraldDark, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 7. Modo Explorador: Atracciones Turísticas & Culturales
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Puntos de Interés & Atracciones", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SlateNavy)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(attractions) { att ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .width(240.dp)
                                    .clickable {
                                        viewModel.searchTrip("Tu Ubicación", att.name)
                                        viewModel.navigateTo(Screen.TripPlanner)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = EmeraldContainer,
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(text = att.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldDark, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                        Text(text = "${att.distanceKm} km", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateNavy)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = att.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SlateNavy, maxLines = 1)
                                    Text(text = att.description, fontSize = 11.sp, color = Color.Gray, maxLines = 2, lineHeight = 14.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "⏱️ ${att.estimatedVisitTime}", fontSize = 10.sp, color = Color.Gray)
                                        Text(text = "Trazar Ruta ➡️", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 8. Tareas Personales de Rutina
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Tus Tareas de Hoy", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SlateNavy)
                    TextButton(onClick = { showAddTaskDialog = true }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agregar", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                }
            }

            items(tasks) { task ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleTask(task.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleTask(task.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (task.isCompleted) EmeraldPrimary else Color.LightGray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                                color = if (task.isCompleted) Color.Gray else SlateNavy
                            )
                            Text(text = "${task.timeTag} hs • ${task.category.label}", fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // 9. BOTÓN / PÍLDORA FLOTANTE TIPO NOTIFICACIÓN DESPLEGABLE (Para abrir o cerrar lista de compras)
        AnimatedVisibility(
            visible = isFloatingPillVisible,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Surface(
                color = SlateNavy,
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                modifier = Modifier.clickable { viewModel.navigateTo(Screen.SmartList) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(EmeraldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Canasta: ${needs.size} productos",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Toca para ver o comparar",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Botón para cerrar la notificación flotante
                    IconButton(
                        onClick = { viewModel.toggleFloatingShoppingPill(false) },
                        modifier = Modifier.size(22.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Si cerraron la píldora flotante, un pequeño botón discreto para reabrirla
        if (!isFloatingPillVisible) {
            IconButton(
                onClick = { viewModel.toggleFloatingShoppingPill(true) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .background(SlateNavy, CircleShape)
                    .size(42.dp)
            ) {
                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Abrir Canasta", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }

    // Modal para agregar tarea
    if (showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text(text = "Nueva Tarea de Rutina") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        label = { Text("Actividad o pendiente") },
                        placeholder = { Text("Ej: Pagar servicio o regar plantas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTaskTime,
                        onValueChange = { newTaskTime = it },
                        label = { Text("Hora sugerida") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {
                            viewModel.addTask(newTaskTitle, TaskCategory.RUTINA, newTaskTime)
                            newTaskTitle = ""
                            showAddTaskDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
