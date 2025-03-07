package com.deblock.exercise.flights.suppliers.turbojet

import com.deblock.exercise.flights.FlightsRequest

interface TurboJetClient {
    fun getFlights(request: FlightsRequest): List<TurboJetFlight>
}