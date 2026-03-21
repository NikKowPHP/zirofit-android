package com.ziro.fit.util

data class Plate(
    val weight: Double,
    val count: Int
)

object BarbellLogic {

    val availablePlatesKg = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)
    val availablePlatesLbs = listOf(45.0, 35.0, 25.0, 10.0, 5.0, 2.5)

    val defaultBarbellWeightKg = 20.0
    val defaultBarbellWeightLbs = 45.0

    fun calculatePlates(
        targetWeight: Double,
        barWeight: Double = defaultBarbellWeightKg,
        useKg: Boolean = true
    ): List<Plate> {
        val plates = if (useKg) availablePlatesKg else availablePlatesLbs
        
        if (targetWeight <= barWeight) {
            return emptyList()
        }

        val weightPerSide = (targetWeight - barWeight) / 2
        val plateList = mutableListOf<Plate>()
        var remaining = weightPerSide

        for (plateWeight in plates) {
            if (remaining >= plateWeight) {
                val count = (remaining / plateWeight).toInt()
                if (count > 0) {
                    plateList.add(Plate(plateWeight, count))
                    remaining -= count * plateWeight
                }
            }
        }

        return plateList
    }

    fun calculateClosestValidWeight(
        targetWeight: Double,
        barWeight: Double = defaultBarbellWeightKg,
        useKg: Boolean = true
    ): Double {
        val plates = calculatePlates(targetWeight, barWeight, useKg)
        val actualWeight = barWeight + plates.sumOf { it.weight * it.count * 2 }
        return actualWeight
    }

    fun getBarbellWeightOptions(useKg: Boolean = true): List<Double> {
        return if (useKg) {
            listOf(15.0, 20.0, 25.0)
        } else {
            listOf(35.0, 45.0, 55.0)
        }
    }

    fun formatWeight(weight: Double, useKg: Boolean = true): String {
        val suffix = if (useKg) "kg" else "lbs"
        return "%.1f %s".format(weight, suffix)
    }
}

object PlateCalculator {

    fun calculatePlatesPerSide(
        targetWeight: Double,
        barWeight: Double = BarbellLogic.defaultBarbellWeightKg,
        availablePlates: List<Double> = BarbellLogic.availablePlatesKg
    ): List<Double> {
        if (targetWeight <= barWeight) {
            return emptyList()
        }

        val weightPerSide = (targetWeight - barWeight) / 2
        val plates = mutableListOf<Double>()
        var remaining = weightPerSide

        for (plateWeight in availablePlates) {
            while (remaining >= plateWeight) {
                plates.add(plateWeight)
                remaining -= plateWeight
            }
        }

        return plates
    }

    fun getTotalWeight(platesPerSide: List<Double>, barWeight: Double): Double {
        val platesTotal = platesPerSide.sum() * 2
        return barWeight + platesTotal
    }

    fun platesToString(platesPerSide: List<Double>): String {
        if (platesPerSide.isEmpty()) return "Bar only"
        
        val grouped = platesPerSide.groupBy { it }
        return grouped.entries
            .sortedByDescending { it.key }
            .joinToString(" + ") { (weight, plates) ->
                if (plates.size > 1) "${plates.size}x$weight" else "$weight"
            }
    }
}
