package com.deblock.exercise.flights.suppliers

import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.FlightsRequest

interface FlightsSupplier {
    fun requestFlights(request: FlightsRequest): List<Flight>
}