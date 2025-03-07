package com.deblock.exercise.flights

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING
import java.math.BigDecimal
import java.time.LocalDateTime

data class Flight(
    val airline: String,
    val supplier: String,
    val fare: BigDecimal,
    val departureAirportCode: String,
    val destinationAirportCode: String,
    @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val departureDate: LocalDateTime,
    @field:JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val arrivalDate: LocalDateTime
)
