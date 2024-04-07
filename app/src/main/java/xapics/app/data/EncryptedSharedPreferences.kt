package xapics.app.data

import android.content.Context
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface EncryptedSharedPrefs {
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String?)
}


class EncryptedSharedPrefsImpl @Inject constructor(
    @ApplicationContext private val appContext: Context
) : EncryptedSharedPrefs {

    private val context = appContext

    private var masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()


    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun getString(key: String, defaultValue: String?): String? {
        Log.e("datasource","getting $defaultValue")
        return sharedPreferences.getString(key, defaultValue)
    }

    override fun putString(key: String, value: String?) {
        Log.e("datasource","putting $value")
        sharedPreferences.edit().putString(key, value)
            .apply()
    }

    companion object {
        private const val PREF_NAME = "encrypted_prefs"
    }
}