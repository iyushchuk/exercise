package com.deblock.exercise.flights.controllers

import com.deblock.exercise.flights.Flight
import com.deblock.exercise.flights.providers.FlightsProvider
import com.deblock.exercise.flights.FlightsRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FlightsController(
    private val flightsProvider: FlightsProvider
) {
    @GetMapping("/flights")
    fun getFlights(@Valid request: FlightsRequest): List<Flight> {
        return flightsProvider.fetchFlights(request)
    }
}