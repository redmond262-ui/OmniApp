package com.example

import com.example.data.ProductMatcher
import com.example.data.SampleData
import com.example.model.NeedItem
import com.example.model.ProductCategory
import com.example.model.StoreProduct
import com.example.util.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ComparisonLogicTest {

    private val milk = StoreProduct("p1", "Leche entera larga vida 1L", ProductCategory.LACTEOS, 1250.0)
    private val oliveOil = StoreProduct("p2", "Aceite de oliva virgen extra 500ml", ProductCategory.ALMACEN, 5400.0)

    @Test
    fun almondMilkDoesNotMatchWholeMilk() {
        val need = NeedItem("n1", "Leche de almendras", ProductCategory.LACTEOS)
        assertNull(ProductMatcher.bestMatch(need, listOf(milk)))
    }

    @Test
    fun plainMilkMatchesWholeMilk() {
        val need = NeedItem("n2", "Leche", ProductCategory.LACTEOS)
        assertNotNull(ProductMatcher.bestMatch(need, listOf(milk)))
    }

    @Test
    fun sunflowerOilDoesNotMatchOliveOil() {
        val need = NeedItem("n3", "Aceite de girasol", ProductCategory.ALMACEN)
        assertNull(ProductMatcher.bestMatch(need, listOf(oliveOil)))
    }

    @Test
    fun accentsAndPluralsAreNormalized() {
        val need = NeedItem("n4", "Café molidos", ProductCategory.ALMACEN)
        val coffee = StoreProduct("p3", "Cafe tostado molido 250g", ProductCategory.ALMACEN, 3850.0)
        assertNotNull(ProductMatcher.bestMatch(need, listOf(coffee)))
    }

    @Test
    fun moneyHasNoFloatingPointNoise() {
        assertEquals(242.5, Money.percentOf(4850.0, 5.0), 0.0)
        assertEquals(0.3, Money.toDouble2(Money.bd(0.1).add(Money.bd(0.2))), 0.0)
    }

    @Test
    fun moneyFormatsAsArgentine() {
        assertEquals("18.450,00", Money.plain(18450.0, 2))
        assertEquals("7.000", Money.plain(7000.0, 0))
        assertEquals("6,4", Money.dec1(6.4))
    }

    @Test
    fun comparisonTotalsAreConsistent() {
        val results = SampleData.calculateComparison(SampleData.sampleStores, SampleData.initialNeeds)
        results.forEach {
            assertEquals(
                Money.toDouble2(Money.bd(it.rawSubtotal).subtract(Money.bd(it.thresholdDiscountApplied))),
                it.finalTotal,
                0.001
            )
        }
    }
}
