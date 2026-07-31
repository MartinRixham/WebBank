package com.webbank.account

import kotlin.test.Test
import kotlin.test.assertEquals

class AccountTest {

    @Test
    fun `describes itself with a field for each detail`() {
        val account =
            Account(
                firstName = "Eleanor",
                lastName = "Whitmore",
                email = "e.whitmore@email.com",
                phone = "+1 (555) 000-0000",
                dateOfBirth = "01 / 01 / 1990",
                ssn = "6789"
            )

        assertEquals(
            mapOf(
                "firstName" to "Eleanor",
                "lastName" to "Whitmore",
                "email" to "e.whitmore@email.com",
                "phone" to "+1 (555) 000-0000",
                "dateOfBirth" to "01 / 01 / 1990",
                "ssn" to "6789"
            ),
            account.fields()
        )
    }

    @Test
    fun `describes a detail the customer left empty`() {
        val account = Account("Eleanor", "Whitmore", "", "", "", "")

        assertEquals("", account.fields()["email"])
    }
}
