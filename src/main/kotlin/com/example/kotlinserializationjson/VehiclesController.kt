package com.example.kotlinserializationjson

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/vehicles")
class VehiclesController(
    private val vehiclesRepository: VehiclesRepository
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @GetMapping("/all")
    fun all(): List<Vehicle> {

        return vehiclesRepository.all()
    }

    @PostMapping("")
    suspend fun saveNew(@RequestBody vehicle: Vehicle): ResponseEntity<Any?> {
        logger.info("body: $vehicle")
        return vehiclesRepository.add(vehicle).fold(
            { ResponseEntity.ok().build() },
            {
                when (it) {
                    is IllegalArgumentException -> ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(
                            ProblemDetail.forStatusAndDetail(
                                HttpStatus.CONFLICT,
                                it.message ?: ""
                            )
                        )

                    else -> ResponseEntity.internalServerError()
                        .body(
                            ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                it.message ?: ""
                            )
                        )
                }
            }
        )
    }
}