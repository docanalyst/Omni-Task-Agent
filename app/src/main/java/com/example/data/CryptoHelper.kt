package com.example.data

import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object CryptoHelper {
    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    
    // In a real production application, these would be retrieved securely from the Android Keystore.
    // For this local prototype, we use a stable secure key to demonstrate functional field-level encryption.
    private val keySpec = SecretKeySpec("3ncrypt10nK3yS3cr3t12345".substring(0, 16).toByteArray(), "AES")
    private val ivSpec = IvParameterSpec("1vP4r4mS3cr3t9988".substring(0, 16).toByteArray())

    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return plainText
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.DEFAULT).trim()
        } catch (e: Exception) {
            plainText
        }
    }

    fun decrypt(encryptedText: String): String {
        if (encryptedText.isEmpty()) return encryptedText
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decodedBytes = Base64.decode(encryptedText, Base64.DEFAULT)
            String(cipher.doFinal(decodedBytes), Charsets.UTF_8).trim()
        } catch (e: Exception) {
            encryptedText
        }
    }
}
