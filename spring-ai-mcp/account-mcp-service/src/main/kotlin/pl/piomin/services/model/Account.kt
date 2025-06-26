package pl.piomin.services.model

import jakarta.persistence.*

@Entity
class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    
    var number: String? = null
    var balance: Int = 0
    var personId: Long? = null
}