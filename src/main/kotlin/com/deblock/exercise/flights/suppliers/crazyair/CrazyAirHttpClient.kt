package com.deblock.exercise.flights.suppliers.crazyair

import com.deblock.exercise.errorhandling.ApplicationException
import com.deblock.exercise.flights.FlightsRequest
import com.deblock.exercise.flights.suppliers.turbojet.TurboJetHttpClient.Companion.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE


@Component
class CrazyAirHttpClient(
    private val restClient: RestClient,
    @Value("\${crazyair.api.url}") private val url: String
) : CrazyAirClient {

    override fun getFlights(request: FlightsRequest): List<CrazyAirFlight> {
        val uri = fromUriString("$url/flights")
            .queryParam("origin", request.origin)
            .queryParam("destination", request.destination)
            .queryParam("departureDate", request.departureDate.format(ISO_LOCAL_DATE))
            .queryParam("returnDate", request.returnDate.format(ISO_LOCAL_DATE))
            .queryParam("passengerCount", request.numberOfPassengers)
            .build()
            .toUri()

        return restClient.get()
            .uri(uri)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { _, response ->
                log.error("Bad request to CrazyAir flights: ${response.body}")
                throw ApplicationException("Failed to request CrazyAir flights")
            }
            .onStatus({ status -> status.is5xxServerError }) { _, response ->
                log.error("CrazyAir flights API error: ${response.body}")
                throw ApplicationException("Failed to request CrazyAir flights")
            }
            .body(object : ParameterizedTypeReference<List<CrazyAirFlight>>() {})
            ?: throw ApplicationException("Failed to get CrazyAir flights")
    }
}