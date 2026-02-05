package com.cortana.security

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SecureStorageInstrumentedTest {
    private lateinit var storage: SecureStorage

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        storage = SecureStorageFactory.create(
            context,
            SecureStorageConfig(fileName = "test_${UUID.randomUUID()}")
        )
    }

    @Test
    fun putAndGetString() {
        storage.putString("token", "abc123")
        assertThat(storage.getString("token")).isEqualTo("abc123")
    }

    @Test
    fun putAndGetBytes() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        storage.putBytes("bytes", bytes)
        assertThat(storage.getBytes("bytes")).isEqualTo(bytes)
    }

    @Test
    fun removeKey() {
        storage.putString("temp", "value")
        storage.remove("temp")
        assertThat(storage.getString("temp")).isNull()
    }
}
