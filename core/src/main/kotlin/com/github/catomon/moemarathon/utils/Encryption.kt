package com.github.catomon.moemarathon.utils

fun encryptData(text: String, secretKey: String): String {
    return text
}

fun decryptData(encryptedString: String, secretKey: String): String {
    return encryptedString
}

fun generateSecretKey(password: String, salt: ByteArray? = ByteArray(1) { 1 }): String {
    return "secret-key-sample"
}
