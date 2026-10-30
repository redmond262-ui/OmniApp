package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Store
import com.example.model.StoreType
import com.example.ui.MainViewModel
import com.example.ui.StoreScheduleFilter
import com.example.ui.components.DistancePill
import com.example.ui.components.OpenStatusBadge
import com.example.ui.components.PulsingStatusDot
import com.example.ui.components.RatingBadge
import com.example.ui.components.StoreTypeChip
import com.example.ui.components.VideoTourTag
import com.example.ui.components.getStoreGradient
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MedicalServices
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RosePromo
import com.example.ui.theme.SlateNavy

@Composable
fun StoresListScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val stores by viewModel.stores.collectAsState()
    val scheduleFilter by viewModel.scheduleFilter.collectAsState()
    val simulatedHour by viewModel.simulatedHour.collectAsState()
    val isSimulatedHoliday by viewModel.isSimulatedHoliday.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<StoreType?>(null) }

    val filteredAndRankedStores = remember(stores, searchQuery, selectedType, scheduleFilter, simulatedHour, isSimulatedHoliday) {
        stores.filter { store ->
            val matchesSearch = searchQuery.isBlank() ||
                store.name.contains(searchQuery, ignoreCase = true) ||
                store.address.contains(searchQuery, ignoreCase = true) ||
                store.description.contains(searchQuery, ignoreCase = true)

            val matchesType = selectedType == null || store.type == selectedType

            val matchesSchedule = when (scheduleFilter) {
                StoreScheduleFilter.TODOS -> {
                    if (isSimulatedHoliday) {
                        store.openOnHolidays || store.is24Hours || store.isOnDuty
                    } else if (simulatedHour < 7 || simulatedHour >= 22) {
                        // Horario no habitual / nocturno
                        store.is24Hours || store.isOnDuty
                    } else {
                        true
                    }
                }
                StoreScheduleFilter.ABIERTOS_AHORA -> store.isEffectivelyOpen
                StoreScheduleFilter.FARMACIAS_TURNO -> store.type == StoreType.FARMACIA || store.isOnDuty
                StoreScheduleFilter.TURNO_24HS -> store.is24Hours || store.isOnDuty
                StoreScheduleFilter.FERIADOS -> store.openOnHolidays || store.is24Hours || store.isOnDuty
            }

            matchesSearch && matchesType && matchesSchedule
        }.sortedWith(
            // Priorizar los que están abiertos o de turno en ese horario sobre los cerrados
            compareByDescending<Store> {
                when {
                    it.isOnDuty -> 4
                    it.is24Hours -> 3
                    it.isOpen -> 2
                    it.openOnHolidays && isSimulatedHoliday -> 2
                    else -> 0
                }
            }.thenBy { it.distanceMeters }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("stores_list_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Barra de Búsqueda
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar comercios, farmacias, kioscos o súper...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EmeraldPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar")
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_stores")
            )
        }

        // Filtro por Horarios & Guardia (Requerimiento Clave)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtro por Horario & Guardia:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateNavy
                    )
                    Text(
                        text = "${filteredAndRankedStores.size} encontrados",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(StoreScheduleFilter.values()) { filter ->
                        FilterChip(
                            selected = scheduleFilter == filter,
                            onClick = { viewModel.setScheduleFilter(filter) },
                            label = { Text(filter.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EmeraldPrimary,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_schedule_${filter.name}")
                        )
                    }
                }
            }
        }

        // Barra de Simulación Horaria (Para probar horarios no habituales o días no laborables)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = SlateNavy, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Simular Situación Horaria:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SlateNavy)
                        }

                        if (isSimulatedHoliday || simulatedHour == 2) {
                            Surface(
                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Filtrando por Guardia activa",
                                    color = EmeraldDark,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = if (simulatedHour == 14 && !isSimulatedHoliday) EmeraldPrimary else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.setSimulatedHour(14)
                                    if (isSimulatedHoliday) viewModel.toggleSimulatedHoliday()
                                    viewModel.setScheduleFilter(StoreScheduleFilter.TODOS)
                                }
                        ) {
                            Text(
                                text = "☀️ Horario Normal (14hs)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (simulatedHour == 14 && !isSimulatedHoliday) Color.White else SlateNavy,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }

                        Surface(
                            color = if (simulatedHour == 2) Color(0xFF0F172A) else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.setSimulatedHour(2)
                                    viewModel.setScheduleFilter(StoreScheduleFilter.TURNO_24HS)
                                }
                        ) {
                            Text(
                                text = "🌙 Madrugada (02:00 am)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (simulatedHour == 2) Color.White else SlateNavy,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }

                        Surface(
                            color = if (isSimulatedHoliday) Color(0xFFD97706) else Color.White,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    viewModel.toggleSimulatedHoliday()
                                    viewModel.setScheduleFilter(StoreScheduleFilter.FERIADOS)
                                }
                        ) {
                            Text(
                                text = "📅 Día Feriado",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSimulatedHoliday) Color.White else SlateNavy,
                                modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Filtro por tipos de comercio
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("Rubro: Todos", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                items(StoreType.values()) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = if (selectedType == type) null else type },
                        label = { Text(type.label, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Resultados con ranking de comercios abiertos arriba
        if (filteredAndRankedStores.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.Bedtime, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(text = "No hay comercios con este filtro horario", fontWeight = FontWeight.Bold, color = SlateNavy, fontSize = 14.sp)
                        Text(text = "Prueba seleccionando 'Todos' o consulta las Farmacias de Turno 24hs disponibles en el barrio.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }
        } else {
            items(filteredAndRankedStores, key = { it.id }) { store ->
                StoreDirectoryCard(
                    store = store,
                    onOpenDetail = { viewModel.selectStoreAndOpenDetail(store) },
                    onOpenRoute = { viewModel.selectStoreAndOpenRoute(store) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StoreDirectoryCard(
    store: Store,
    onOpenDetail: () -> Unit,
    onOpenRoute: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetail() }
            .testTag("directory_store_${store.id}")
    ) {
        Column {
            // Cabecera Visual estilo Booking
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(getStoreGradient(store.type))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    StoreTypeChip(type = store.type)
                    if (store.hasVideoTour) {
                        VideoTourTag(duration = store.videoTourDuration)
                    }
                }

                // Info inferior de cabecera con Badge de estado y Pelotita Verde Parpadeando
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OpenStatusBadge(
                        isOpen = store.isOpen,
                        is24Hours = store.is24Hours,
                        isOnDuty = store.isOnDuty,
                        openOnHolidays = store.openOnHolidays
                    )

                    Surface(
                        color = Color.Black.copy(alpha = 0.55f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "📷 ${store.photosCount} fotos disponibles",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Cuerpo
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (store.isEffectivelyOpen) {
                            PulsingStatusDot(color = Color(0xFF10B981), sizeDp = 8.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Text(
                            text = store.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateNavy
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    DistancePill(
                        distanceMeters = store.distanceMeters,
                        walkingMinutes = store.walkingTimeMin
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingBadge(rating = store.rating, reviewsCount = store.reviewsCount)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = store.hoursText,
                        fontSize = 11.sp,
                        fontWeight = if (store.isEffectivelyOpen) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (store.isEffectivelyOpen) EmeraldDark else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = store.address,
                    fontSize = 12.sp,
                    color = SlateNavy.copy(alpha = 0.8f)
                )

                if (store.scheduleNotes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🔔 ${store.scheduleNotes}",
                            fontSize = 10.sp,
                            color = SlateNavy,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Promo banner
                Surface(
                    color = RosePromo.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalOffer,
                            contentDescription = null,
                            tint = RosePromo,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = store.specialOfferText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RosePromo
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Acciones: Ver Ficha y Botón Directo al Mapa con Distancia
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenDetail,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(0.42f)
                    ) {
                        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ficha", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onOpenRoute,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier
                            .weight(0.58f)
                            .testTag("btn_route_store_${store.id}")
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ver en Mapa (${store.distanceText})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
