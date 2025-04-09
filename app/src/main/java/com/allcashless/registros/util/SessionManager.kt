package com.allcashless.registros.util

import android.content.Context
import android.content.SharedPreferences

object SessionManager {

    private lateinit var prefs: SharedPreferences
    private const val PREF_NAME = "AllcashlessPrefs"
    private const val KEY_CC = "cc"
    private const val KEY_NOMBRE = "nombre"
    private const val KEY_TOKEN = "token"

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthData(cc: String, nombre: String, token: String) {
        prefs.edit().apply {
            putString(KEY_CC, cc)
            putString(KEY_NOMBRE, nombre)
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun getUserCc(): String = prefs.getString(KEY_CC, "") ?: ""
    fun getUserNombre(): String = prefs.getString(KEY_NOMBRE, "") ?: ""
    fun getToken(): String = prefs.getString(KEY_TOKEN, "") ?: ""

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = getToken().isNotEmpty()
}