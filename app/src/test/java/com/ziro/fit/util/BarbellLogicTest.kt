package com.ziro.fit.util

import org.junit.Assert.*
import org.junit.Test

class BarbellLogicTest {

    // --- BarbellLogic.calculatePlates tests ---

    @Test
    fun `calculatePlates returns empty for weight at or below barbell`() {
        assertTrue(BarbellLogic.calculatePlates(20.0, 20.0).isEmpty())
        assertTrue(BarbellLogic.calculatePlates(15.0, 20.0).isEmpty())
    }

    @Test
    fun `calculatePlates returns single smallest plate for minimal overage`() {
        val plates = BarbellLogic.calculatePlates(22.5, 20.0)
        assertEquals(1, plates.size)
        assertEquals(Plate(1.25, 1), plates[0])
    }

    @Test
    fun `calculatePlates uses largest plates first`() {
        val plates = BarbellLogic.calculatePlates(70.0, 20.0)
        val totalPerSide = plates.sumOf { it.weight * it.count }
        assertEquals(25.0, totalPerSide, 0.001)
    }

    @Test
    fun `calculatePlates correctly distributes weight per side`() {
        val plates = BarbellLogic.calculatePlates(120.0, 20.0)
        val perSide = plates.sumOf { it.weight * it.count }
        assertEquals(50.0, perSide, 0.001)
    }

    @Test
    fun `calculatePlates handles exact plate combinations`() {
        val plates = BarbellLogic.calculatePlates(100.0, 20.0)
        val perSide = plates.sumOf { it.weight * it.count }
        assertEquals(40.0, perSide, 0.001)
    }

    @Test
    fun `calculatePlates handles lbs mode`() {
        val plates = BarbellLogic.calculatePlates(135.0, 45.0, useKg = false)
        val perSide = plates.sumOf { it.weight * it.count }
        assertEquals(45.0, perSide, 0.001)
    }

    @Test
    fun `calculatePlates handles mixed large and small plates`() {
        val plates = BarbellLogic.calculatePlates(147.5, 20.0)
        val perSide = plates.sumOf { it.weight * it.count }
        assertEquals(63.75, perSide, 0.001)
    }

    @Test
    fun `calculatePlates default barbell weight is 20kg in kg mode`() {
        val plates = BarbellLogic.calculatePlates(20.0)
        assertTrue(plates.isEmpty())
    }

    // --- BarbellLogic.calculateClosestValidWeight tests ---

    @Test
    fun `calculateClosestValidWeight returns barbell weight when target is at barbell`() {
        val result = BarbellLogic.calculateClosestValidWeight(20.0)
        assertEquals(20.0, result, 0.001)
    }

    @Test
    fun `calculateClosestValidWeight rounds down to exact plates`() {
        val result = BarbellLogic.calculateClosestValidWeight(100.0)
        assertEquals(100.0, result, 0.001)
    }

    @Test
    fun `calculateClosestValidWeight rounds down when exact plates unavailable`() {
        val result = BarbellLogic.calculateClosestValidWeight(99.0)
        val plates = BarbellLogic.calculatePlates(99.0)
        val expected = 20.0 + plates.sumOf { it.weight * it.count * 2 }
        assertEquals(expected, result, 0.001)
    }

    @Test
    fun `calculateClosestValidWeight uses barbell param`() {
        val result = BarbellLogic.calculateClosestValidWeight(100.0, barWeight = 15.0)
        assertEquals(100.0, result, 0.001)
    }

    // --- BarbellLogic.getBarbellWeightOptions tests ---

    @Test
    fun `getBarbellWeightOptions returns kg options`() {
        val options = BarbellLogic.getBarbellWeightOptions(useKg = true)
        assertEquals(listOf(15.0, 20.0, 25.0), options)
    }

    @Test
    fun `getBarbellWeightOptions returns lbs options`() {
        val options = BarbellLogic.getBarbellWeightOptions(useKg = false)
        assertEquals(listOf(35.0, 45.0, 55.0), options)
    }

    // --- BarbellLogic.formatWeight tests ---

    @Test
    fun `formatWeight formats with kg suffix`() {
        assertEquals("100.0 kg", BarbellLogic.formatWeight(100.0, true))
    }

    @Test
    fun `formatWeight formats with lbs suffix`() {
        assertEquals("225.0 lbs", BarbellLogic.formatWeight(225.0, false))
    }

    @Test
    fun `formatWeight formats decimals correctly`() {
        assertEquals("97.5 kg", BarbellLogic.formatWeight(97.5, true))
    }

    // --- PlateCalculator tests ---

    @Test
    fun `calculatePlatesPerSide returns empty when at barbell weight`() {
        assertTrue(PlateCalculator.calculatePlatesPerSide(20.0).isEmpty())
    }

    @Test
    fun `calculatePlatesPerSide returns plates per side`() {
        val plates = PlateCalculator.calculatePlatesPerSide(120.0, 20.0)
        assertEquals(50.0, plates.sum(), 0.001)
        assertEquals(listOf(25.0, 25.0), plates)
    }

    @Test
    fun `calculatePlatesPerSide uses available plates list`() {
        val plates = PlateCalculator.calculatePlatesPerSide(
            targetWeight = 100.0,
            barWeight = 20.0,
            availablePlates = listOf(25.0, 10.0, 5.0)
        )
        assertEquals(40.0, plates.sum(), 0.001)
    }

    @Test
    fun `getTotalWeight calculates correctly`() {
        val total = PlateCalculator.getTotalWeight(listOf(25.0, 20.0, 5.0), 20.0)
        assertEquals(120.0, total, 0.001)
    }

    @Test
    fun `getTotalWeight with no plates returns barbell only`() {
        val total = PlateCalculator.getTotalWeight(emptyList(), 20.0)
        assertEquals(20.0, total, 0.001)
    }

    @Test
    fun `platesToString formats single plate`() {
        val result = PlateCalculator.platesToString(listOf(25.0))
        assertEquals("25.0", result)
    }

    @Test
    fun `platesToString formats multiple of same plate`() {
        val result = PlateCalculator.platesToString(listOf(25.0, 25.0, 20.0))
        assertEquals("2x25.0 + 20.0", result)
    }

    @Test
    fun `platesToString formats sorted descending`() {
        val result = PlateCalculator.platesToString(listOf(20.0, 25.0, 10.0, 25.0))
        assertEquals("2x25.0 + 20.0 + 10.0", result)
    }

    @Test
    fun `platesToString returns bar only for empty list`() {
        assertEquals("Bar only", PlateCalculator.platesToString(emptyList()))
    }
}
