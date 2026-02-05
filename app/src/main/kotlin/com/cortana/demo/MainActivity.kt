package com.cortana.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.cortana.demo.ui.AppScreen
import com.cortana.demo.ui.theme.SecureStorageDemoTheme
import com.cortana.security.SecureStorageFactory

class MainActivity : ComponentActivity() {
    private val storage by lazy { SecureStorageFactory.create(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SecureStorageDemoTheme {
                AppScreen(storage)
            }
        }
    }
}
