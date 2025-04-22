package com.math_problem_server.user

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<UserModel, String> {
    fun findByEmail(email: String): UserModel?
}
