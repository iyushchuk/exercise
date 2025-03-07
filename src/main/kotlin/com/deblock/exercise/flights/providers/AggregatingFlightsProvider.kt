package com.deblock.exercise.flights.providers

import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.FlightsRequest
import com.deblock.exercise.flights.suppliers.FlightsSupplier
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture.allOf
import java.util.concurrent.CompletableFuture.supplyAsync

@Service
class AggregatingFlightsProvider(
    private val flightsSuppliers: List<FlightsSupplier>
): FlightsProvider {

    override fun fetchFlights(request: FlightsRequest): List<Flight> {
        val asyncRequests = flightsSuppliers.map { supplyAsync { it.requestFlights(request) }
            .exceptionally { listOf() } }

        return allOf(*asyncRequests.toTypedArray())
            .thenApply { asyncRequests.map { it.join() }.flatten() }
            .get()
    }
}