package net.store.divineit.models

data class UserAccount(val id: Int?, val email: String?, val type: String?,
                   val role: String?, val fullname: String?, val mobile: String?,
                   val company: String?, val gender: String?, val salesmanid: Int?,
                       val Table: String?, val password: String?, val retypepassword: String?)

data class LoginResponse(val code: Int?, val data: LoginResponseData?, val msg: String?)

data class LoginResponseData(val Account: UserAccount?, val Token: LoginToken?)

data class LoginToken(val AccessToken: String?, val RefreshToken: String?, val AccessUUID: String?,
                 val RefreshUUID: String?, val AtExpires: Long?, val RtExpires: Long?)