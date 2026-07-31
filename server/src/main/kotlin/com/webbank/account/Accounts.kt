package com.webbank.account

// The store the account route writes to, kept behind an interface so that the
// route is testable without a Redis instance to talk to.
interface Accounts {

    suspend fun save(account: Account): String
}
