package com.deblock.exercise.flights

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class FlightsRequest(
    @field:NotNull(message = "Hello") @field:Pattern(regexp = "^([A-Za-z]{3}+)\$")
    val origin: String,

    @field:NotNull(message = "Hello") @field:Pattern(regexp = "^([A-Za-z]{3}+)\$")
    val destination: String,

    @field:NotNull(message = "Hello") @field:DateTimeFormat( pattern = "yyyy-MM-dd")
    val departureDate: LocalDate,

    @field:NotNull(message = "Hello") @field:DateTimeFormat( pattern = "yyyy-MM-dd")
    val returnDate: LocalDate,

    @field:NotNull(message = "Hello") @field:Min(1) @field:Max(4)
    val numberOfPassengers: Int
)
