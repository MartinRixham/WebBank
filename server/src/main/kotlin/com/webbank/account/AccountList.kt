package com.webbank.account

import kotlinx.serialization.Serializable

@Serializable
data class AccountList(val accounts: List<Account> = emptyList())
