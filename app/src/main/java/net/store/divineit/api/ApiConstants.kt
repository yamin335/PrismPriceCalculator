package net.store.divineit.api

import net.store.divineit.api.Api.API_VERSION
import net.store.divineit.api.Api.AUTH_REPO

object Api {
    private const val PROTOCOL = "https"
    private const val API_ROOT = "prismerpbackend.rtchubs.com"
    const val API_ROOT_URL = "$PROTOCOL://$API_ROOT/"

    const val API_VERSION = "v1"
    const val AUTH_REPO = "auth"

    const val ContentType = "Content-Type: application/json"
}

object ApiEndPoint {
    const val LOGIN = "$API_VERSION/$AUTH_REPO/login"
    const val SIGNUP = "$API_VERSION/$AUTH_REPO/registercustomer"
}

object ResponseCodes {
    const val CODE_SUCCESS = 200
    const val CODE_TOKEN_EXPIRE = 401
    const val CODE_UNAUTHORIZED = 403
}

object ApiCallStatus {
    const val LOADING = 0
    const val SUCCESS = 1
    const val ERROR = 2
    const val EMPTY = 3
}
