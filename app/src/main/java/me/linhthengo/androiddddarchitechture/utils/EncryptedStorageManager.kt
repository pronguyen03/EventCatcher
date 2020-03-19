package me.linhthengo.androiddddarchitechture.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class EncryptedStorageManager(context: Context) {
    private val tokenKey = "token"
    private var token: String? = null

    private var encryptedSharedPreferences: SharedPreferences

    init {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        encryptedSharedPreferences = EncryptedSharedPreferences
            .create(
                "secure_pref",
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
    }

    fun saveToken(token: String) {
        encryptedSharedPreferences.edit().putString(tokenKey, token).apply()
    }

    fun hasToken() = token.isNullOrBlank()

    fun getToken() = token ?: encryptedSharedPreferences.getString(tokenKey, "")?.also {
        token = it
    }
}