package com.deblock.exercise.flights.suppliers.crazyair

import com.deblock.exercise.flights.FlightsRequest

interface CrazyAirClient {
    fun getFlights(request: FlightsRequest): List<CrazyAirFlight>
}