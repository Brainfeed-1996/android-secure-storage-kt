package com.cortana.demo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DemoColorScheme = darkColorScheme(
    primary = BlueGlow,
    secondary = Mint,
    background = Midnight,
    surface = Color(0xFF111827),
    onPrimary = Color(0xFF0B1220),
    onSecondary = Color(0xFF0B1220),
    onBackground = Ice,
    onSurface = Ice
)

@Composable
fun SecureStorageDemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DemoColorScheme,
        typography = Typography,
        content = content
    )
}
