package com.example.kotlinserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.http.ResponseEntity
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

@RestController
@RequestMapping("/vehicles")
class CarsController {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    private val vehicles: ArrayList<Vehicle> = arrayListOf(
        Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019),
        Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007),
        Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
    )

    @GetMapping("/all")
    fun all(): List<Vehicle> {
        logger.info(Json.encodeToString(vehicles))
        logger.info(Json.encodeToString(vehicles.first()))

        return vehicles
    }

    @PostMapping("")
    suspend fun saveNew(@RequestBody vehicle: Vehicle): ResponseEntity<Unit> {
        logger.info("body: $vehicle")
        return if (vehicles.none { it.id == vehicle.id }) {
            vehicles.add(vehicle)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity.badRequest().build()
        }
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
    val year: Int
) : Vehicle

@Serializable
data class Motorcycle(
    override val id: Int,
    override val name: String,
    val brand: String,
    val model: String,
    val year: Int
) : Vehicle