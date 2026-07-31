package com.webbank.account

// The one thing the account store does to Redis, behind an interface so that
// the store itself is testable without a Redis instance to talk to.
interface Hashes {

    suspend fun set(key: String, fields: Map<String, String>)
}
