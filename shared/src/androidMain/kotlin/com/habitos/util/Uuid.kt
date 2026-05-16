package com.habitos.util

import java.util.UUID

actual fun generateUuid(): String {
    return UUID.randomUUID().toString()
}
