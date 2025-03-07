package com.deblock.exercise.flights.suppliers.turbojet

import com.deblock.exercise.errorhandling.ApplicationException
import com.deblock.exercise.flights.FlightsRequest
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE


@Component
class TurboJetHttpClient(
    private val restClient: RestClient,
    @Value("\${turbojet.api.url}") private val url: String
) : TurboJetClient {

    override fun getFlights(request: FlightsRequest): List<TurboJetFlight> {
        val uri = fromUriString("$url/flights")
            .queryParam("from", request.origin)
            .queryParam("to", request.destination)
            .queryParam("outboundDate", request.departureDate.format(ISO_LOCAL_DATE))
            .queryParam("inboundDate", request.returnDate.format(ISO_LOCAL_DATE))
            .queryParam("numberOfAdults", request.numberOfPassengers)
            .build()
            .toUri()

        return restClient.get()
            .uri(uri)
            .retrieve()
            .onStatus({ status -> status.is4xxClientError }) { _, response ->
                log.error("Bad request to TurboJet flights: ${response.body}")
                throw ApplicationException("Failed to request TurboJet flights")
            }
            .onStatus({ status -> status.is5xxServerError }) { _, response ->
                log.error("TurboJet flights API error: ${response.body}")
                throw ApplicationException("Failed to request TurboJet flights")
            }
            .body(object : ParameterizedTypeReference<List<TurboJetFlight>>() {})
            ?: throw ApplicationException("Failed to request turboJetFLights")
    }

    companion object {
        val log = getLogger(TurboJetHttpClient::class.java)
    }
}