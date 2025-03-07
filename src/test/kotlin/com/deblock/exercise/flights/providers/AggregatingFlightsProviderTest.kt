package com.deblock.exercise.flights.providers

import com.deblock.exercise.flights.suppliers.FlightsSupplier
import com.deblock.exercise.testdata.FlightRequestTestData
import com.deblock.exercise.testdata.FlightTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.willReturn
import org.mockito.kotlin.willThrow

class AggregatingFlightsProviderTest : FlightRequestTestData, FlightTestData {

    private val crazyAirSupplier = mock<FlightsSupplier>()
    private val turboJetSupplier = mock<FlightsSupplier>()

    private val aggregatingFlightsProvider = AggregatingFlightsProvider(listOf(crazyAirSupplier, turboJetSupplier))

    @Test
    fun `returns aggregated list of flights given all suppliers return flights`() {
        // given
        val request = aFlightRequest()
        val crazyAirFlight = aFlight(supplier = "CrazyAir")
        val turboJetFlight = aFlight(supplier = "TurboJet")

        given { crazyAirSupplier.requestFlights(request) } willReturn { listOf(crazyAirFlight) }
        given { turboJetSupplier.requestFlights(request) } willReturn { listOf(turboJetFlight) }

        // when
        val flights = aggregatingFlightsProvider.fetchFlights(request)

        // then
        assertThat(flights).containsOnly(crazyAirFlight, turboJetFlight)
    }

    @Test
    fun `returns flights from one supplier given another fails`() {
        // given
        val request = aFlightRequest()
        val crazyAirFlight = aFlight(supplier = "CrazyAir")

        given { crazyAirSupplier.requestFlights(request) } willReturn { listOf(crazyAirFlight) }
        given { turboJetSupplier.requestFlights(request) } willThrow { IllegalStateException() }

        // when
        val flights = aggregatingFlightsProvider.fetchFlights(request)

        // then
        assertThat(flights).containsOnly(crazyAirFlight)
    }
}
