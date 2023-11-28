package com.example.kotlinserializationjson

import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class KotlinSerializationJsonApplication

fun main(args: Array<String>) {
    runApplication<KotlinSerializationJsonApplication>(*args)
}

@Repository
class VehiclesRepository {

    private val vehicles: ArrayList<Vehicle> = arrayListOf(
        Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5),
        Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007, noSeats = 5),
        Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
    )

    fun all(): List<Vehicle> = vehicles

    fun add(vehicle: Vehicle): Result<Unit> {
        return if (vehicles.none { it.id == vehicle.id }) {
            vehicles.add(vehicle)
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("vehicle.id ${vehicle.id} already exist"))
        }
    }
}

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
                                "vehicle.id ${vehicle.id} already exist"
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

@Serializable
sealed interface Vehicle {
    val id: Int
    val name: String
}

@Serializable
data class Car(
    override val id: Int,
    override val name: String,
    val brand: String,
    val model: String,
    val year: Int,
    val noSeats: Int
) : Vehicle

@Serializable
data class Motorcycle(
    override val id: Int,
    override val name: String,
    val brand: String,
    val model: String,
    val year: Int
) : Vehicle