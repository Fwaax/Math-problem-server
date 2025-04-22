package com.math_problem_server.user


import com.math_problem_server.utils.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@RestController
@RequestMapping("/api/user")
class UserController @Autowired constructor(
    val userRepository: UserRepository,
    val jwtUtil: JwtUtil
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @PostMapping("/register")
    fun register(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
        val email = request["email"] ?: return ResponseEntity.badRequest().body("Email is required")
        val password = request["password"] ?: return ResponseEntity.badRequest().body("Password is required")

        if (userRepository.findByEmail(email) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists")
        }

        val hashedPassword = passwordEncoder.encode(password)
        val newUser = UserModel(
            email = email,
            hashedPassword = hashedPassword
        )
        userRepository.save(newUser)

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: Map<String, String>): ResponseEntity<Any> {
        val email = request["email"] ?: return ResponseEntity.badRequest().body("Email is required")
        val password = request["password"] ?: return ResponseEntity.badRequest().body("Password is required")

        val user = userRepository.findByEmail(email)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")

        if (!passwordEncoder.matches(password, user.hashedPassword)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials")
        }

        val token = jwtUtil.generateToken(user.id ?: "")
        return ResponseEntity.ok(mapOf("token" to token))
    }

    @GetMapping("/getUser")
    fun getUser(request: HttpServletRequest): ResponseEntity<Any> {
        val authHeader = request.getHeader("Authorization")
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token")

        if (!authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token format")
        }

        val token = authHeader.removePrefix("Bearer ")
        val userId = jwtUtil.extractUserId(token)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token")

        val user = userRepository.findById(userId)
        return if (user.isPresent) {
            val u = user.get().copy(hashedPassword = "") // hide password
            ResponseEntity.ok(u)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")
        }
    }
}
