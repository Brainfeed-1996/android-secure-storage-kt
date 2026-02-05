package com.cortana.security

import android.content.SharedPreferences

internal class EncryptedSecureStorage(
    private val prefs: SharedPreferences
) : SecureStorage {
    override fun putString(key: String, value: String?) {
        KeyValidator.requireValidKey(key)
        prefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String, defaultValue: String?): String? {
        KeyValidator.requireValidKey(key)
        return prefs.getString(key, defaultValue)
    }

    override fun putBoolean(key: String, value: Boolean) {
        KeyValidator.requireValidKey(key)
        prefs.edit().putBoolean(key, value).apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        KeyValidator.requireValidKey(key)
        return prefs.getBoolean(key, defaultValue)
    }

    override fun putInt(key: String, value: Int) {
        KeyValidator.requireValidKey(key)
        prefs.edit().putInt(key, value).apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        KeyValidator.requireValidKey(key)
        return prefs.getInt(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        KeyValidator.requireValidKey(key)
        prefs.edit().putLong(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        KeyValidator.requireValidKey(key)
        return prefs.getLong(key, defaultValue)
    }

    override fun putFloat(key: String, value: Float) {
        KeyValidator.requireValidKey(key)
        prefs.edit().putFloat(key, value).apply()
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        KeyValidator.requireValidKey(key)
        return prefs.getFloat(key, defaultValue)
    }

    override fun putBytes(key: String, value: ByteArray?) {
        KeyValidator.requireValidKey(key)
        val encoded = Encoding.encodeBytes(value)
        prefs.edit().putString(key, encoded).apply()
    }

    override fun getBytes(key: String): ByteArray? {
        KeyValidator.requireValidKey(key)
        val encoded = prefs.getString(key, null)
        return Encoding.decodeBytes(encoded)
    }

    override fun remove(key: String) {
        KeyValidator.requireValidKey(key)
        prefs.edit().remove(key).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }

    override fun contains(key: String): Boolean {
        KeyValidator.requireValidKey(key)
        return prefs.contains(key)
    }

    override fun allKeys(): Set<String> {
        return prefs.all.keys
    }
}
