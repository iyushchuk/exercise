package com.deblock.exercise.integration

import com.deblock.exercise.errorhandling.ApplicationException
import com.deblock.exercise.flights.suppliers.turbojet.TurboJetFlight
import com.deblock.exercise.flights.suppliers.turbojet.TurboJetHttpClient
import com.deblock.exercise.testdata.FlightRequestTestData
import com.deblock.exercise.testdata.FlightTestData
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE


@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(proxyMode = true)
class TurboJetHttpClientSpec : FlightTestData, FlightRequestTestData {

    @Autowired
    lateinit var turboJetHttpClient: TurboJetHttpClient

    @Test
    fun `returns flights fetched from http server`() {
        // given
        val flightsRequest = aFlightRequest()

        stubFor(
            get(urlPathEqualTo("/flights"))
                .withHost(equalTo("www.turbojet.com"))
                .withQueryParam("from", equalTo(flightsRequest.origin))
                .withQueryParam("to", equalTo(flightsRequest.destination))
                .withQueryParam("outboundDate", equalTo(flightsRequest.departureDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("inboundDate", equalTo(flightsRequest.returnDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("numberOfAdults", equalTo(flightsRequest.numberOfPassengers.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(
                            """
                            [
                                {
                                    "carrier": "Lufthansa",
                                    "basePrice": 100,
                                    "tax": "20",
                                    "discount": "10",
                                    "departureAirportName": "AAA",
                                    "arrivalAirportName": "BBB",
                                    "outboundDateTime": "1577874600",
                                    "inboundDateTime": "1577961000"
                                }
                           ]
                            """.trimIndent()
                        )
                        .withHeader("Content-Type", "application/json")
                )
        )

        val expectedFlight = TurboJetFlight(
            carrier = "Lufthansa",
            basePrice = BigDecimal("100"),
            tax = BigDecimal("20"),
            discount = BigDecimal("10"),
            departureAirportName = "AAA",
            arrivalAirportName = "BBB",
            outboundDateTime = LocalDateTime.of(2020, 1, 1, 10, 30).toInstant(UTC),
            inboundDateTime = LocalDateTime.of(2020, 1, 2, 10, 30).toInstant(UTC)
        )

        // when
        val flights = turboJetHttpClient.getFlights(flightsRequest)

        // then
        assertThat(flights).containsOnly(expectedFlight)
    }

    @Test
    fun `throws exception when API call returns bad request response`() {
        // given
        val flightsRequest = aFlightRequest()

        stubFor(
            get(urlPathEqualTo("/flights"))
                .withHost(equalTo("www.turbojet.com"))
                .withQueryParam("from", equalTo(flightsRequest.origin))
                .withQueryParam("to", equalTo(flightsRequest.destination))
                .withQueryParam("outboundDate", equalTo(flightsRequest.departureDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("inboundDate", equalTo(flightsRequest.returnDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("numberOfAdults", equalTo(flightsRequest.numberOfPassengers.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(400)
                        .withBody(
                            """
                            {
                                "status": "BAD_REQUEST",
                            }
                            """.trimIndent()
                        )
                        .withHeader("Content-Type", "application/json")
                )
        )

        // then
        assertThatThrownBy { turboJetHttpClient.getFlights(flightsRequest) }
            .isInstanceOf(ApplicationException::class.java)
            .hasMessage("Failed to request TurboJet flights")
    }

    @Test
    fun `throws exception when API call returns internal server response`() {
        // given
        val flightsRequest = aFlightRequest()

        stubFor(
            get(urlPathEqualTo("/flights"))
                .withHost(equalTo("www.turbojet.com"))
                .withQueryParam("from", equalTo(flightsRequest.origin))
                .withQueryParam("to", equalTo(flightsRequest.destination))
                .withQueryParam("outboundDate", equalTo(flightsRequest.departureDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("inboundDate", equalTo(flightsRequest.returnDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("numberOfAdults", equalTo(flightsRequest.numberOfPassengers.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withBody(
                            """
                            {
                                "status": "INTERNAL_SERVER_ERROR",
                            }
                            """.trimIndent()
                        )
                        .withHeader("Content-Type", "application/json")
                )
        )

        // then
        assertThatThrownBy { turboJetHttpClient.getFlights(flightsRequest) }
            .isInstanceOf(ApplicationException::class.java)
            .hasMessage("Failed to request TurboJet flights")
    }
}