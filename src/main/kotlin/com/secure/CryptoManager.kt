package com.secure

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Wrapper for AES-GCM encryption with hardware acceleration support.
 */
object CryptoManager {
    private val keyGen = KeyGenerator.getInstance("AES")
    private val random = SecureRandom()

    fun generateKey(): SecretKey {
        keyGen.init(256, random)
        return keyGen.generateKey()
    }

    fun encrypt(data: ByteArray, key: SecretKey): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        return cipher.doFinal(data)
    }
}
