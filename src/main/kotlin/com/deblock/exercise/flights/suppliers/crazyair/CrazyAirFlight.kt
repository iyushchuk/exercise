package com.deblock.exercise.flights.suppliers.crazyair

import com.deblock.exercise.flights.Flight
import java.math.BigDecimal
import java.time.LocalDateTime

data class CrazyAirFlight(
    val airline: String,
    val price: BigDecimal,
    val cabinClass: CabinCLass,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    val departureDate: LocalDateTime,
    val arrivalDate: LocalDateTime,
) {
    fun toFlight(): Flight {

        return Flight(
            airline = airline,
            supplier = SUPPLIER,
            fare = price,
            departureAirportCode = departureAirportCode,
            destinationAirportCode = destinationAirportCode,
            departureDate = departureDate,
            arrivalDate = arrivalDate

        )
    }

    enum class CabinCLass {
        E, B
    }

    companion object {
        const val SUPPLIER = "CrazyAir"
    }
}