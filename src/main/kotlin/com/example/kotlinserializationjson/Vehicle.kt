package com.example.kotlinserializationjson

import kotlinx.serialization.Serializable

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
