package com.cortana.security

import org.junit.Assert.assertThrows
import org.junit.Test

class KeyValidatorTest {
    @Test
    fun blankKeyThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            KeyValidator.requireValidKey(" ")
        }
    }

    @Test
    fun nullCharKeyThrows() {
        assertThrows(IllegalArgumentException::class.java) {
            KeyValidator.requireValidKey("bad\u0000key")
        }
    }

    @Test
    fun validKeyPasses() {
        KeyValidator.requireValidKey("good_key")
    }
}
