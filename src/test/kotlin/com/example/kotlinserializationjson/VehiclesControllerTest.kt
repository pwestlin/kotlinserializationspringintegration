package com.example.kotlinserializationjson

import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.verifySequence
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.net.URI

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

    private val vehicles: ArrayList<Vehicle> = arrayListOf(
        Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5),
        Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007, noSeats = 5),
        Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
    )

    @Test
    fun `get all vehicles`() {
        every { vehiclesRepository.all() } returns vehicles

        client.get()
            .uri("/vehicles")
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

    @Test
    fun `add a vehicle with an id that already exist`() = runTest {
        val car = Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5)
        val exception = IllegalArgumentException("Id ${car.id} already exist")
        every { vehiclesRepository.add(car) } returns Result.failure(exception)

        // You must specify <Vehicle> explicitly otherwiew class discriminator ("type":"com.example.kotlinserializationjson.Car") is not added to the JSON output.
        // Se https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md
        val json = Json.encodeToString<Vehicle>(car)
        val httpStatusResponse = HttpStatus.CONFLICT
        client.post()
            .uri("/vehicles")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isEqualTo(httpStatusResponse)
            .returnResult<ProblemDetail>().let { result ->
                with(result.responseBody.awaitFirst()) {
                    println("this = $this")
                    assertThat(type).isEqualTo(URI("about:blank"))
                    assertThat(title).isEqualTo(httpStatusResponse.reasonPhrase)
                    assertThat(status).isEqualTo(httpStatusResponse.value())
                    assertThat(detail).isEqualTo(exception.message)
                    assertThat(instance).isEqualTo(URI("/vehicles"))
                }
            }

        verifySequence { vehiclesRepository.add(car) }
    }

    @Test
    fun `add a vehicle - repo throws exception`() = runTest {
        val car = Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5)
        val exception = RuntimeException("It went to shite")
        every { vehiclesRepository.add(car) } returns Result.failure(exception)

        // You must specify <Vehicle> explicitly otherwiew class discriminator ("type":"com.example.kotlinserializationjson.Car") is not added to the JSON output.
        // Se https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md
        val json = Json.encodeToString<Vehicle>(car)
        val httpStatusResponse = HttpStatus.INTERNAL_SERVER_ERROR
        client.post()
            .uri("/vehicles")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(json)
            .exchange()
            .expectStatus().isEqualTo(httpStatusResponse)
            .returnResult<ProblemDetail>().let { result ->
                with(result.responseBody.awaitFirst()) {
                    println("this = $this")
                    assertThat(type).isEqualTo(URI("about:blank"))
                    assertThat(title).isEqualTo(httpStatusResponse.reasonPhrase)
                    assertThat(status).isEqualTo(httpStatusResponse.value())
                    assertThat(detail).isEqualTo(exception.message)
                    assertThat(instance).isEqualTo(URI("/vehicles"))
                }
            }

        verifySequence { vehiclesRepository.add(car) }
    }


    @Test
    fun `get all cars`() {
        val cars = vehicles.filterIsInstance<Car>()
        every { vehiclesRepository.cars() } returns cars

        client.get()
            .uri("/vehicles/cars")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(Json.encodeToString(cars))

        verifySequence { vehiclesRepository.cars() }
    }

    @Test
    fun `get vehicle by id that exist`() {
        val vehicle = vehicles.last()
        every { vehiclesRepository.byId(vehicle.id) } returns vehicle

        client.get()
            .uri("/vehicles/${vehicle.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody().json(Json.encodeToString(vehicle))

        verifySequence { vehiclesRepository.byId(vehicle.id) }
    }

    @Test
    fun `get vehicle by id that does not exist`() {
        val id = -42
        every { vehiclesRepository.byId(id) } returns null

        client.get()
            .uri("/vehicles/$id")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty

        verifySequence { vehiclesRepository.byId(id) }
    }
}