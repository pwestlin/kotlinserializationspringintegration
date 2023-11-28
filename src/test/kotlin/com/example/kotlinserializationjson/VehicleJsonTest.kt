package com.example.kotlinserializationjson

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VehicleJsonTest {

    private val vehicles: List<Vehicle> = listOf(
        Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5),
        Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007, noSeats = 5),
        Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
    )

    private val car: Car = vehicles.filterIsInstance<Car>().first()
    private val motorcycle: Car = vehicles.filterIsInstance<Car>().first()

    private val jsonFormat = Json { prettyPrint = true }

    @Test
    fun `Car to JSON to Car`() {
        val json = jsonFormat.encodeToString(car)
        println("json = $json")
        assertThat(Json.decodeFromString<Car>(json)).isEqualTo(car)
    }

    @Test
    fun `Car to JSON to Vehicle`() {
        val json = jsonFormat.encodeToString<Vehicle>(car)
        println("json = $json")
        assertThat(Json.decodeFromString<Vehicle>(json)).isEqualTo(car)
    }

    @Test
    fun `Vehicle(Car) to JSON to Car`() {
        val vehicle: Vehicle = car
        val json = jsonFormat.encodeToString(vehicle)
        println("json = $json")
        val decodedFromJson: Vehicle = Json.decodeFromString(json)
        println("decodedFromJson = $decodedFromJson")
        val decodedCar = decodedFromJson as Car
        println("decodedCar = $decodedCar")
        assertThat(decodedCar).isEqualTo(car)
    }
}