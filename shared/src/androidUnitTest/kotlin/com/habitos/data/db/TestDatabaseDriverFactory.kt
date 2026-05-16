package com.habitos.data.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

fun createInMemoryDriver(): SqlDriver {
    val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
    HabitosDatabase.Schema.create(driver)

    // Enable foreign key constraints in SQLite via PRAGMA
    driver.execute(null, "PRAGMA foreign_keys = ON", 0)

    return driver
}
