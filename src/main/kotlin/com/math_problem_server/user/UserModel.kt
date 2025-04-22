package com.math_problem_server.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("users")
data class UserModel(
    @Id val id: String? = null,
    val firstName: String = "John",
    val lastName: String = "Doe",
    val dateOfBirth: Long = 0L, // Unix timestamp in milliseconds
    val email: String,
    val hashedPassword: String
)
