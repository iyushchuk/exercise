package com.deblock.exercise.flights.suppliers.turbojet

import com.deblock.exercise.flights.Flight
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

data class TurboJetFlight(
    val carrier: String,
    val basePrice: BigDecimal,
    val tax: BigDecimal,
    val discount: BigDecimal,
    val departureAirportName: String,
    val arrivalAirportName: String,
    val outboundDateTime: Instant,
    val inboundDateTime: Instant
) {
    fun toFlight(): Flight {
        val discountedPrice = basePrice.subtract(basePrice.multiply(discount).divide(BigDecimal(100)))
        val priceAfterTax = discountedPrice.add(discountedPrice.multiply(tax).divide(BigDecimal(100)))
            .setScale(2, RoundingMode.HALF_UP)

        return Flight(
            airline = carrier,
            supplier = SUPPLIER,
            fare = priceAfterTax,
            departureAirportCode = departureAirportName,
            destinationAirportCode = arrivalAirportName,
            departureDate = LocalDateTime.ofInstant(outboundDateTime, UTC),
            arrivalDate = LocalDateTime.ofInstant(inboundDateTime, UTC)
        )
    }

    companion object {
        const val SUPPLIER = "TurboJet"
    }
}