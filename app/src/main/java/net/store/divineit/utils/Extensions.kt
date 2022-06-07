package net.store.divineit.utils

fun String?.toShortForm(): String {
    var result = "N/A"
    if (this.isNullOrBlank()) return result
    val words = this.split(" ")
    val stringBuilder = StringBuilder()
    for (word in words) {
        if (word.isNotBlank() && word.trim()[0].toString().matches("[A-Za-z]".toRegex()))
            stringBuilder.append(word[0])
    }
    result = stringBuilder.toString()
    return result
}