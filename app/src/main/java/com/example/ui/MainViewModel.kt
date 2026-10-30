package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.remote.GeoRepository
import com.example.data.remote.WeatherRepository
import com.example.model.LatLon
import com.example.model.TripStep
import com.example.util.Money
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal
import com.example.data.SampleData
import com.example.model.ComboDeal
import com.example.model.ContactCategory
import com.example.model.DailyTask
import com.example.model.DietaryPreference
import com.example.model.NeedItem
import com.example.model.NeighborhoodContact
import com.example.model.ProductCategory
import com.example.model.SmartSuggestion
import com.example.model.Store
import com.example.model.StoreComparisonResult
import com.example.model.StoreNews
import com.example.model.StoreProduct
import com.example.model.TaskCategory
import com.example.model.TransactionType
import com.example.model.TripRoute
import com.example.model.UserProfile
import com.example.model.UserWallet
import com.example.model.WalletTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class StoreScheduleFilter(val label: String, val iconDescription: String) {
    TODOS("Todos", "Todos los comercios"),
    ABIERTOS_AHORA("Abiertos Ahora 🟢", "Comercios con atención en este momento"),
    FARMACIAS_TURNO("Farmacias de Turno 💊", "Guardia de 24hs y urgencias médicas"),
    TURNO_24HS("24 Horas / Guardia 🌙", "Atención continua día y noche"),
    FERIADOS("Abren Feriados 📅", "Atención en días no laborables")
}

sealed class Screen(val title: String) {
    object DailyHub : Screen("Tu Día")
    object SmartList : Screen("Mi Lista")
    object StoresList : Screen("Comercios")
    object Comparison : Screen("Comparador")
    object StoreDetail : Screen("Detalle del Local")
    object InteractiveRoute : Screen("Ruta Interactiva")
    object Wallet : Screen("Billetera OmniPay")
    object Contacts : Screen("Remises & Servicios")
    object UserProfile : Screen("Mi Cuenta & Ajustes")
    object MerchantHub : Screen("Panel de Comercio")
    object TripPlanner : Screen("Buscador de Viaje")
}

class MainViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow<Screen>(Screen.DailyHub)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _tasks = MutableStateFlow<List<DailyTask>>(SampleData.initialTasks)
    val tasks: StateFlow<List<DailyTask>> = _tasks.asStateFlow()

    private val _needs = MutableStateFlow<List<NeedItem>>(SampleData.initialNeeds)
    val needs: StateFlow<List<NeedItem>> = _needs.asStateFlow()

    // Comercios de demostración (con catálogo de ejemplo). Los comercios reales llegan de OpenStreetMap.
    private val demoStores: List<Store> = SampleData.sampleStores.map {
        it.copy(isDemo = true, description = "[DEMO] " + it.description)
    }

    private val _stores = MutableStateFlow<List<Store>>(demoStores)
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _selectedStore = MutableStateFlow<Store?>(demoStores.first())
    val selectedStore: StateFlow<Store?> = _selectedStore.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<ProductCategory?>(null)
    val selectedCategoryFilter: StateFlow<ProductCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtros de Horarios y Disponibilidad
    private val _scheduleFilter = MutableStateFlow(StoreScheduleFilter.TODOS)
    val scheduleFilter: StateFlow<StoreScheduleFilter> = _scheduleFilter.asStateFlow()

    // Simulación de Horario / Feriado para ver qué comercios abren en horas inusuales
    private val _simulatedHour = MutableStateFlow(14)
    val simulatedHour: StateFlow<Int> = _simulatedHour.asStateFlow()

    private val _isSimulatedHoliday = MutableStateFlow(false)
    val isSimulatedHoliday: StateFlow<Boolean> = _isSimulatedHoliday.asStateFlow()

    // Billetera Virtual y Pagos
    private val _wallet = MutableStateFlow(UserWallet())
    val wallet: StateFlow<UserWallet> = _wallet.asStateFlow()

    // Contactos y Remises Barriales
    private val _contacts = MutableStateFlow<List<NeighborhoodContact>>(SampleData.sampleContacts)
    val contacts: StateFlow<List<NeighborhoodContact>> = _contacts.asStateFlow()

    // Sugerencias Inteligentes
    private val _suggestions = MutableStateFlow<List<SmartSuggestion>>(SampleData.sampleSuggestions)
    val suggestions: StateFlow<List<SmartSuggestion>> = _suggestions.asStateFlow()

    // Perfil de Usuario y Ajustes
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Modo Negocio / Comerciante (Mi Comercio Administrado)
    private val _managedStore = MutableStateFlow(demoStores.first())
    val managedStore: StateFlow<Store> = _managedStore.asStateFlow()

    // Muro de Noticias Barriales
    private val _storeNews = MutableStateFlow<List<StoreNews>>(SampleData.sampleNews)
    val storeNews: StateFlow<List<StoreNews>> = _storeNews.asStateFlow()

    // Buscador de Viajes y Recorridos
    private val _tripOrigin = MutableStateFlow("Tu Ubicación")
    val tripOrigin: StateFlow<String> = _tripOrigin.asStateFlow()

    private val _tripDestination = MutableStateFlow("El Obelisco (Centro)")
    val tripDestination: StateFlow<String> = _tripDestination.asStateFlow()

    private val _currentTrip = MutableStateFlow(SampleData.calculateTrip(_tripOrigin.value, _tripDestination.value))
    val currentTrip: StateFlow<TripRoute> = _currentTrip.asStateFlow()

    // Alertas Meteorológicas & Catástrofes
    private val _weatherAlerts = MutableStateFlow<List<com.example.model.WeatherAlert>>(emptyList())
    val weatherAlerts: StateFlow<List<com.example.model.WeatherAlert>> = _weatherAlerts.asStateFlow()

    // Resumen Matutino de Despertar (Morning Briefing)
    private val _morningBriefing = MutableStateFlow(
        SampleData.sampleMorningBriefing.copy(
            weatherSummary = "Cargando clima…",
            temperatureRange = "--"
        )
    )
    val morningBriefing: StateFlow<com.example.model.MorningBriefing> = _morningBriefing.asStateFlow()

    // Comprobantes Digitales y Facturas
    private val _receipts = MutableStateFlow(SampleData.sampleReceipts)
    val receipts: StateFlow<List<com.example.model.DigitalReceipt>> = _receipts.asStateFlow()

    // Atracciones Turísticas Cercanas (Modo Viajero)
    private val _touristAttractions = MutableStateFlow(SampleData.sampleTouristAttractions)
    val touristAttractions: StateFlow<List<com.example.model.TouristAttraction>> = _touristAttractions.asStateFlow()

    // Botón flotante tipo notificación para lista de compras
    private val _isFloatingShoppingPillVisible = MutableStateFlow(true)
    val isFloatingShoppingPillVisible: StateFlow<Boolean> = _isFloatingShoppingPillVisible.asStateFlow()

    // Punto de interés interactivo seleccionado en el camino
    private val _selectedWaypointLandmark = MutableStateFlow<String?>(null)
    val selectedWaypointLandmark: StateFlow<String?> = _selectedWaypointLandmark.asStateFlow()

    // Un único flujo que se recalcula solo cuando cambian comercios o lista de compras.
    val comparisons: StateFlow<List<StoreComparisonResult>> =
        combine(_stores, _needs) { stores, needs -> SampleData.calculateComparison(stores, needs) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                SampleData.calculateComparison(_stores.value, _needs.value)
            )

    fun toggleFloatingShoppingPill(visible: Boolean) {
        _isFloatingShoppingPillVisible.value = visible
    }

    fun selectWaypointLandmark(name: String?) {
        _selectedWaypointLandmark.value = name
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun selectStoreAndOpenDetail(store: Store) {
        _selectedStore.value = store
        _currentScreen.value = Screen.StoreDetail
    }

    fun selectStoreAndOpenRoute(store: Store) {
        _selectedStore.value = store
        _currentScreen.value = Screen.InteractiveRoute
    }

    fun selectStoreAndPlanTripFrom(store: Store) {
        _tripOrigin.value = store.name
        _currentScreen.value = Screen.TripPlanner
        if (store.lat != null && store.lon != null) {
            searchTrip(store.name, _tripDestination.value)
        } else {
            _currentTrip.value = SampleData.calculateTrip(store.name, _tripDestination.value)
            _tripStatus.value = "Comercio de demostración: mostrando una estimación de ejemplo."
        }
    }

    // ---------- Datos reales (OpenStreetMap + Open-Meteo) ----------

    private val _userLocation = MutableStateFlow<LatLon?>(null)
    val userLocation: StateFlow<LatLon?> = _userLocation.asStateFlow()

    private val _dataStatus = MutableStateFlow<String?>("Buscando tu ubicación…")
    val dataStatus: StateFlow<String?> = _dataStatus.asStateFlow()

    private val _tripStatus = MutableStateFlow<String?>(
        "Mostrando un ejemplo. Toca «Trazar Recorrido del Viaje» para calcular una ruta real."
    )
    val tripStatus: StateFlow<String?> = _tripStatus.asStateFlow()

    private val _isTripLoading = MutableStateFlow(false)
    val isTripLoading: StateFlow<Boolean> = _isTripLoading.asStateFlow()

    private var tripJob: Job? = null
    private var refreshJob: Job? = null

    private class PlaceNotFound(val query: String) : Exception()

    /** Lo llama la Activity cuando resolvió (o no pudo resolver) la ubicación del usuario. */
    fun onLocationResolved(point: LatLon?) {
        _userLocation.value = point
        refreshRealData(point ?: DEFAULT_LOCATION, approximate = point == null)
    }

    fun refreshRealData() {
        val loc = _userLocation.value
        refreshRealData(loc ?: DEFAULT_LOCATION, approximate = loc == null)
    }

    private fun refreshRealData(center: LatLon, approximate: Boolean) {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            _dataStatus.value = "Actualizando clima y comercios…"
            val weatherJob = async { runCatching { WeatherRepository.fetch(center) } }
            val storesJob = async {
                runCatching {
                    GeoRepository.nearbyStores(center, _userProfile.value.searchRadiusMeters.coerceAtLeast(500))
                }
            }
            val problems = mutableListOf<String>()
            if (approximate) {
                problems += "Sin ubicación: usando Buenos Aires como referencia. Activa el permiso de ubicación para ver tu zona."
            }

            val weather = weatherJob.await()
            val snapshot = weather.getOrNull()
            if (snapshot != null) {
                _weatherAlerts.value = WeatherRepository.buildAlerts(snapshot)
                _morningBriefing.value = WeatherRepository.buildBriefing(
                    s = snapshot,
                    firstName = _userProfile.value.fullName.substringBefore(" "),
                    pendingTasks = _tasks.value.count { !it.isCompleted },
                    activeDeals = _morningBriefing.value.activeDealsCount,
                    quote = _morningBriefing.value.motivationalQuote
                )
            } else {
                _weatherAlerts.value = emptyList()
                _morningBriefing.value = _morningBriefing.value.copy(
                    weatherSummary = "Clima no disponible (revisa tu conexión)",
                    temperatureRange = "--"
                )
                problems += "No pude obtener el clima."
            }

            val realStores = storesJob.await().getOrNull()
            if (realStores != null) {
                val kept = _stores.value.filter { it.lat == null }
                _stores.value = realStores + kept
                if (realStores.isEmpty()) problems += "No encontré comercios en OpenStreetMap dentro de tu radio de búsqueda."
            } else {
                problems += "No pude obtener comercios cercanos."
            }

            _dataStatus.value = problems.takeIf { it.isNotEmpty() }?.joinToString(" ")
        }
    }

    fun searchTrip(origin: String, destination: String) {
        _tripOrigin.value = origin
        _tripDestination.value = destination
        tripJob?.cancel()
        tripJob = viewModelScope.launch {
            _isTripLoading.value = true
            _tripStatus.value = "Calculando ruta real con OpenStreetMap…"
            try {
                val near = _userLocation.value ?: DEFAULT_LOCATION
                val from = resolvePlace(origin, near) ?: throw PlaceNotFound(origin)
                val to = resolvePlace(destination, near) ?: throw PlaceNotFound(destination)

                val (car, foot) = coroutineScope {
                    val carJob = async { GeoRepository.route(from, to, GeoRepository.Profile.CAR) }
                    val footJob = async { runCatching { GeoRepository.route(from, to, GeoRepository.Profile.FOOT) }.getOrNull() }
                    carJob.await() to footJob.await()
                }

                _currentTrip.value = TripRoute(
                    originName = origin.ifBlank { "Tu Ubicación" },
                    destinationName = destination.ifBlank { "Destino" },
                    distanceKm = car.distanceKm,
                    walkingMinutes = foot?.durationMinutes ?: maxOf(1, (car.distanceKm * 14).toInt()),
                    drivingMinutes = car.durationMinutes,
                    transitMinutes = maxOf(8, (car.distanceKm * 4.5).toInt()),
                    estimatedRemisCost = Math.round((1200.0 + car.distanceKm * 480.0) / 10.0) * 10.0,
                    steps = car.steps.map {
                        TripStep(it.instruction, formatStepDistance(it.distanceMeters), it.subtext)
                    },
                    landmarksOnRoute = emptyList(),
                    geometry = car.geometry,
                    originPoint = from,
                    destinationPoint = to,
                    isRealRoute = true
                )
                _tripStatus.value = "Ruta real de OpenStreetMap. El tiempo en transporte público y el costo del remis son estimaciones."
            } catch (e: CancellationException) {
                throw e
            } catch (e: PlaceNotFound) {
                _tripStatus.value = "No encontré «${e.query}». Prueba con calle, número y ciudad."
            } catch (e: Exception) {
                _currentTrip.value = SampleData.calculateTrip(origin, destination)
                _tripStatus.value = "No pude conectar con el servicio de rutas: muestro una estimación de ejemplo."
            } finally {
                _isTripLoading.value = false
            }
        }
    }

    private suspend fun resolvePlace(text: String, near: LatLon): LatLon? {
        val t = text.trim()
        if (t.isEmpty() || t.contains("tu ubicación", ignoreCase = true) || t.contains("tu ubicacion", ignoreCase = true)) {
            return _userLocation.value ?: near
        }
        _stores.value.firstOrNull { it.name == t && it.lat != null && it.lon != null }?.let {
            return LatLon(it.lat!!, it.lon!!)
        }
        val cleaned = t.replace(Regex("\\(.*?\\)"), "").trim().ifBlank { t }
        return GeoRepository.geocode(cleaned, near)?.point
    }

    private fun formatStepDistance(meters: Double): String =
        if (meters < 1000) "${meters.toInt()} m" else "${Money.dec1(meters / 1000.0)} km"

    fun toggleTask(taskId: String) {
        _tasks.value = _tasks.value.map {
            if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
        }
    }

    fun addTask(title: String, category: TaskCategory, timeTag: String) {
        val newTask = DailyTask(
            id = UUID.randomUUID().toString(),
            title = title,
            category = category,
            isCompleted = false,
            timeTag = timeTag.ifBlank { "09:00" }
        )
        _tasks.value = listOf(newTask) + _tasks.value
    }

    fun toggleNeed(needId: String) {
        _needs.value = _needs.value.map {
            if (it.id == needId) it.copy(isChecked = !it.isChecked) else it
        }
    }

    fun addNeed(name: String, category: ProductCategory, quantity: Int, unit: String) {
        val newNeed = NeedItem(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            quantity = quantity,
            unit = unit,
            isChecked = false
        )
        _needs.value = _needs.value + newNeed
    }

    fun deleteNeed(needId: String) {
        _needs.value = _needs.value.filter { it.id != needId }
    }

    fun addSuggestionToNeeds(suggestion: SmartSuggestion) {
        addNeed(suggestion.productName, suggestion.category, 1, "unid.")
        _suggestions.value = _suggestions.value.filter { it.id != suggestion.id }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: ProductCategory?) {
        _selectedCategoryFilter.value = category
    }

    // Funciones de Billetera Virtual (OmniPay)
    fun payWithWallet(storeName: String, amount: Double, cashbackPercent: Double = 5.0): Boolean {
        if (amount.isNaN() || amount <= 0.0) return false
        val amountBd = Money.round2(Money.bd(amount))
        val balanceBd = Money.bd(_wallet.value.balance)
        if (balanceBd < amountBd) return false

        val cashbackBd = Money.round2(amountBd.multiply(Money.bd(cashbackPercent)).divide(BigDecimal(100)))
        val newBalance = Money.round2(balanceBd.subtract(amountBd).add(cashbackBd))
        val amountD = amountBd.toDouble()
        val cashbackD = cashbackBd.toDouble()
        val percentLabel = cashbackPercent.toString().removeSuffix(".0")

        val newTransactions = listOf(
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "Pago con QR en $storeName",
                storeOrEntityName = storeName,
                amount = -amountD,
                type = TransactionType.COMPRA_LOCAL,
                dateText = "Recién",
                cashbackEarned = cashbackD
            ),
            WalletTransaction(
                id = UUID.randomUUID().toString(),
                title = "Reintegro Barrial ($percentLabel%)",
                storeOrEntityName = "OmniPay Cashback",
                amount = cashbackD,
                type = TransactionType.REINTEGRO_CASHBACK,
                dateText = "Recién"
            )
        ) + _wallet.value.transactions

        _wallet.value = _wallet.value.copy(
            balance = newBalance.toDouble(),
            totalCashbackEarned = Money.toDouble2(Money.bd(_wallet.value.totalCashbackEarned).add(cashbackBd)),
            transactions = newTransactions
        )

        // Comprobante digital de la operación (demostración: no es un comprobante fiscal real)
        val ticketNum = "T-" + (1000..9999).random() + "-" + (1000000..9999999).random()
        val newReceipt = com.example.model.DigitalReceipt(
            id = UUID.randomUUID().toString(),
            receiptNumber = ticketNum,
            storeName = storeName,
            dateText = "Hoy, " + java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date()) + " hs",
            items = listOf(
                com.example.model.ReceiptItem("Compra en comercio / Pago QR", 1, amountD, amountD)
            ),
            subtotal = amountD,
            discountApplied = cashbackD,
            total = Money.toDouble2(amountBd.subtract(cashbackBd)),
            paymentMethod = "OmniPay QR ($percentLabel% Cashback Acreditado)",
            qrVerificationCode = "OMNI-VALID-" + (10000..99999).random()
        )
        _receipts.value = listOf(newReceipt) + _receipts.value

        return true
    }

    fun addStopToTrip(stopName: String, extraDistanceKm: Double = 1.2) {
        val current = _currentTrip.value
        val newDistance = current.distanceKm + extraDistanceKm
        val newWalking = (newDistance * 14).toInt()
        val newDriving = maxOf(4, (newDistance * 3.2).toInt())
        val newTransit = maxOf(8, (newDistance * 4.5).toInt())
        val newRemisCost = 1200.0 + (newDistance * 480.0)

        val newStep = com.example.model.TripStep(
            instruction = "Parada intermedia: $stopName",
            distanceText = "+${Money.dec1(extraDistanceKm)} km",
            subtext = "Parada agregada a tu recorrido personalizado"
        )

        val updatedSteps = current.steps.toMutableList()
        val insertIndex = if (updatedSteps.size > 1) updatedSteps.size - 1 else updatedSteps.size
        updatedSteps.add(insertIndex, newStep)

        _currentTrip.value = current.copy(
            distanceKm = newDistance,
            walkingMinutes = newWalking,
            drivingMinutes = newDriving,
            transitMinutes = newTransit,
            estimatedRemisCost = newRemisCost,
            steps = updatedSteps,
            landmarksOnRoute = listOf("⭐ Parada: $stopName") + current.landmarksOnRoute
        )
    }

    fun addFunds(amount: Double) {
        if (amount.isNaN() || amount <= 0.0) return
        val newBalance = Money.toDouble2(Money.bd(_wallet.value.balance).add(Money.bd(amount)))
        val newTx = WalletTransaction(
            id = UUID.randomUUID().toString(),
            title = "Carga de Saldo",
            storeOrEntityName = "Transferencia Bancaria",
            amount = amount,
            type = TransactionType.CARGA_SALDO,
            dateText = "Recién"
        )
        _wallet.value = _wallet.value.copy(
            balance = newBalance,
            transactions = listOf(newTx) + _wallet.value.transactions
        )
    }

    // Funciones de Contactos
    fun toggleContactFavorite(contactId: String) {
        _contacts.value = _contacts.value.map {
            if (it.id == contactId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun addCustomContact(name: String, category: ContactCategory, phone: String, address: String) {
        val newContact = NeighborhoodContact(
            id = UUID.randomUUID().toString(),
            name = name,
            category = category,
            phoneNumber = phone,
            statusText = "Agendado por ti",
            rating = 5.0f,
            address = address.ifBlank { null },
            isFavorite = true
        )
        _contacts.value = listOf(newContact) + _contacts.value
    }

    // Funciones de Ajustes de Usuario
    fun updateSearchRadius(meters: Int) {
        _userProfile.value = _userProfile.value.copy(searchRadiusMeters = meters)
    }

    fun updateDietaryPreference(pref: DietaryPreference) {
        _userProfile.value = _userProfile.value.copy(dietaryPreference = pref)
    }

    fun toggleMorningDigest(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(morningDigestEnabled = enabled)
    }

    fun toggleOnRouteAlerts(enabled: Boolean) {
        _userProfile.value = _userProfile.value.copy(onRouteAlertsEnabled = enabled)
    }

    // Funciones de Panel de Comercio (Merchant Hub)
    fun toggleStoreOpenStatus() {
        val current = _managedStore.value
        val updated = current.copy(isOpen = !current.isOpen)
        _managedStore.value = updated
        _stores.value = _stores.value.map { if (it.id == updated.id) updated else it }
    }

    fun addProductToManagedStore(name: String, category: ProductCategory, price: Double, originalPrice: Double?) {
        val current = _managedStore.value
        val newProd = StoreProduct(
            id = "prod_${UUID.randomUUID().toString().take(6)}",
            name = name,
            category = category,
            price = price,
            originalPrice = originalPrice,
            inStock = true
        )
        val updated = current.copy(inventory = listOf(newProd) + current.inventory)
        _managedStore.value = updated
        _stores.value = _stores.value.map { if (it.id == updated.id) updated else it }
    }

    fun addComboToManagedStore(title: String, description: String, discountBadge: String) {
        val current = _managedStore.value
        val newCombo = ComboDeal(title, description, discountBadge)
        val updated = current.copy(comboDeals = listOf(newCombo) + current.comboDeals)
        _managedStore.value = updated
        _stores.value = _stores.value.map { if (it.id == updated.id) updated else it }
    }

    fun updateDiscountThreshold(threshold: Double, percent: Int) {
        val current = _managedStore.value
        val updated = current.copy(
            minSpendDiscountThreshold = threshold,
            minSpendDiscountPercent = percent,
            specialOfferText = "$percent% de reintegro en canasta total superando los $${Money.plain(threshold, 0)}"
        )
        _managedStore.value = updated
        _stores.value = _stores.value.map { if (it.id == updated.id) updated else it }
    }

    fun publishStoreNews(title: String, content: String, badge: String) {
        val current = _managedStore.value
        val newNews = StoreNews(
            id = UUID.randomUUID().toString(),
            storeId = current.id,
            storeName = current.name,
            title = title,
            content = content,
            timeAgoText = "Recién publicado",
            badge = badge.ifBlank { "Novedad Flash 🔥" }
        )
        _storeNews.value = listOf(newNews) + _storeNews.value
    }

    // Funciones de Configuración de Horarios & Guardia
    fun setScheduleFilter(filter: StoreScheduleFilter) {
        _scheduleFilter.value = filter
    }

    fun setSimulatedHour(hour: Int) {
        _simulatedHour.value = hour.coerceIn(0, 23)
    }

    fun toggleSimulatedHoliday() {
        _isSimulatedHoliday.value = !_isSimulatedHoliday.value
    }

    fun updateManagedStoreSchedule(
        openTime: String,
        closeTime: String,
        weekendHoursText: String,
        is24Hours: Boolean,
        isOnDuty: Boolean,
        openOnHolidays: Boolean,
        scheduleNotes: String,
        isOpenNow: Boolean
    ) {
        val current = _managedStore.value
        val hoursDisplay = when {
            isOnDuty -> "DE TURNO 24HS • Guardia Activa Nocturna y Feriados"
            is24Hours -> "Abierto 24 horas continuas • Todos los días"
            else -> "Abierto de $openTime a $closeTime hs"
        }
        val updated = current.copy(
            openTime = openTime,
            closeTime = closeTime,
            weekendHoursText = weekendHoursText,
            is24Hours = is24Hours,
            isOnDuty = isOnDuty,
            openOnHolidays = openOnHolidays,
            scheduleNotes = scheduleNotes,
            isOpen = isOpenNow,
            hoursText = hoursDisplay
        )
        _managedStore.value = updated
        _stores.value = _stores.value.map { if (it.id == updated.id) updated else it }
    }

    companion object {
        /** Referencia cuando no hay permiso/ubicación: Obelisco, Buenos Aires. */
        val DEFAULT_LOCATION = LatLon(-34.6037, -58.3816)
    }
}
