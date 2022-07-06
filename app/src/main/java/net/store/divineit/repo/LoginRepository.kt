package net.store.divineit.repo

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.store.divineit.api.ApiService
import net.store.divineit.models.LoginResponse
import net.store.divineit.models.SignUpResponse
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(
    private val apiService: ApiService
    ) {

    suspend fun login(email: String?, password: String?): Response<LoginResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("email", email)
            addProperty("password", password)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.login(jsonObject)
        }
    }

    suspend fun signUp(username: String?, password: String?, email: String?, usertype: Int?,
                      firstname: String?, lastname: String?, company: String?, mobile: String?,
                      retypepassword: String?, fullname: String?, type: String?, role: String?): Response<SignUpResponse> {
        val jsonObject = JsonObject().apply {
            addProperty("username", username)
            addProperty("password", password)
            addProperty("email", email)
            addProperty("usertype", usertype)
            addProperty("firstname", firstname)
            addProperty("lastname", lastname)
            addProperty("company", company)
            addProperty("mobile", mobile)
            addProperty("retypepassword", retypepassword)
            addProperty("fullname", fullname)
            addProperty("type", type)
            addProperty("role", role)
        }.toString()

        return withContext(Dispatchers.IO) {
            apiService.signUp(jsonObject)
        }
    }
}