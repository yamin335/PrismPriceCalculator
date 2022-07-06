package net.store.divineit.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.WorkerThread
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import net.store.divineit.di.DefaultPreferenceInfo
import net.store.divineit.models.LoginToken
import net.store.divineit.models.UserAccount
import javax.inject.Inject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AppPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    @DefaultPreferenceInfo private val prefFileName: String
) : PreferencesHelper {

    private val prefs = lazy {
        // Lazy to prevent IO access to main thread.
        context.applicationContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)
    }
    override val preference: SharedPreferences
        get() = context.applicationContext.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override var isLoggedIn by BooleanPreference(prefs, KEY_IS_LOGGED_IN, defaultValue = false, commit = true)

    override var loginToken: LoginToken?
        get() {
            val tokenData = prefs.value.getString(KEY_ACCESS_TOKEN, null)
            return tokenData?.let {
                Gson().fromJson(it, LoginToken::class.java)
            }
        }
        set(value) {
            val tokenData = Gson().toJson(value)
            prefs.value.edit { putString(KEY_ACCESS_TOKEN, tokenData) }
        }

    override var userAccount: UserAccount?
        get() {
            val userAccountData = prefs.value.getString(KEY_USER_ACCOUNT, null)
            return userAccountData?.let {
                Gson().fromJson(it, UserAccount::class.java)
            }
        }
        set(value) {
            val userAccountData = Gson().toJson(value)
            prefs.value.edit { putString(KEY_USER_ACCOUNT, userAccountData) }
        }

    override fun logoutUser() {
        prefs.value.edit {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_USER_ACCOUNT)
        }
    }

    private fun getCurTime(): Long {
        return System.currentTimeMillis()
    }

    override val isAccessTokenExpired: Boolean
        get() {
            loginToken?.AtExpires?.let {
                return getCurTime() > it
            }
            return true
        }

    override fun getAccessTokenHeader(): String {
        return getAuthHeader(loginToken?.AccessToken)
    }

    override fun getAuthHeader(token: String?): String {
        return /*$tokenType */"Bearer $token"
    }


    companion object {
        private const val KEY_IS_LOGGED_IN = "PREF_KEY_LOGIN_STATUS"
        private const val KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
        private const val KEY_USER_ACCOUNT = "PREF_KEY_USER_ACCOUNT"
    }

}

class BooleanPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Boolean,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Boolean> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return preferences.value.getBoolean(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        preferences.value.edit(commit) { putBoolean(name, value) }
    }
}

class StringPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: String?,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, String?> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return preferences.value.getString(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        preferences.value.edit(commit) { putString(name, value) }
    }
}

class IntPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Int,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Int> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return preferences.value.getInt(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        preferences.value.edit(commit) { putInt(name, value) }
    }
}

class LongPreference(
    private val preferences: Lazy<SharedPreferences>,
    private val name: String,
    private val defaultValue: Long,
    private val commit: Boolean = false
) : ReadWriteProperty<Any, Long> {

    @WorkerThread
    override fun getValue(thisRef: Any, property: KProperty<*>): Long {
        return preferences.value.getLong(name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        preferences.value.edit(commit) { putLong(name, value) }
    }
}
