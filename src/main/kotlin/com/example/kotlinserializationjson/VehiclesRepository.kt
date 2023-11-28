package com.example.kotlinserializationjson

import org.springframework.stereotype.Repository

@Repository
class VehiclesRepository(
    vehiclesDatabase: VehiclesDatabase
) {

    private val vehicles: ArrayList<Vehicle> = vehiclesDatabase.vehicles

    fun all(): List<Vehicle> = vehicles

    fun add(vehicle: Vehicle): Result<Unit> {
        return if (vehicles.none { it.id == vehicle.id }) {
            vehicles.add(vehicle)
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("vehicle.id ${vehicle.id} already exist"))
        }
    }

    fun cars(): List<Car> {
        return vehicles.filterIsInstance<Car>()
    }

    fun byId(id: Int): Vehicle? {
        return vehicles.firstOrNull { it.id == id }
    }
}