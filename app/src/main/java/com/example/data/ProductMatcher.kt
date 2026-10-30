package com.example.data

import com.example.model.NeedItem
import com.example.model.StoreProduct
import java.text.Normalizer
import java.util.Locale

/**
 * Empareja un producto de la lista de compras con el inventario de un comercio.
 * Antes se comparaba solo la primera palabra ("Leche de almendras" = "Leche entera").
 * Ahora se normalizan acentos/plurales/unidades y se exige que coincida la mayoría de las palabras clave.
 */
object ProductMatcher {
    private val STOPWORDS = setOf(
        "de", "del", "la", "el", "los", "las", "con", "sin", "y", "en", "para", "x", "por", "a", "al", "un", "una"
    )
    private val UNIT_TOKEN = Regex("^\\d+([.,]\\d+)?(g|gr|kg|ml|l|lt|cc|u|un|unid)?$")
    private val ACCENTS = Regex("\\p{Mn}+")
    private val SPLIT = Regex("[^a-z0-9]+")

    private const val MIN_LEXICAL_RATIO = 0.6

    fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD).replace(ACCENTS, "")

    fun tokens(text: String): List<String> =
        normalize(text).split(SPLIT)
            .filter { it.isNotBlank() && it !in STOPWORDS && !UNIT_TOKEN.matches(it) }
            .map { if (it.length > 3 && it.endsWith("s")) it.dropLast(1) else it }

    /** Proporción (0..1) de palabras del pedido que aparecen en el producto. */
    fun lexicalRatio(need: NeedItem, product: StoreProduct): Double {
        val wanted = tokens(need.name)
        val available = tokens(product.name).toSet()
        if (wanted.isEmpty() || available.isEmpty()) return 0.0
        return wanted.count { it in available }.toDouble() / wanted.size
    }

    private data class Candidate(val product: StoreProduct, val score: Double)

    /**
     * Mejor producto del inventario para el pedido, o null si ninguno es razonable.
     * Prioriza: con stock > mejor coincidencia > mismo rubro > menor precio.
     */
    fun bestMatch(need: NeedItem, inventory: List<StoreProduct>): StoreProduct? {
        val candidates = inventory.mapNotNull { product ->
            val ratio = lexicalRatio(need, product)
            if (ratio < MIN_LEXICAL_RATIO) null
            else Candidate(product, ratio + if (product.category == need.category) 0.05 else 0.0)
        }
        if (candidates.isEmpty()) return null
        return candidates
            .sortedWith(
                compareByDescending<Candidate> { it.product.inStock }
                    .thenByDescending { Math.round(it.score * 100) }
                    .thenBy { it.product.price }
            )
            .first().product
    }
}
