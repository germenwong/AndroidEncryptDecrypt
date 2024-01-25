package com.hgm.androidencryptdecrypt

import android.content.Context
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import com.hgm.androidencryptdecrypt.ui.theme.AndroidEncryptDecryptTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 *    Android加密和解密的完整使用指南（Keystore）
 **/
class MainActivity : ComponentActivity() {

      private val Context.dataStore by dataStore(
            fileName = "user-info.json",
            serializer = UserInfoSerializer(CryptoManager())
      )

      @OptIn(ExperimentalMaterial3Api::class)
      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                  AndroidEncryptDecryptTheme {
                        var username by remember {
                              mutableStateOf("")
                        }
                        var password by remember {
                              mutableStateOf("")
                        }
                        var info by remember {
                              mutableStateOf("")
                        }
                        val scope = rememberCoroutineScope()


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
                                    value = username,
                                    onValueChange = { username = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(text = "用户名") }
                              )
                              OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = { Text(text = "密码") }
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
                                          scope.launch {
                                                dataStore.updateData {
                                                      UserInfo(
                                                            username = username,
                                                            password = password
                                                      )
                                                }
                                          }
                                    }) {
                                          Text(text = "保存")
                                    }
                                    Button(onClick = {
                                          scope.launch {
                                                info = dataStore.data.first().toString()
                                          }
                                    }) {
                                          Text(text = "读取")
                                    }
                              }

                              Text(text = info)
                        }
                  }
            }
      }
}
