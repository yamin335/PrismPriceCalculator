package net.store.divineit.models

data class SignUpResponse(val code: Int?, val data: SignUpResponseData?, val msg: String?)

data class SignUpResponseData(val Account: UserAccount?)