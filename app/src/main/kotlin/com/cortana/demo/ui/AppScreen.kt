package com.cortana.demo.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.cortana.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AppScreen(storage: SecureStorage) {
    var key by remember { mutableStateOf("user_token") }
    var value by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("auth") }
    var result by remember { mutableStateOf("Ready.") }
    var revealValue by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    var showOnboarding by remember { mutableStateOf(true) }
    var onboardingStep by remember { mutableStateOf(0) }
    var currentScreen by remember { mutableStateOf(Screen.Vault) }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0B1220), Color(0xFF111827), Color(0xFF1F2937))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Secure Storage Vault",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFE2E8F0),
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Store secrets with encrypted key/value storage.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF94A3B8)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { currentScreen = Screen.Vault },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentScreen == Screen.Vault) Color(0xFF38BDF8) else Color(0xFF1F2937)
                )
            ) {
                Text("Vault")
            }
            Button(
                onClick = { currentScreen = Screen.Security },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentScreen == Screen.Security) Color(0xFF5EEAD4) else Color(0xFF1F2937)
                )
            ) {
                Text("Security Info")
            }
        }

        when (currentScreen) {
            Screen.Vault -> VaultContent(
                key = key,
                value = value,
                tag = tag,
                revealValue = revealValue,
                result = result,
                history = history,
                onKeyChange = { key = it },
                onValueChange = { value = it },
                onTagChange = { tag = it },
                onToggleReveal = { revealValue = !revealValue },
                onCopy = {
                    clipboard.setText(AnnotatedString(value))
                    result = "Value copied to clipboard."
                    history.add(0, "${timestamp()} · COPY · $key")
                },
                onSave = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            storage.putString(key, value)
                        }
                        result = "Saved value for '$key'."
                        history.add(0, "${timestamp()} · SAVE · $tag · $key")
                    }
                },
                onLoad = {
                    scope.launch {
                        val stored = withContext(Dispatchers.IO) {
                            storage.getString(key)
                        }
                        result = if (stored == null) {
                            "No value for '$key'."
                        } else {
                            "Loaded: $stored"
                        }
                        if (stored != null) {
                            value = stored
                        }
                        history.add(0, "${timestamp()} · LOAD · $tag · $key")
                    }
                },
                onDelete = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            storage.remove(key)
                        }
                        result = "Removed '$key'."
                        history.add(0, "${timestamp()} · DELETE · $tag · $key")
                    }
                },
                onClear = {
                    scope.launch {
                        withContext(Dispatchers.IO) {
                            storage.clear()
                        }
                        result = "Cleared all entries."
                        history.add(0, "${timestamp()} · CLEAR · vault")
                    }
                },
                onList = {
                    scope.launch {
                        val keys = withContext(Dispatchers.IO) {
                            storage.allKeys().sorted()
                        }
                        result = if (keys.isEmpty()) {
                            "No keys stored."
                        } else {
                            "Keys: ${keys.joinToString(", ") }"
                        }
                        history.add(0, "${timestamp()} · LIST · ${keys.size} keys")
                    }
                },
                onGenerate = {
                    key = "key_${UUID.randomUUID().toString().take(8)}"
                    result = "Generated new key."
                    history.add(0, "${timestamp()} · GENERATE · $key")
                }
            )
            Screen.Security -> SecurityContent()
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (showOnboarding) {
        OnboardingOverlay(
            step = onboardingStep,
            onNext = {
                if (onboardingStep < 2) {
                    onboardingStep += 1
                } else {
                    showOnboarding = false
                }
            },
            onSkip = { showOnboarding = false }
        )
    }
}

private fun timestamp(): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}

private enum class Screen { Vault, Security }

@Composable
private fun VaultContent(
    key: String,
    value: String,
    tag: String,
    revealValue: Boolean,
    result: String,
    history: List<String>,
    onKeyChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onTagChange: (String) -> Unit,
    onToggleReveal: () -> Unit,
    onCopy: () -> Unit,
    onSave: () -> Unit,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onList: () -> Unit,
    onGenerate: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Entry",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )

            TextField(
                value = key,
                onValueChange = onKeyChange,
                label = { Text("Key") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text("Value") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (revealValue) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )

            TextField(
                value = tag,
                onValueChange = onTagChange,
                label = { Text("Tag") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                    Text("Save")
                }
                Button(onClick = onLoad, modifier = Modifier.weight(1f)) {
                    Text("Load")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF93C5FD))
                ) {
                    Text("Delete Key")
                }
                TextButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFFCA5A5))
                ) {
                    Text("Clear All")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onList, modifier = Modifier.weight(1f)) {
                    Text("List Keys")
                }
                Button(onClick = onGenerate, modifier = Modifier.weight(1f)) {
                    Text("Generate Key")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(onClick = onToggleReveal, modifier = Modifier.weight(1f)) {
                    Text(if (revealValue) "Hide Value" else "Show Value")
                }
                TextButton(onClick = onCopy, modifier = Modifier.weight(1f)) {
                    Text("Copy Value")
                }
            }
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )
            Text(
                text = result,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Activity Log",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )
            if (history.isEmpty()) {
                Text(
                    text = "No activity yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF475569)
                )
            } else {
                history.take(8).forEach { entry ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = entry,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color(0xFF1F2937)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityContent() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Security Overview",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "MasterKey is stored in Android Keystore and backed by hardware when available.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Preference keys are encrypted with AES-256 SIV.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Values are encrypted with AES-256 GCM.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Best Practices",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "Use separate storage files for different domains.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Rotate secrets when a user logs out.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Avoid storing large blobs; use encrypted files instead.",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun OnboardingOverlay(
    step: Int,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    val title: String
    val body: String
    val primary: String

    when (step) {
        0 -> {
            title = "Welcome"
            body = "This demo stores secrets using encrypted preferences."
            primary = "Next"
        }
        1 -> {
            title = "Security First"
            body = "Keys and values are encrypted, backed by Android Keystore."
            primary = "Next"
        }
        else -> {
            title = "Organize Your Vault"
            body = "Use tags to group secrets and keep things tidy."
            primary = "Get Started"
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(tween(220)) + slideInVertically(tween(220)) { it / 2 },
        exit = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it / 2 }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onSkip, modifier = Modifier.weight(1f)) {
                        Text("Skip")
                    }
                    Button(onClick = onNext, modifier = Modifier.weight(1f)) {
                        Text(primary)
                    }
                }
            }
        }
    }
}
