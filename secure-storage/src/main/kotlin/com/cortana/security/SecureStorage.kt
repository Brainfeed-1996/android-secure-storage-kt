package com.cortana.security

interface SecureStorage {
    fun putString(key: String, value: String?)
    fun getString(key: String, defaultValue: String? = null): String?

    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0L): Long

    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun putBytes(key: String, value: ByteArray?)
    fun getBytes(key: String): ByteArray?

    fun remove(key: String)
    fun clear()
    fun contains(key: String): Boolean
    fun allKeys(): Set<String>
}
