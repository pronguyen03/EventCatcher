package me.linhthengo.androiddddarchitechture.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object EncryptedSharedPreferenceManager {
    private const val TOKEN = "token"
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private lateinit var encryptedSharedPreferences: SharedPreferences

    fun init(context: Context) {
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
        encryptedSharedPreferences.edit().putString(TOKEN, token).apply()
    }

    fun getToken() = encryptedSharedPreferences.getString(TOKEN, "")
}