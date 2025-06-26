package pl.piomin.services.model

import jakarta.persistence.*

@Entity
class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var firstName: String? = null
    var lastName: String? = null
    var age: Int = 0
    var nationality: String? = null

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null
}