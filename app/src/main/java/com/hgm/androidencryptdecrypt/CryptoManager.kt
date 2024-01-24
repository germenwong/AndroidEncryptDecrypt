package com.hgm.androidencryptdecrypt

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 * @author：HGM
 * @created：2024/1/23 0023
 * @description：加密管理器
 **/
class CryptoManager {

      /**  密钥库实例  **/
      private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
      }

      /**  定义用于加密的密码  **/
      private fun encryptCipher() = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getKey())
      }

      /**  定义用于解密的密码  **/
      private fun decryptCipherForIv(iv: ByteArray) = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
      }

      /**  获取密钥库的key  **/
      private fun getKey(): SecretKey {
            // 先检查密钥库中是否存在key，如果没有则创建一个
            val existingKey = keyStore.getEntry("secret", null) as? KeyStore.SecretKeyEntry
            return existingKey?.secretKey ?: createKey()
      }

      /**  创建密钥库的key  **/
      private fun createKey(): SecretKey {
            return KeyGenerator.getInstance(ALGORITHM).apply {
                  init(
                        KeyGenParameterSpec.Builder(
                              "secret",
                              KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                        )
                              .setBlockModes(BLOCK_MODE)
                              .setEncryptionPaddings(PADDING)
                              .setUserAuthenticationRequired(false)
                              .setRandomizedEncryptionRequired(true)
                              .build()
                  )
            }.generateKey()
      }


      /**  加密  **/
      fun encrypt(bytes: ByteArray, outputStream: OutputStream): ByteArray {
            // 每次加密都要都需要生成新的Initial Vector，否则报错（IV 已被使用。在加密模式下重复使用 IV 违反了安全最佳实践）
            val cipher = encryptCipher()

            // 加密后的字节数据
            val encryptedBytes = cipher.doFinal(bytes)
            outputStream.use {
                  // 写入加密时iv的信息
                  it.write(cipher.iv.size)
                  it.write(cipher.iv)
                  // 写入加密字节的信息
                  it.write(encryptedBytes.size)
                  it.write(encryptedBytes)
            }

            return encryptedBytes
      }

      /**  解密  **/
      fun decrypt(inputStream: InputStream): ByteArray {
            return inputStream.use {
                  // 接收加密时的iv信息（InitialValue）
                  val ivSize = it.read()
                  val iv = ByteArray(ivSize)
                  it.read(iv)

                  // 接收加密字节的信息
                  val encryptedBytesSize = it.read()
                  val encryptedBytes = ByteArray(encryptedBytesSize)
                  it.read(encryptedBytes)

                  decryptCipherForIv(iv).doFinal(encryptedBytes)
            }
      }

      companion object {
            // 加密算法
            private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

            // 块模式
            private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_CBC

            // 填充
            private const val PADDING = KeyProperties.ENCRYPTION_PADDING_PKCS7

            // 转换
            private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"
      }
}