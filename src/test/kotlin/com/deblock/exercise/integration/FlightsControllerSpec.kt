package com.deblock.exercise.integration

import com.deblock.exercise.testdata.FlightTestData
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE


@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(proxyMode = true)
class FlightsControllerSpec : FlightTestData {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @Test
    fun `returns flights provided by multiple suppliers`() {
        // given
        val origin = "AAA"
        val destination = "BBB"
        val passengers = 4
        val departureDate = LocalDateTime.of(2011, 12, 3, 10, 15, 30)
        val arrivalDate = LocalDateTime.of(2011, 12, 5, 10, 15, 30)

        stubFor(
            get(urlPathEqualTo("/flights"))
                .withHost(equalTo("www.crazyair.com"))
                .withQueryParam("origin", equalTo(origin))
                .withQueryParam("destination", equalTo(destination))
                .withQueryParam("departureDate", equalTo(departureDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("returnDate", equalTo(arrivalDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("passengerCount", equalTo(passengers.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(
                            """
                            [
                                {
                                    "airline": "Lufthansa",
                                    "price": 100.00,
                                    "cabinClass": "B",
                                    "departureAirportCode": "AAA",
                                    "destinationAirportCode": "BBB",
                                    "departureDate": "2011-12-03T10:15:30",
                                    "arrivalDate": "2011-12-05T10:15:30"
                                }
                           ]
                            """.trimIndent()
                        )
                        .withHeader("Content-Type", "application/json")
                )
        )

        stubFor(
            get(urlPathEqualTo("/flights"))
                .withHost(equalTo("www.turbojet.com"))
                .withQueryParam("from", equalTo(origin))
                .withQueryParam("to", equalTo(destination))
                .withQueryParam("outboundDate", equalTo(departureDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("inboundDate", equalTo(arrivalDate.format(ISO_LOCAL_DATE)))
                .withQueryParam("numberOfAdults", equalTo(passengers.toString()))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(
                            """
                            [
                                {
                                    "carrier": "Ryanair",
                                    "basePrice": 100.00,
                                    "tax": "12",
                                    "discount": "15",
                                    "departureAirportName": "AAA",
                                    "arrivalAirportName": "BBB",
                                    "outboundDateTime": "1322907330",
                                    "inboundDateTime": "1323080130"
                                }
                           ]
                            """.trimIndent()
                        )
                        .withHeader("Content-Type", "application/json")
                )
        )

        val requestUrl = fromUriString("/flights")
            .queryParam("origin", origin)
            .queryParam("destination", destination)
            .queryParam("departureDate", departureDate.format(ISO_LOCAL_DATE))
            .queryParam("returnDate", arrivalDate.format(ISO_LOCAL_DATE))
            .queryParam("numberOfPassengers", passengers)
            .build()
            .toString()

        // then
        mockMvc.get(requestUrl) {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { contentType(APPLICATION_JSON) }
            content {
                json(
                    """
                [
                  {
                    "airline":"Lufthansa",
                    "supplier":"CrazyAir",
                    "fare":100.00,
                    "departureAirportCode":"AAA",
                    "destinationAirportCode":"BBB",
                    "departureDate":"2011-12-03T10:15:30",
                    "arrivalDate":"2011-12-05T10:15:30"
                  },
                  {
                    "airline":"Ryanair",
                    "supplier":"TurboJet",
                    "fare":95.20,
                    "departureAirportCode":"AAA",
                    "destinationAirportCode":"BBB",
                    "departureDate":"2011-12-03T10:15:30",
                    "arrivalDate":"2011-12-05T10:15:30"}
                ]
            """.trimIndent()
                )
            }
        }
    }

    @Test
    fun `returns bad request response given wrong number of passengers`() {
        // given
        val origin = "AAA"
        val destination = "BBB"
        val passengers = 10
        val departureDate = LocalDateTime.of(2011, 12, 3, 10, 15, 30)
        val arrivalDate = LocalDateTime.of(2011, 12, 5, 10, 15, 30)

        val requestUrl = fromUriString("/flights")
            .queryParam("origin", origin)
            .queryParam("destination", destination)
            .queryParam("departureDate", departureDate.format(ISO_LOCAL_DATE))
            .queryParam("returnDate", arrivalDate.format(ISO_LOCAL_DATE))
            .queryParam("numberOfPassengers", passengers)
            .build()
            .toString()

        // then
        mockMvc.get(requestUrl) {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            content { contentType(APPLICATION_JSON) }
            content {
                json(
                    """
                {"numberOfPassengers":"must be less than or equal to 4"}
         """.trimIndent()
                )
            }
        }

    }
}