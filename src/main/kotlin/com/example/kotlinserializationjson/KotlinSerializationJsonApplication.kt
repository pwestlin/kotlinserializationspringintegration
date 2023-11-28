package com.example.kotlinserializationjson

import kotlinx.serialization.Serializable
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSerializationJsonApplication

fun main(args: Array<String>) {
    runApplication<KotlinSerializationJsonApplication>(*args)
}
