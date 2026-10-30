package com.example.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Utilidades de dinero y formato.
 * - Los cálculos se hacen con BigDecimal (sin errores de punto flotante) y se redondean a 2 decimales.
 * - El formato usa siempre es-AR (1.234,50), sin depender del idioma del teléfono.
 */
object Money {
    private val AR = Locale("es", "AR")

    /** BigDecimal.valueOf usa Double.toString, así 0.1 es exactamente 0.1. */
    fun bd(value: Double): BigDecimal = BigDecimal.valueOf(value)

    fun round2(value: BigDecimal): BigDecimal = value.setScale(2, RoundingMode.HALF_UP)

    fun toDouble2(value: BigDecimal): Double = round2(value).toDouble()

    /** [percent]% de [amount], redondeado a centavos. Ej: percentOf(4850.0, 5.0) = 242.5 */
    fun percentOf(amount: Double, percent: Double): Double =
        toDouble2(bd(amount).multiply(bd(percent)).divide(BigDecimal(100)))

    /** Número con separadores es-AR, sin símbolo. Ej: plain(18450.0, 2) = "18.450,00" */
    fun plain(value: Double, decimals: Int = 0): String {
        val pattern = if (decimals <= 0) "#,##0" else "#,##0." + "0".repeat(decimals)
        val df = DecimalFormat(pattern, DecimalFormatSymbols(AR))
        df.roundingMode = RoundingMode.HALF_UP
        return df.format(value)
    }

    /** Un decimal con coma. Ej: dec1(6.4) = "6,4" */
    fun dec1(value: Double): String = plain(value, 1)

    fun dec1(value: Float): String = dec1(value.toDouble())
}
