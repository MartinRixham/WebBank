package com.webbank.database.account

interface Accounts {

    suspend fun save(account: Account): String

    suspend fun list(): AccountList
}
