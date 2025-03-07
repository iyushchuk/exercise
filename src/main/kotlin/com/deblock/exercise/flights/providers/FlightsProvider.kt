package com.deblock.exercise.flights.providers

import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.FlightsRequest

interface FlightsProvider {
    fun fetchFlights(request: FlightsRequest): List<Flight>
}