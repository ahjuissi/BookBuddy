package com.example.bookbuddy

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.bookbuddy.MySQLDatabaseExampleKotlin.conn
import com.example.bookbuddy.MySQLDatabaseExampleKotlin.main
import com.example.bookbuddy.databinding.ActivitySignupBinding
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.Properties

object MySQLDatabaseExampleKotlin {

    internal var conn: Connection? = null
    internal var username = "username" // provide the username
    internal var password = "password" // provide the corresponding password

    @JvmStatic
    fun main(args: Array<String>) {
        // make a connection to MySQL Server
        getConnection()
        // execute the query via connection object
        executeMySQLQuery()
    }
    fun executeMySQLQuery() {
        var stmt: Statement? = null
        var resultset: ResultSet? = null

        try {
            stmt = conn!!.createStatement()
            resultset = stmt!!.executeQuery("Select * from users;")

            if (stmt.execute("Select * from users;")) {
                resultset = stmt.resultSet
            }

            while (resultset!!.next()) {
                println(resultset.getString("user_name"))

            }
        } catch (ex: SQLException) {
            // handle any errors
            ex.printStackTrace()
        } finally {
            // release resources
            if (resultset != null) {
                try {
                    resultset.close()
                } catch (sqlEx: SQLException) {
                }

                resultset = null
            }

            if (stmt != null) {
                try {
                    stmt.close()
                } catch (sqlEx: SQLException) {
                }

                stmt = null
            }

            if (conn != null) {
                try {
                    conn!!.close()
                } catch (sqlEx: SQLException) {
                }

                conn = null
            }
        }
    }
}
fun getConnection() {
    val connectionProps = Properties()
    connectionProps.put("user", "BookBuddy_usingyear")
    connectionProps.put("password", "3c9a82c9c1a80ef787584b6c504b2927bc2c2d87")
    try {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance()
        conn = DriverManager.getConnection(
            "jdbc:" + "mysql" + "://" +
                    "2oq.h.filess.io" +
                    ":" + "3307" + "/" +
                    "BookBuddy_usingyear",
            connectionProps
        )
    } catch (ex: SQLException) {
        // handle any errors
        ex.printStackTrace()
    } catch (ex: Exception) {
        // handle any errors
        ex.printStackTrace()
    }
}

class SignupActivity : AppCompatActivity() {
    private lateinit var bindingSignup: ActivitySignupBinding

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindingSignup = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(bindingSignup.root)

        bindingSignup.btnSignupSubmit.setOnClickListener {

            val name = bindingSignup.etUserName.text.toString()
            val surname = bindingSignup.etUserSurname.text.toString()
            val mail = bindingSignup.etUserMail.text.toString()
            val password = bindingSignup.etUserMail.text.toString()
            val password2 = bindingSignup.etUserMail.text.toString()
            val city = bindingSignup.spinnerCity.toString()
            val role = bindingSignup.spinnerRole.toString()
        }

        bindingSignup.loginRedirectText.setOnClickListener {
            val signupIntent = Intent(this, MainActivity::class.java)
            startActivity(signupIntent)
        }


    }
}
//"Insert into users (user_name, user_surname, user_email, user_password, user_city, user_role,isActive)" +
//                      " values ('$name','$surname','$mail','$password1',4,'$role',0);"

