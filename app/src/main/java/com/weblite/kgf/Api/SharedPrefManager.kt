package com.weblite.kgf.Api2


import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {

    private const val PREF_NAME = "MyAppSharedPref"
    private lateinit var sharedPref: SharedPreferences

    // Call this once in Application class or first activity
    fun init(context: Context) {
        sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Save data
    fun setString(key: String, value: String) {
        sharedPref.edit().putString(key, value).apply()
    }

    fun setInt(key: String, value: Int) {
        sharedPref.edit().putInt(key, value).apply()
    }

    fun setBoolean(key: String, value: Boolean) {
        sharedPref.edit().putBoolean(key, value).apply()
    }

    // Get data
    fun getString(key: String, default: String? = null): String? {
        return sharedPref.getString(key, default)
    }

    fun getInt(key: String, default: Int = 0): Int {
        return sharedPref.getInt(key, default)
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, default)
    }

    // Remove a key
    fun remove(key: String) {
        sharedPref.edit().remove(key).apply()
    }

    // Clear all
    fun clear() {
        sharedPref.edit().clear().apply()
    }
}
