package com.cortana.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object SecureStorageFactory {
    fun create(
        context: Context,
        config: SecureStorageConfig = SecureStorageConfig()
    ): SecureStorage {
        val appContext = context.applicationContext
        val masterKey = MasterKey.Builder(appContext, config.keyAlias)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            appContext,
            config.fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return EncryptedSecureStorage(prefs)
    }
}

data class SecureStorageConfig(
    val fileName: String = "secure_storage",
    val keyAlias: String = MasterKey.DEFAULT_MASTER_KEY_ALIAS
)
