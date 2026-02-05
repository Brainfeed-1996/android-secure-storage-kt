# android-secure-storage-kt

A modern Kotlin wrapper around `EncryptedSharedPreferences`, with a Compose demo app.

## Modules
- `secure-storage`: Lightweight, testable API for encrypted key/value storage.
- `app`: Demo app showing the API in action.

## Requirements
- `minSdk 23`
- AndroidX Security Crypto
- Kotlin 1.9+

## Quick start (library)
```kotlin
val storage = SecureStorageFactory.create(context)

storage.putString("token", "abc123")
val token = storage.getString("token")

storage.putBoolean("is_pro", true)
val isPro = storage.getBoolean("is_pro")

storage.putBytes("avatar", byteArrayOf(1, 2, 3))
val avatar = storage.getBytes("avatar")
```

## Demo app
The demo app lets you save, load, delete, and list keys using the same API.

## Tests
- JVM unit tests: `:secure-storage:test`
- Instrumented tests: `:secure-storage:connectedAndroidTest`

## CI
GitHub Actions runs unit tests and assembles the demo app on each push and PR.

## Notes
- Values are encrypted using a `MasterKey` stored in the Android Keystore.
- Keys are also encrypted (AES256_SIV) to protect metadata.
- Consider rotating keys and using separate storage files for different domains.
