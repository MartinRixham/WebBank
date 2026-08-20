package com.webbank.account

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val dateOfBirth: String,
    val ssn: String)
