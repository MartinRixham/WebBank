package com.webbank.database.account

import kotlinx.serialization.Serializable

@Serializable
data class AccountList(val accounts: List<Account> = emptyList())
