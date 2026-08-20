package com.webbank.account

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class HttpAccounts(
    private val client: HttpClient,
    private val url: String = "http://localhost:8081") : Accounts {

    override suspend fun save(account: Account): String =
        client.post("$url/account") {
            contentType(ContentType.Application.Json)
            setBody(account)
        }.body<AccountCreated>().id

    override suspend fun list(): AccountList =
        client.get("$url/accounts").body()
}
