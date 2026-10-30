package com.example.data

import com.example.util.Money
import java.math.BigDecimal
import com.example.model.ComboDeal
import com.example.model.DailyTask
import com.example.model.MatchedItem
import com.example.model.NeedItem
import com.example.model.NeighborhoodContact
import com.example.model.ProductCategory
import com.example.model.SmartSuggestion
import com.example.model.Store
import com.example.model.StoreComparisonResult
import com.example.model.StoreProduct
import com.example.model.StoreType
import com.example.model.TaskCategory

object SampleData {

    val initialTasks = listOf(
        DailyTask(
            id = "t1",
            title = "Planificar compras semanales y canasta familiar",
            category = TaskCategory.COMPRAS,
            isCompleted = false,
            timeTag = "08:30"
        ),
        DailyTask(
            id = "t2",
            title = "Chequear promociones y combos activos en el barrio",
            category = TaskCategory.COMPRAS,
            isCompleted = true,
            timeTag = "09:00"
        ),
        DailyTask(
            id = "t3",
            title = "Caminata activa y retiro de productos en camino",
            category = TaskCategory.RUTINA,
            isCompleted = false,
            timeTag = "11:15"
        ),
        DailyTask(
            id = "t4",
            title = "Revisar despensa y reposición de frescos",
            category = TaskCategory.HOGAR,
            isCompleted = false,
            timeTag = "18:00"
        )
    )

    val initialNeeds = listOf(
        NeedItem(
            id = "n1",
            name = "Leche entera larga vida 1L",
            category = ProductCategory.LACTEOS,
            quantity = 2,
            unit = "litro",
            isChecked = false
        ),
        NeedItem(
            id = "n2",
            name = "Café tostado molido 250g",
            category = ProductCategory.ALMACEN,
            quantity = 1,
            unit = "paq.",
            isChecked = false
        ),
        NeedItem(
            id = "n3",
            name = "Pan de campo / masa madre",
            category = ProductCategory.ALMACEN,
            quantity = 1,
            unit = "kg",
            isChecked = false
        ),
        NeedItem(
            id = "n4",
            name = "Aceite de oliva virgen extra 500ml",
            category = ProductCategory.ALMACEN,
            quantity = 1,
            unit = "botella",
            isChecked = false
        ),
        NeedItem(
            id = "n5",
            name = "Detergente concentrado vajilla 750ml",
            category = ProductCategory.LIMPIEZA,
            quantity = 1,
            unit = "botella",
            isChecked = false
        )
    )

    val sampleStores = listOf(
        Store(
            id = "s1",
            name = "Supermercado San Martín Express",
            type = StoreType.SUPERMERCADO,
            rating = 4.7f,
            reviewsCount = 215,
            distanceMeters = 420,
            address = "Av. San Martín 1420 (e/ Mitre y Belgrano)",
            isOpen = true,
            hoursText = "Abierto hoy de 08:30 a 21:00 hs",
            accentColorHex = 0xFF0D9488,
            photosCount = 14,
            hasVideoTour = true,
            videoTourDuration = "0:42",
            description = "Supermercado integral con pasillos amplios, sector de carnicería premium, lácteos refrigerados y pasillo de ofertas express. Acepta todos los medios de pago y QR.",
            specialOfferText = "15% de reintegro en canasta total superando los $7.000",
            minSpendDiscountThreshold = 7000.0,
            minSpendDiscountPercent = 15,
            comboDeals = listOf(
                ComboDeal("Combo Desayuno Familiar", "2 Leches + 1 Café molido con 20% OFF", "-20%"),
                ComboDeal("Pack Limpieza Total", "Detergente + Lavandina + Suavizante", "-25%"),
                ComboDeal("Miércoles de Frescos", "30% en frutas y verduras seleccionadas", "-30%")
            ),
            inventory = listOf(
                StoreProduct("p1_1", "Leche entera larga vida 1L", ProductCategory.LACTEOS, 1250.0, 1400.0, true),
                StoreProduct("p1_2", "Café tostado molido 250g", ProductCategory.ALMACEN, 3850.0, 4200.0, true),
                StoreProduct("p1_3", "Pan de campo / masa madre", ProductCategory.ALMACEN, 2100.0, null, true),
                StoreProduct("p1_4", "Aceite de oliva virgen extra 500ml", ProductCategory.ALMACEN, 5400.0, 5900.0, true),
                StoreProduct("p1_5", "Detergente concentrado vajilla 750ml", ProductCategory.LIMPIEZA, 2200.0, 2600.0, true),
                StoreProduct("p1_6", "Galletitas dulces surtidas 300g", ProductCategory.KIOSCO, 1150.0, null, true),
                StoreProduct("p1_7", "Yogur bebible frutilla 1L", ProductCategory.LACTEOS, 1800.0, 2000.0, true)
            ),
            mapOffsetX = 0.65f,
            mapOffsetY = 0.28f,
            openTime = "08:30",
            closeTime = "21:00",
            weekendHoursText = "Sáb: 09:00 - 19:00 • Dom: Cerrado",
            is24Hours = false,
            isOnDuty = false,
            openOnHolidays = false,
            scheduleNotes = "Lunes a Sábado horario corrido"
        ),
        Store(
            id = "s2",
            name = "Almacén y Fiambrería Don Pedro",
            type = StoreType.ALMACEN,
            rating = 4.9f,
            reviewsCount = 128,
            distanceMeters = 140,
            address = "Calle Córdoba 312 (a la vuelta)",
            isOpen = true,
            hoursText = "Abierto de 09:00 a 13:30 y 17:00 a 21:30 hs",
            accentColorHex = 0xFFF59E0B,
            photosCount = 9,
            hasVideoTour = true,
            videoTourDuration = "0:30",
            description = "Atendido por sus propios dueños hace más de 18 años. Especialistas en fiambres de primera calidad cortados a la vista, panadería fresca traída al mediodía y productos seleccionados.",
            specialOfferText = "10% de descuento directo en compras mayores a $4.500",
            minSpendDiscountThreshold = 4500.0,
            minSpendDiscountPercent = 10,
            comboDeals = listOf(
                ComboDeal("Combo Picada de Barrio", "Queso gouda + Jamón cocido natural + Pan artesanal", "Oferta Especial"),
                ComboDeal("Dúo Mañanero", "Pan recién horneado + Manteca 200g con 15% OFF", "-15%")
            ),
            inventory = listOf(
                StoreProduct("p2_1", "Leche entera larga vida 1L", ProductCategory.LACTEOS, 1380.0, null, true),
                StoreProduct("p2_2", "Café tostado molido 250g", ProductCategory.ALMACEN, 3950.0, null, true),
                StoreProduct("p2_3", "Pan de campo / masa madre", ProductCategory.ALMACEN, 1850.0, 2100.0, true),
                StoreProduct("p2_4", "Aceite de oliva virgen extra 500ml", ProductCategory.ALMACEN, 5800.0, null, true),
                StoreProduct("p2_5", "Detergente concentrado vajilla 750ml", ProductCategory.LIMPIEZA, 2450.0, null, true)
            ),
            mapOffsetX = 0.32f,
            mapOffsetY = 0.42f,
            openTime = "09:00",
            closeTime = "21:30",
            weekendHoursText = "Sáb: 09:00 - 14:00 • Dom: 10:00 - 13:00",
            is24Hours = false,
            isOnDuty = false,
            openOnHolidays = true,
            scheduleNotes = "Corta al mediodía para siesta (13:30 a 17:00)"
        ),
        Store(
            id = "s3",
            name = "Kiosco & Minimarket La Esquina 24hs",
            type = StoreType.KIOSCO,
            rating = 4.6f,
            reviewsCount = 312,
            distanceMeters = 65,
            address = "Esquina Mitre y Jujuy 802",
            isOpen = true,
            hoursText = "Abierto 24 horas continuas • Todos los días",
            accentColorHex = 0xFFEF4444,
            photosCount = 6,
            hasVideoTour = false,
            videoTourDuration = "",
            description = "Ubicación inmejorable a metros de casa. Bebidas frías, golosinas importadas y nacionales, artículos esenciales de despensa de urgencia y recargas electrónicas.",
            specialOfferText = "2x1 en cafés y bebidas listas para llevar",
            minSpendDiscountThreshold = 3000.0,
            minSpendDiscountPercent = 8,
            comboDeals = listOf(
                ComboDeal("Combo Noche", "Snack salado + Bebida gaseosa 1.5L", "-18%"),
                ComboDeal("Pausa Café", "Café espresso + Alfajor artesanal", "Precio Fijo $1.900")
            ),
            inventory = listOf(
                StoreProduct("p3_1", "Leche entera larga vida 1L", ProductCategory.LACTEOS, 1500.0, null, true),
                StoreProduct("p3_2", "Café tostado molido 250g", ProductCategory.ALMACEN, 4200.0, 4600.0, true),
                StoreProduct("p3_3", "Galletitas dulces surtidas 300g", ProductCategory.KIOSCO, 1200.0, null, true),
                StoreProduct("p3_5", "Detergente concentrado vajilla 750ml", ProductCategory.LIMPIEZA, 2700.0, null, true)
            ),
            mapOffsetX = 0.48f,
            mapOffsetY = 0.58f,
            openTime = "00:00",
            closeTime = "23:59",
            weekendHoursText = "24 Horas continuas sábados, domingos y feriados",
            is24Hours = true,
            isOnDuty = false,
            openOnHolidays = true,
            scheduleNotes = "Turno noche con ventanilla de seguridad blindada"
        ),
        Store(
            id = "s4",
            name = "Mercado Natural & Dietética Vida Verde",
            type = StoreType.TIENDA_NATURAL,
            rating = 4.95f,
            reviewsCount = 89,
            distanceMeters = 680,
            address = "Paseo del Boulevard 450, Local 4",
            isOpen = true,
            hoursText = "Abierto de 09:30 a 20:00 hs",
            accentColorHex = 0xFF10B981,
            photosCount = 12,
            hasVideoTour = true,
            videoTourDuration = "0:50",
            description = "Alimentos agroecológicos, frutos secos por peso, harinas integrales, aceites de prensado en frío, cosmética natural y productos sin TACC certificados.",
            specialOfferText = "12% de descuento pagando con transferencia o efectivo",
            minSpendDiscountThreshold = 6000.0,
            minSpendDiscountPercent = 12,
            comboDeals = listOf(
                ComboDeal("Canasta Saludable", "Mix frutos secos 500g + Miel orgánica", "-15%"),
                ComboDeal("Dúo Oliva & Especias", "Aceite de oliva virgen extra + 2 especias", "-20%")
            ),
            inventory = listOf(
                StoreProduct("p4_1", "Aceite de oliva virgen extra 500ml", ProductCategory.ALMACEN, 5100.0, 5700.0, true),
                StoreProduct("p4_2", "Café tostado molido 250g", ProductCategory.ALMACEN, 4100.0, null, true),
                StoreProduct("p4_3", "Pan de campo / masa madre", ProductCategory.ALMACEN, 2200.0, 2400.0, true),
                StoreProduct("p4_4", "Leche vegetal almendras 1L", ProductCategory.LACTEOS, 2100.0, null, true)
            ),
            mapOffsetX = 0.82f,
            mapOffsetY = 0.75f,
            openTime = "09:30",
            closeTime = "20:00",
            weekendHoursText = "Sáb: 10:00 - 15:00 • Dom: Cerrado",
            is24Hours = false,
            isOnDuty = false,
            openOnHolidays = false,
            scheduleNotes = "Cerrado feriados nacionales"
        ),
        Store(
            id = "s5",
            name = "Panadería y Café La Espiga de Oro",
            type = StoreType.PANADERIA,
            rating = 4.85f,
            reviewsCount = 270,
            distanceMeters = 290,
            address = "Av. Rivadavia 780",
            isOpen = true,
            hoursText = "Abierto desde las 07:00 hasta 20:30 hs",
            accentColorHex = 0xFFD97706,
            photosCount = 11,
            hasVideoTour = true,
            videoTourDuration = "0:35",
            description = "Maestros panaderos desde 1964. Harinas seleccionadas, fermentación lenta de 48 horas y medialunas de manteca premiadas por el barrio.",
            specialOfferText = "Docena de medialunas con 20% de descuento después de las 18 hs",
            minSpendDiscountThreshold = 4000.0,
            minSpendDiscountPercent = 10,
            comboDeals = listOf(
                ComboDeal("Combo Desayuno Express", "Café latte + 2 medialunas calientes", "$2.100"),
                ComboDeal("Canasta de Panes", "Baguette rústica + Pan multicereal + Focaccia", "-15%")
            ),
            inventory = listOf(
                StoreProduct("p5_1", "Pan de campo / masa madre", ProductCategory.ALMACEN, 1750.0, 1950.0, true),
                StoreProduct("p5_2", "Café tostado molido 250g", ProductCategory.ALMACEN, 4300.0, null, true),
                StoreProduct("p5_3", "Leche entera larga vida 1L", ProductCategory.LACTEOS, 1450.0, null, true)
            ),
            mapOffsetX = 0.22f,
            mapOffsetY = 0.78f,
            openTime = "07:00",
            closeTime = "20:30",
            weekendHoursText = "Sáb y Dom: 07:30 - 13:30 y 16:30 - 20:30",
            is24Hours = false,
            isOnDuty = false,
            openOnHolidays = true,
            scheduleNotes = "Horneado fresco a las 07:00, 12:00 y 17:30 hs"
        ),
        Store(
            id = "s6",
            name = "Farmacia & Salud Del Carmen (De Turno 24hs)",
            type = StoreType.FARMACIA,
            rating = 4.92f,
            reviewsCount = 418,
            distanceMeters = 350,
            address = "Av. San Martín 980 (esq. Salta)",
            isOpen = true,
            hoursText = "DE TURNO 24HS • Guardia Activa Nocturna y Feriados",
            accentColorHex = 0xFF0D9488,
            photosCount = 8,
            hasVideoTour = true,
            videoTourDuration = "0:25",
            description = "Farmacia oficial de turno barrial con guardia médica 24 horas continuas. Medicamentos bajo receta, botiquín de primeros auxilios, perfumería y atención por ventanilla nocturna con timbre de emergencia.",
            specialOfferText = "20% en botiquín de primeros auxilios y repelentes",
            minSpendDiscountThreshold = 5000.0,
            minSpendDiscountPercent = 10,
            comboDeals = listOf(
                ComboDeal("Botiquín Esencial", "Alcohol en gel + Gasas + Curitas + Termómetro digital", "-25%"),
                ComboDeal("Dúo Alivio Antigripal", "Ibuprofeno 400mg + Vitamina C efervescente", "Precio Especial")
            ),
            inventory = listOf(
                StoreProduct("p6_1", "Ibuprofeno 400mg x 10 comp.", ProductCategory.FARMACIA, 1950.0, 2400.0, true),
                StoreProduct("p6_2", "Alcohol en gel sanitizante 250ml", ProductCategory.FARMACIA, 1400.0, null, true),
                StoreProduct("p6_3", "Pack apósitos protectores curitas x 20", ProductCategory.FARMACIA, 980.0, null, true),
                StoreProduct("p6_4", "Repelente de mosquitos spray 165ml", ProductCategory.FARMACIA, 3900.0, 4500.0, true),
                StoreProduct("p6_5", "Pañales descartables talle M x 36", ProductCategory.FARMACIA, 8600.0, 9500.0, true)
            ),
            mapOffsetX = 0.52f,
            mapOffsetY = 0.22f,
            openTime = "00:00",
            closeTime = "23:59",
            weekendHoursText = "Guardia 24hs sábados, domingos y feriados nacionales",
            is24Hours = true,
            isOnDuty = true,
            openOnHolidays = true,
            scheduleNotes = "Guardia activa de turno. Después de las 21:00 hs toque el timbre de ventanilla."
        )
    )

    /**
     * Compara comercios para la lista de compras.
     * - Emparejado por palabras clave normalizadas (ver [ProductMatcher]).
     * - Dinero calculado con BigDecimal y redondeado a centavos.
     * - El descuento por monto mínimo solo aplica si hay productos disponibles y el porcentaje es > 0.
     */
    fun calculateComparison(stores: List<Store>, needs: List<NeedItem>): List<StoreComparisonResult> {
        return stores.map { store ->
            val matchedItems = needs.map { need ->
                val match = ProductMatcher.bestMatch(need, store.inventory)
                MatchedItem(
                    need = need,
                    matchedProduct = match,
                    isAvailable = match != null && match.inStock
                )
            }

            val available = matchedItems.filter { it.isAvailable && it.matchedProduct != null }
            var subtotal = BigDecimal.ZERO
            var regularTotal = BigDecimal.ZERO
            for (item in available) {
                val product = item.matchedProduct!!
                val qty = BigDecimal(item.need.quantity)
                subtotal = subtotal.add(Money.bd(product.price).multiply(qty))
                regularTotal = regularTotal.add(Money.bd(product.originalPrice ?: product.price).multiply(qty))
            }
            subtotal = Money.round2(subtotal)
            regularTotal = Money.round2(regularTotal)

            var discount = BigDecimal.ZERO
            if (available.isNotEmpty() &&
                store.minSpendDiscountPercent > 0 &&
                subtotal >= Money.bd(store.minSpendDiscountThreshold)
            ) {
                discount = Money.round2(
                    subtotal.multiply(BigDecimal(store.minSpendDiscountPercent)).divide(BigDecimal(100))
                )
            }

            val finalTotal = subtotal.subtract(discount).max(BigDecimal.ZERO)
            val savings = regularTotal.subtract(subtotal).add(discount).max(BigDecimal.ZERO)

            var badge: String? = null
            when {
                store.distanceMeters <= 150 -> badge = "⚡ Más Cercano (${store.walkingTimeMin} min a pie)"
                store.rating >= 4.9f -> badge = "⭐ Local Destacado de la Comunidad"
                discount > BigDecimal.ZERO -> badge = "🏆 Ahorro Extra (${store.minSpendDiscountPercent}% OFF aplicado)"
            }

            StoreComparisonResult(
                store = store,
                matchedItems = matchedItems,
                availableCount = available.size,
                totalRequestedCount = needs.size,
                rawSubtotal = subtotal.toDouble(),
                thresholdDiscountApplied = discount.toDouble(),
                finalTotal = finalTotal.toDouble(),
                estimatedSavings = savings.toDouble(),
                highlightBadge = badge
            )
        }.sortedWith(
            compareByDescending<StoreComparisonResult> { it.availableCount }
                .thenBy { it.finalTotal }
        )
    }


    val sampleContacts = listOf(
        NeighborhoodContact(
            id = "c1",
            name = "Remisería Belgrano Centro",
            category = com.example.model.ContactCategory.REMIS,
            phoneNumber = "+54 11 4782-9900",
            statusText = "Disponible ahora • 4 móviles en zona",
            rating = 4.8f,
            address = "Av. Cabildo 2240",
            isFavorite = true,
            averageWaitMinutes = 5
        ),
        NeighborhoodContact(
            id = "c2",
            name = "Radio Taxi Express Barrial",
            category = com.example.model.ContactCategory.TAXI,
            phoneNumber = "+54 11 4555-1234",
            statusText = "Móvil a 3 cuadras listo",
            rating = 4.7f,
            address = "Base Belgrano Norte",
            isFavorite = false,
            averageWaitMinutes = 3
        ),
        NeighborhoodContact(
            id = "c3",
            name = "Farmacia San Juan (De Turno 24hs)",
            category = com.example.model.ContactCategory.FARMACIA_TURNO,
            phoneNumber = "+54 11 4701-8844",
            statusText = "De Turno 24hs hoy • Guardias activas",
            rating = 4.9f,
            address = "Calle Juramento 1840",
            isFavorite = true
        ),
        NeighborhoodContact(
            id = "c4",
            name = "Cerrajería Urgencias 24hs Don Mario",
            category = com.example.model.ContactCategory.CERRAJERIA,
            phoneNumber = "+54 11 3820-1122",
            statusText = "Guardia activa • Llega en 15 minutos",
            rating = 4.95f,
            address = "Móvil a domicilio",
            isFavorite = false,
            averageWaitMinutes = 15
        ),
        NeighborhoodContact(
            id = "c5",
            name = "Fletes & Repartos San Martín",
            category = com.example.model.ContactCategory.FLETE,
            phoneNumber = "+54 11 6940-5566",
            statusText = "Camioneta disponible para compras grandes",
            rating = 4.75f,
            address = "Av. San Martín 1500",
            isFavorite = false
        ),
        NeighborhoodContact(
            id = "c6",
            name = "SAME / Emergencias Médicas 107",
            category = com.example.model.ContactCategory.EMERGENCIA,
            phoneNumber = "107",
            statusText = "Servicio público gratuito de emergencias",
            rating = 5.0f,
            isFavorite = true
        )
    )

    val sampleSuggestions = listOf(
        SmartSuggestion(
            id = "sug1",
            productName = "Manteca artesanal primera marca 200g",
            category = ProductCategory.LACTEOS,
            reasonText = "Combina con el pan y café de tu lista",
            discountBadge = "-20% con Combo Desayuno",
            storeName = "Almacén Don Pedro",
            price = 1450.0,
            originalPrice = 1800.0,
            isOnRoute = false
        ),
        SmartSuggestion(
            id = "sug2",
            productName = "Mermelada orgánica frutos del bosque",
            category = ProductCategory.ALMACEN,
            reasonText = "Ideal para acompañar tu pan de masa madre",
            discountBadge = "-15% especial",
            storeName = "Dietética Vida Verde",
            price = 2100.0,
            originalPrice = 2480.0,
            isOnRoute = false
        ),
        SmartSuggestion(
            id = "sug3",
            productName = "Esponjas antibacteriales (Pack x3)",
            category = ProductCategory.LIMPIEZA,
            reasonText = "Complemento para tu detergente concentrado",
            discountBadge = "2x1",
            storeName = "Supermercado San Martín Express",
            price = 950.0,
            originalPrice = 1350.0,
            isOnRoute = false
        ),
        SmartSuggestion(
            id = "sug4",
            productName = "Medialunas de manteca calientes (Docena)",
            category = ProductCategory.ALMACEN,
            reasonText = "A solo 25m de tu camino habitual",
            discountBadge = "Promo Mañana",
            storeName = "Panadería La Espiga de Oro",
            price = 3200.0,
            originalPrice = 4000.0,
            isOnRoute = true,
            detourMeters = 25
        ),
        SmartSuggestion(
            id = "sug5",
            productName = "Agua mineral con gas 2L",
            category = ProductCategory.BEBIDAS,
            reasonText = "Al paso sin desviarte de tu recorrido",
            discountBadge = "-25% Flash",
            storeName = "Kiosco La Esquina 24hs",
            price = 850.0,
            originalPrice = 1150.0,
            isOnRoute = true,
            detourMeters = 10
        )
    )

    val sampleNews = listOf(
        com.example.model.StoreNews(
            id = "news_1",
            storeId = "s5",
            storeName = "Panadería La Espiga de Oro",
            title = "¡Acaban de salir medialunas calientes de manteca!",
            content = "Horneada fresca recién salida del horno. 20% de descuento llevando docena en la próxima hora.",
            timeAgoText = "Hace 15 min",
            badge = "Recién Horneado 🔥"
        ),
        com.example.model.StoreNews(
            id = "news_2",
            storeId = "s1",
            storeName = "Supermercado San Martín",
            title = "Llegó Palta Hass premium a precio de cajón",
            content = "Stock limitado de palta directa del norte. $1.990 el kilo por compras superiores a $5.000.",
            timeAgoText = "Hace 1 hora",
            badge = "Oferta Flash 🥑"
        ),
        com.example.model.StoreNews(
            id = "news_3",
            storeId = "s2",
            storeName = "Almacén Don Pedro",
            title = "Quesos de campo y salamines tandileros ingresados hoy",
            content = "Cortamos fiambrería a la vista como siempre. Pide tu degustación gratuita al pasar.",
            timeAgoText = "Hace 3 horas",
            badge = "Novedad 🧀"
        )
    )

    fun calculateTrip(origin: String, destination: String): com.example.model.TripRoute {
        val destLower = destination.lowercase()
        val isObelisco = destLower.contains("obelisco") || destLower.contains("corrientes") || destLower.contains("centro")
        val isPalermo = destLower.contains("palermo") || destLower.contains("soho")

        val distance = if (isObelisco) 6.4 else if (isPalermo) 2.8 else 4.2
        val walking = (distance * 14).toInt()
        val driving = maxOf(4, (distance * 3.2).toInt())
        val transit = maxOf(8, (distance * 4.5).toInt())
        val remisCost = 1200.0 + (distance * 480.0)

        val steps = if (isObelisco) {
            listOf(
                com.example.model.TripStep("Salida desde $origin", "0 m", "Dirígete hacia Av. San Martín / Av. Corrientes"),
                com.example.model.TripStep("Avanza directo por Av. Corrientes", "4.8 km", "Pasarás por Parque Centenario y Abasto"),
                com.example.model.TripStep("Continúa hasta Av. 9 de Julio", "1.2 km", "Llegas a la Plaza de la República"),
                com.example.model.TripStep("Destino final: El Obelisco", "400 m", "Punto histórico en el centro de la ciudad")
            )
        } else {
            listOf(
                com.example.model.TripStep("Salida desde $origin", "0 m", "Comienza el recorrido en dirección a $destination"),
                com.example.model.TripStep("Tomar avenida principal", "${Money.dec1(distance * 0.7)} km", "Tránsito fluido en este horario"),
                com.example.model.TripStep("Llegada a $destination", "${Money.dec1(distance * 0.3)} km", "Estacionamiento y parada de remis habilitada")
            )
        }

        val landmarks = if (isObelisco) {
            listOf("Kiosco La Esquina 24hs (a 2 cuadras)", "Estación de Subte Línea B", "Teatro San Martín", "Pizzería Güerrin")
        } else {
            listOf("Farmacia San Juan de Turno", "Cafetería La Espiga", "Parada de Colectivo 168")
        }

        return com.example.model.TripRoute(
            originName = origin.ifBlank { "Supermercado San Martín" },
            destinationName = destination.ifBlank { "El Obelisco (Centro)" },
            distanceKm = distance,
            walkingMinutes = walking,
            drivingMinutes = driving,
            transitMinutes = transit,
            estimatedRemisCost = remisCost,
            steps = steps,
            landmarksOnRoute = landmarks
        )
    }

    val sampleWeatherAlerts = listOf(
        com.example.model.WeatherAlert(
            id = "alert_1",
            type = com.example.model.AlertType.TORMENTA,
            severity = com.example.model.AlertSeverity.AMARILLO,
            title = "Alerta Amarilla por Tormentas Fuertes",
            description = "Se prevén tormentas aisladas con ocasional caída de granizo y ráfagas intensas en tu zona geográfica.",
            instructions = "Evita dejar objetos sueltos en balcones y planifica compras techadas.",
            validUntil = "Hoy hasta 19:00 hs"
        ),
        com.example.model.WeatherAlert(
            id = "alert_2",
            type = com.example.model.AlertType.CLIMA_CALOR,
            severity = com.example.model.AlertSeverity.INFO,
            title = "Aviso Preventivo de Radiación UV & Calor",
            description = "Índice UV alto entre las 11:30 y 16:30 hs. Temperatura máxima estimada de 30°C.",
            instructions = "Lleva agua mineral contigo al salir y prefiere recorridos sombreados.",
            validUntil = "Hoy hasta 17:00 hs"
        )
    )

    val sampleMorningBriefing = com.example.model.MorningBriefing(
        greeting = "¡Buen día, Santiago! Esperamos que tengas una excelente jornada.",
        dateText = "Jueves, 29 de Octubre • Comienzo de día",
        weatherSummary = "Cielo despejado a algo nublado con brisa agradable",
        temperatureRange = "Min 17°C • Máx 28°C",
        motivationalQuote = "El orden en tu mañana define la paz de tu día.",
        activeDealsCount = 5,
        pendingTasksCount = 3
    )

    val sampleReceipts = listOf(
        com.example.model.DigitalReceipt(
            id = "rec_1",
            receiptNumber = "T-0048-0001928",
            storeName = "Supermercado San Martín Express",
            dateText = "28 Oct 2026, 18:42 hs",
            items = listOf(
                com.example.model.ReceiptItem("Leche entera larga vida 1L", 2, 1250.0, 2500.0),
                com.example.model.ReceiptItem("Café tostado molido 250g", 1, 3850.0, 3850.0),
                com.example.model.ReceiptItem("Detergente concentrado 750ml", 1, 2200.0, 2200.0)
            ),
            subtotal = 8550.0,
            discountApplied = 1282.5,
            total = 7267.5,
            paymentMethod = "OmniPay QR (5% Cashback Acreditado)",
            qrVerificationCode = "OMNI-VALID-98214"
        ),
        com.example.model.DigitalReceipt(
            id = "rec_2",
            receiptNumber = "T-0012-0000412",
            storeName = "Panadería La Espiga de Oro",
            dateText = "27 Oct 2026, 09:15 hs",
            items = listOf(
                com.example.model.ReceiptItem("Pan de masa madre 1kg", 1, 1750.0, 1750.0),
                com.example.model.ReceiptItem("Medialunas de manteca (Docena)", 1, 3200.0, 3200.0)
            ),
            subtotal = 4950.0,
            discountApplied = 500.0,
            total = 4450.0,
            paymentMethod = "Tarjeta Visa Débito Galicia",
            qrVerificationCode = "OMNI-VALID-41209"
        )
    )

    val sampleTouristAttractions = listOf(
        com.example.model.TouristAttraction(
            id = "att_1",
            name = "El Obelisco de Buenos Aires",
            category = "Monumento Histórico",
            distanceKm = 6.4,
            estimatedVisitTime = "45 min",
            description = "Monumento histórico erigido en 1936 en el cruce de las avenidas Corrientes y 9 de Julio.",
            rating = 4.8f,
            coordinatesX = 0.8f,
            coordinatesY = 0.25f
        ),
        com.example.model.TouristAttraction(
            id = "att_2",
            name = "Jardín Botánico & Rosedal",
            category = "Paseo & Naturaleza",
            distanceKm = 2.4,
            estimatedVisitTime = "1 h 30 min",
            description = "Espacio verde emblemático con senderos arbolados, lagos y fuentes artísticas.",
            rating = 4.9f,
            coordinatesX = 0.5f,
            coordinatesY = 0.45f
        ),
        com.example.model.TouristAttraction(
            id = "att_3",
            name = "Museo de Bellas Artes",
            category = "Arte & Cultura",
            distanceKm = 3.9,
            estimatedVisitTime = "2 h",
            description = "Una de las colecciones de arte público más destacadas de América Latina.",
            rating = 4.95f,
            coordinatesX = 0.65f,
            coordinatesY = 0.35f
        )
    )
}
