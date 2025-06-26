package pl.piomin.services

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SampleClient {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplication.run(SampleClient::class.java, *args)
        }
    }
}