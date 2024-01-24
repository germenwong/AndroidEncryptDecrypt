package com.hgm.androidencryptdecrypt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hgm.androidencryptdecrypt.ui.theme.AndroidEncryptDecryptTheme
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *    Android加密和解密的完整使用指南（Keystore）
 **/
class MainActivity : ComponentActivity() {

      private val cryptoManager by lazy {
            CryptoManager()
      }

      @OptIn(ExperimentalMaterial3Api::class)
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                  AndroidEncryptDecryptTheme {
                        // 明文
                        var plainText by remember {
                              mutableStateOf("")
                        }
                        // 密文
                        var cipherText by remember {
                              mutableStateOf("")
                        }


                        Column(
                              modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                              horizontalAlignment = Alignment.CenterHorizontally,
                              verticalArrangement = Arrangement.spacedBy(
                                    12.dp, Alignment.CenterVertically
                              )
                        ) {
                              OutlinedTextField(
                                    value = plainText,
                                    onValueChange = { plainText = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(text = "输入需要的内容...") }
                              )

                              Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(
                                          12.dp,
                                          Alignment.CenterHorizontally
                                    )
                              ) {
                                    Button(onClick = {
                                          // 把明文转为字节数组
                                          val bytes = plainText.encodeToByteArray()
                                          // 创建文件对象用于存储加密结果
                                          val file = File(filesDir, "secret.txt")
                                          // 检查文件存在性
                                          if (!file.exists()) {
                                                file.createNewFile()
                                          }
                                          // 文件输出流
                                          val fos = FileOutputStream(file)
                                          // 调用加密函数，接收密文
                                          cipherText = cryptoManager.encrypt(bytes, fos).decodeToString()
                                    }) {
                                          Text(text = "加密")
                                    }
                                    Button(onClick = {
                                          // 创建文件对象
                                          val file = File(filesDir, "secret.txt")
                                          // 调用解密函数，接收文件输入流并转换为明文
                                          plainText = cryptoManager.decrypt(FileInputStream(file)).decodeToString()
                                    }) {
                                          Text(text = "解密")
                                    }
                              }

                              Text(text = "加密后：$cipherText")
                        }
                  }
            }
      }
}
