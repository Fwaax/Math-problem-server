package com.math_problem_server.user

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    @GetMapping("/")
    fun index(@RequestParam(name = "name", defaultValue = "unknown") name: String): ResponseEntity<String> {
        return when (name) {
            "error" -> {
                // Return an error response with HTTP status 400 (Bad Request)
                ResponseEntity("Error: Invalid name provided!", HttpStatus.BAD_REQUEST)
            }
            else -> {
                // Return a successful response with HTTP status 200 (OK)
                ResponseEntity("Hello, $name!", HttpStatus.OK)
            }
        }
    }
}
