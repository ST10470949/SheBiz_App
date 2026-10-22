package com.example

import com.example.data.SheBizRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SheBizRepositoryTest {

    private lateinit var repository: SheBizRepository

    @Before
    fun setUp() {
        repository = SheBizRepository()
    }

    @Test
    fun testDynamicMatchPercentageCalculation() {
        val opps = repository.opportunities.value
        assertFalse(opps.isEmpty())

        val firstOpp = opps.first()
        val matchPct = repository.calculateMatchPercentage(firstOpp)
        assertTrue("Match percentage should be between 50 and 100", matchPct in 50..100)
    }

    @Test
    fun testAtomicSaleRecordAndStockReduction() {
        val products = repository.products.value
        val chocolateCake = products.first { it.id == "prod_1" }
        val initialStock = chocolateCake.stock

        val result = repository.recordSale("prod_1", 2, "Test Customer")
        assertTrue(result.isSuccess)

        val updatedProducts = repository.products.value
        val updatedCake = updatedProducts.first { it.id == "prod_1" }
        assertEquals(initialStock - 2, updatedCake.stock)

        val sales = repository.sales.value
        val latestSale = sales.first()
        assertEquals("prod_1", latestSale.productId)
        assertEquals(2, latestSale.quantity)
        assertEquals(700.0, latestSale.total, 0.01)
    }

    @Test
    fun testSaleFailsWhenStockInsufficient() {
        val products = repository.products.value
        val vanillaCake = products.first { it.id == "prod_3" } // stock is 0
        assertEquals(0, vanillaCake.stock)

        val result = repository.recordSale("prod_3", 1, "Customer")
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Insufficient stock") == true)
    }

    @Test
    fun testProfitCalculation() {
        val sales = repository.getMonthlySales()
        val expenses = repository.getMonthlyExpenses()
        val profit = repository.getEstProfit()

        assertEquals(sales - expenses, profit, 0.01)
    }
}
