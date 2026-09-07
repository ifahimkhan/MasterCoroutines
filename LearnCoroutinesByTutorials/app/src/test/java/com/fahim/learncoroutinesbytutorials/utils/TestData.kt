package com.fahim.learncoroutinesbytutorials.utils

import com.fahim.learncoroutinesbytutorials.data.model.ApiUser

object TestData {

    val users = listOf(
        ApiUser(id = 1, name = "Alice", email = "alice@example.com", avatar = "a.png"),
        ApiUser(id = 2, name = "Bob", email = "bob@example.com", avatar = "b.png")
    )

    val moreUsers = listOf(
        ApiUser(id = 3, name = "Carol", email = "carol@example.com", avatar = "c.png")
    )
}
