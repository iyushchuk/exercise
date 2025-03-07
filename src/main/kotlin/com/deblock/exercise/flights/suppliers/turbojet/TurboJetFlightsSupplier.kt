package com.deblock.exercise.flights.suppliers.turbojet

import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.FlightsRequest
import com.deblock.exercise.flights.suppliers.FlightsSupplier
import org.springframework.stereotype.Service

@Service
class TurboJetFlightsSupplier(
    private val turboJetClient: TurboJetClient
): FlightsSupplier {

    override fun requestFlights(request: FlightsRequest): List<Flight> {
        return turboJetClient.getFlights(request).map { it.toFlight() }
    }
}

