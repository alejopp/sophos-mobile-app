package com.example.sophos_mobile_app.data.api.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: String?,
    val nombre: String?,
    val apellido: String?,
    val acceso: Boolean?,
    val admin: Boolean?,
)
