package com.example.kotlinserializationjson

import org.springframework.stereotype.Component

interface VehiclesDatabase {
    val vehicles: ArrayList<Vehicle>
}

@Component
class InMemoryVehiclesDatabase : VehiclesDatabase {
    override val vehicles: ArrayList<Vehicle>
        get() = arrayListOf(
            Car(id = 1, name = "Vovvon", brand = "Volvo", model = "V16", year = 2019, noSeats = 5),
            Car(id = 2, name = "Fårrden", brand = "Ford", model = "Mondeo", year = 2007, noSeats = 5),
            Motorcycle(id = 3, name = "Enduron", brand = "KTM", model = "EXC-F 350", year = 2018),
            Motorcycle(id = 4, name = "Hyskyn", brand = "Husqvarna", model = "TC125", year = 2019),
        )
}
