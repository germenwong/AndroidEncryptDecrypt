package com.hgm.androidencryptdecrypt

import kotlinx.serialization.Serializable

/**
 * @author：HGM
 * @created：2024/1/25 0025
 * @description：
 **/
@Serializable
data class UserInfo(
      val username: String?=null,
      val password: String?=null
)
