package com.webbank.account

import kotlinx.serialization.Serializable

// The database service declares an account of its own. The two are a wire
// contract rather than a shared class, so that the server carries none of the
// database's dependencies.
@Serializable
data class Account(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val ssn: String
)
