# kotlinserializationspringintegration

I'm trying out [kotlinx.serialization](https://kotlinlang.org/docs/serialization.html) integrations with [Spring Boot](https://spring.io/projects/spring-boot).

## Main takeaways and gotchas

* The integration between Spring Boot and Kotlin serialization just works.
* You must annotate bbth interface an implementing classes with `kotlinx.serialization.Serializable` to get polymorphism working.
* Wen you serialize a subclass (Car and Motorcycle in my example) the produced JSON **does not include** a discriminator! You have to cast it to its baseclass (Vehicle) to get that.