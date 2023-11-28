package com.example.kotlinserializationjson

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class VehiclesRepositoryTest {

    private lateinit var vehiclesDatabase: VehiclesDatabase

    private lateinit var repository: VehiclesRepository

    @BeforeEach
    fun setup() {
        vehiclesDatabase = InMemoryVehiclesDatabase()
        repository = VehiclesRepository(vehiclesDatabase)
    }

    @Test
    fun `get all vehicles`() {
        assertThat(repository.all()).containsExactlyInAnyOrderElementsOf(vehiclesDatabase.vehicles)
    }

    @Test
    fun `add a vehicle that don't exist`() {
        val vehicle = Car(id = 5, name = "Rejsbussen", brand = "Peugeot", model = "Boxer", year = 2004, noSeats = 3)
        println(vehiclesDatabase.vehicles)
        assertThat(repository.add(vehicle)).isEqualTo(Result.success(Unit))
        println(vehiclesDatabase.vehicles)
        assertThat(repository.all()).containsExactlyInAnyOrderElementsOf(vehiclesDatabase.vehicles + vehicle)
    }

    @Test
    fun `add a vehicle that exist`() {
        val vehicle = vehiclesDatabase.vehicles.first()
        val result = repository.add(vehicle)

        assertThat(result.isSuccess).isFalse()
        assertThat(result.exceptionOrNull()!!)
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("vehicle.id ${vehicle.id} already exist")
        assertThat(repository.all()).containsExactlyInAnyOrderElementsOf(vehiclesDatabase.vehicles)
    }

    @Test
    fun `all cars`() {
        assertThat(repository.cars()).containsExactlyInAnyOrderElementsOf(vehiclesDatabase.vehicles.filterIsInstance<Car>())
    }

    @Test
    fun `vehicle by id that exist`() {
        val vehicle = vehiclesDatabase.vehicles.last()
        assertThat(repository.byId(vehicle.id)).isEqualTo(vehicle)
    }

    @Test
    fun `vehicle by id that does not exist`() {
        assertThat(repository.byId(-42)).isNull()
    }
}
