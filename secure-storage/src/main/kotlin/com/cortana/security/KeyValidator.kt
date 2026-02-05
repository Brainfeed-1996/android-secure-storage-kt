package com.cortana.security

internal object KeyValidator {
    fun requireValidKey(key: String) {
        require(key.isNotBlank()) { "Key must not be blank." }
        require(!key.contains('\u0000')) { "Key must not contain null characters." }
    }
}
