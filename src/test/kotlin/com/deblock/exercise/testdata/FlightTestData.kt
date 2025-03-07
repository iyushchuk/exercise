package com.deblock.exercise.testdata

import com.deblock.exercise.flights.Flight
import java.math.BigDecimal
import java.time.LocalDateTime

interface FlightTestData {
    fun aFlight(
        airline: String = "airline",
        supplier: String = "Turbojet",
        fare: BigDecimal = BigDecimal("10.0"),
        departureAirportCode: String = "AAA",
        destinationAirportCode: String = "BBB",
        departureDate: LocalDateTime = LocalDateTime.of(2020, 1, 1, 12, 12),
        arrivalDate: LocalDateTime = LocalDateTime.of(2020, 1, 2, 12, 12)
    ) = Flight(
        airline = airline,
        supplier = supplier,
        fare = fare,
        departureAirportCode = departureAirportCode,
        destinationAirportCode = destinationAirportCode,
        departureDate = departureDate,
        arrivalDate = arrivalDate,
    )

}