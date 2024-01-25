package com.hgm.androidencryptdecrypt

import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream


/**
 * @author：HGM
 * @created：2024/1/25 0025
 * @description：
 **/
class UserInfoSerializer(
      private val cryptoManager: CryptoManager
) : Serializer<UserInfo> {
      override val defaultValue: UserInfo
            get() = UserInfo()

      override suspend fun readFrom(input: InputStream): UserInfo {
            val decryptedBytes = cryptoManager.decrypt(input)
            return try {
                  Json.decodeFromString(
                        deserializer = UserInfo.serializer(),
                        string = decryptedBytes.decodeToString()
                  )
            } catch (e: SerializationException) {
                  e.printStackTrace()
                  UserInfo()
            }
      }

      override suspend fun writeTo(t: UserInfo, output: OutputStream) {
            cryptoManager.encrypt(
                  bytes = Json.encodeToString(
                        serializer = UserInfo.serializer(),
                        value = t
                  ).encodeToByteArray(),
                  outputStream = output
            )
      }

}