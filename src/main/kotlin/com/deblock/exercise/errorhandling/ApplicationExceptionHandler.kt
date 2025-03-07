package com.deblock.exercise.errorhandling

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.badRequest
import org.springframework.http.ResponseEntity.internalServerError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class ApplicationExceptionHandler : ResponseEntityExceptionHandler() {

    @ResponseStatus(value = BAD_REQUEST)
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> = badRequest().body(ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage })

    @ExceptionHandler(value = [ApplicationException::class])
    @ResponseStatus(value = INTERNAL_SERVER_ERROR)
    protected fun handleInternalError(ex: ApplicationException, request: WebRequest): ResponseEntity<Any> =
        internalServerError().body(
            ApplicationError(
                status = INTERNAL_SERVER_ERROR,
                errorMessage = ex.errorMessage
            )
        )


    data class ApplicationError(
        val status: HttpStatus,
        val errorMessage: String,
        val errors: List<String> = emptyList()
    )
}