package com.deblock.exercise.testdata

import com.deblock.exercise.flights.FlightsRequest
import java.time.LocalDate

interface FlightRequestTestData {
    fun aFlightRequest() = FlightsRequest(
        origin = "origin",
        destination = "destination",
        departureDate = LocalDate.of(2020, 1, 1),
        returnDate = LocalDate.of(2020, 1, 2),
        numberOfPassengers = 4
    )
}