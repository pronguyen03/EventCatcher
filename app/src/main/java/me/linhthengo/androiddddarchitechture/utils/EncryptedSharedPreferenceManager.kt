package me.linhthengo.androiddddarchitechture.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object EncryptedSharedPreferenceManager {
    private const val TOKEN_KEY = "token"
    private var token: String? = null

    private lateinit var encryptedSharedPreferences: SharedPreferences

    fun init(context: Context) {
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
        encryptedSharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun hasToken() = token.isNullOrBlank()

    fun getToken() = token ?: encryptedSharedPreferences.getString(TOKEN_KEY, "")?.also {
        token = it
    }
}