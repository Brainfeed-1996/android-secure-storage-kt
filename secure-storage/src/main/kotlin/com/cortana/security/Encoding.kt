package com.cortana.security

import android.util.Base64

internal object Encoding {
    fun encodeBytes(value: ByteArray?): String? {
        if (value == null) return null
        return Base64.encodeToString(value, Base64.NO_WRAP)
    }

    fun decodeBytes(value: String?): ByteArray? {
        if (value.isNullOrBlank()) return null
        return try {
            Base64.decode(value, Base64.NO_WRAP)
        } catch (_: IllegalArgumentException) {
            null
        }
    }
}
