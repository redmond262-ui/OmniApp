package com.example.model

data class UserAddress(
    val id: String,
    val title: String,
    val address: String,
    val isPrimary: Boolean = false
)

enum class DietaryPreference(val label: String) {
    TODO("Sin restricciones"),
    SIN_TACC("Sin TACC / Celíaco"),
    VEGANO("Vegano"),
    VEGETARIANO("Vegetariano"),
    ORGANICO("100% Orgánico")
}

data class UserProfile(
    val id: String = "user_1",
    val fullName: String = "Santiago Rossi",
    val email: String = "santiago.rossi@email.com",
    val phone: String = "+54 11 5521-9988",
    val neighborLevel: String = "Vecino Oro",
    val monthlySavingsTotal: Double = 14250.0,
    val searchRadiusMeters: Int = 1500,
    val dietaryPreference: DietaryPreference = DietaryPreference.SIN_TACC,
    val morningDigestEnabled: Boolean = true,
    val morningDigestTime: String = "08:00",
    val onRouteAlertsEnabled: Boolean = true,
    val maxDetourAlertMeters: Int = 50,
    val addresses: List<UserAddress> = listOf(
        UserAddress("addr_1", "Casa", "Calle Belgrano 1240, Dpto 4B", true),
        UserAddress("addr_2", "Trabajo", "Av. Santa Fe 3420", false)
    )
)
