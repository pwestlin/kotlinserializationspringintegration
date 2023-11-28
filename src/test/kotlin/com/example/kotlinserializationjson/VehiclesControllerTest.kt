package com.example.kotlinserializationjson

import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verifySequence
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@WebFluxTest
class VehiclesControllerTest {

    @Autowired
    private lateinit var client: WebTestClient

    @MockkBean
    private lateinit var vehiclesRepository: VehiclesRepository

    @AfterEach
    fun clearMocks() {
        clearAllMocks()
    }

    @Test
    fun `get all vehicles`() {
        val vehicles: ArrayList<Vehicle> = arrayListOf(
            Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5),
            Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007, noSeats = 5),
            Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
        )

        every { vehiclesRepository.all() } returns vehicles

        client.get()
            .uri("/vehicles/all")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(Json.encodeToString(vehicles))

        verifySequence { vehiclesRepository.all() }
    }

    @Test
    fun `add a vehicle`() {
        val car = Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5)
        every { vehiclesRepository.add(car) } returns Result.success(Unit)

        // You must specify <Vehicle> explicitly otherwiew class discriminator ("type":"com.example.kotlinserializationjson.Car") is not added to the JSON output.
        // Se https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md
        val json = Json.encodeToString<Vehicle>(car)
        client.post()
            .uri("/vehicles")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isOk
            .expectBody().isEmpty()

        verifySequence { vehiclesRepository.add(car) }
    }
}