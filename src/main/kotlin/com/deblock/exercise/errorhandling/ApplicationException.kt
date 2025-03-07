package com.deblock.exercise.errorhandling

class ApplicationException(val errorMessage: String) : RuntimeException(errorMessage)