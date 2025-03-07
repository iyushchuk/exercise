package com.deblock.exercise.flights.suppliers.crazyair


import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.FlightsRequest
import com.deblock.exercise.flights.suppliers.FlightsSupplier
import org.springframework.stereotype.Service


@Service
class CrazyAirFlightsSupplier(
    private val crazyAirClient: CrazyAirClient
): FlightsSupplier {

    override fun requestFlights(request: FlightsRequest): List<Flight> {
        return crazyAirClient.getFlights(request).map { it.toFlight() }
    }
}

