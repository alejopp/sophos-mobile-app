package com.example.sophos_mobile_app.data.repository

import com.example.sophos_mobile_app.data.source.remote.api.ResponseStatus
import com.example.sophos_mobile_app.data.source.remote.api.dto.UserDto
import com.example.sophos_mobile_app.data.model.User

interface UserRepository {
    suspend fun getUserById(email: String, password: String): ResponseStatus<User>
}