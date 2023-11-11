package com.example.bookbuddy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
data class User(val id: Int, val test: String)
    fun connection() {


        val jdbcUrl = "jdbc:mysql://2oq.h.filess.io:3307/BookBuddy_usingyear"

        // get the connection
        val connection = DriverManager
            .getConnection(
                jdbcUrl,
                "BookBuddy_usingyear",
                "3c9a82c9c1a80ef787584b6c504b2927bc2c2d87"
            )

        // prints true if the connection is valid
        println(connection.isValid(0))

        // the query is only prepared not executed
        val query = connection?.prepareStatement("SELECT * FROM users")

        // the query is executed and results are fetched
        val result = query?.executeQuery()

        // an empty list for holding the results
        val users = mutableListOf<User>()

        while (result?.next() == true) {

            // getting the value of the id column
            val id = result.getInt("idusers")

            // getting the value of the name column
            val test = result.getString("user_name")

            users.add(User(id, test))
            println(users)
        }
    }


