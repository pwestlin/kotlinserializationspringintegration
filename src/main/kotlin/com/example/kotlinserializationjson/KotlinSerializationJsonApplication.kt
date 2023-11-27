package com.example.kotlinserializationjson

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
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
                Car(name = "Vovvon", brand = "Volvo", model = "V16", year = 2019),
                Car(name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007),
                Motorcycle(name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
            )

    @GetMapping("/all")
    fun all(): List<Vehicle> {
        logger.info(Json.encodeToString(vehicles))
        logger.info(Json.encodeToString(vehicles.first()))

        return vehicles
    }

    @PostMapping("")
    fun saveNew(@RequestBody vehicle: Vehicle) {
        logger.info("body: $vehicle")
        vehicles.add(vehicle)
    }
}

@Serializable
sealed interface Vehicle {
    val name: String
}

@Serializable
data class Car(
    val brand: String,
    val model: String,
    val year: Int, override val name: String
): Vehicle

@Serializable
data class Motorcycle(
    val brand: String,
    val model: String,
    val year: Int, override val name: String
): Vehicle