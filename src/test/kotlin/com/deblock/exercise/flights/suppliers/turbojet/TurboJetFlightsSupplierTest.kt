package com.deblock.exercise.flights.suppliers.turbojet

import com.deblock.exercise.testdata.FlightRequestTestData
import com.deblock.exercise.testdata.FlightTestData
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.willReturn
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class TurboJetFlightsSupplierTest : FlightRequestTestData, FlightTestData {

    private val turboJetClient = mock<TurboJetClient>()

    private val turboJetFlightsSupplier = TurboJetFlightsSupplier(turboJetClient)

    @Test
    fun `returns list of converted flights given client returns flights`() {
        // given
        val request = aFlightRequest()
        val turboJetFlight = TurboJetFlight(
            carrier = "Lufthansa",
            basePrice = BigDecimal("100.00"),
            tax = BigDecimal("20.00"),
            discount = BigDecimal("10.00"),
            departureAirportName = "AAA",
            arrivalAirportName = "BBB",
            outboundDateTime = LocalDateTime.of(2020, 12, 12, 10, 30).toInstant(UTC),
            inboundDateTime = LocalDateTime.of(2020, 12, 13, 10, 30).toInstant(UTC)
        )
        val flight = aFlight(
            airline = "Lufthansa",
            supplier = "TurboJet",
            fare = BigDecimal("108.00"),
            departureAirportCode = "AAA",
            destinationAirportCode = "BBB",
            departureDate = LocalDateTime.of(2020, 12, 12, 10, 30),
            arrivalDate = LocalDateTime.of(2020, 12, 13, 10, 30)
        )

        given { turboJetClient.getFlights(request) } willReturn { listOf(turboJetFlight) }

        // when
        val flights = turboJetFlightsSupplier.requestFlights(request)

        // then
        assertThat(flights).containsOnly(flight)
    }
}
