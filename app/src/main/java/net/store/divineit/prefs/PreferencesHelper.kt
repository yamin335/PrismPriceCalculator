package net.store.divineit.prefs

import android.content.SharedPreferences
import net.store.divineit.models.LoginToken
import net.store.divineit.models.UserAccount


interface PreferencesHelper {

    val preference: SharedPreferences

    var isLoggedIn: Boolean

    var loginToken: LoginToken?

    var userAccount: UserAccount?

    val isAccessTokenExpired: Boolean

    fun getAccessTokenHeader(): String

    fun getAuthHeader(token: String?): String

    fun logoutUser()
}
