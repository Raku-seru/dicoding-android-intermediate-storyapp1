package com.rakuseru.storyapp1.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// for POST
data class RequestRegister(
    var name: String,
    var email: String,
    var password: String
)

// for POST
@Parcelize
data class RequestLogin(
    var email: String,
    var password: String
) : Parcelable

data class ResponseLogin(
    var error: Boolean,
    var message: String,
    var loginResult: LoginResult
)
data class LoginResult(
    var userId: String,
    var name: String,
    var token: String
)

data class ResponseMsg(
    var error: Boolean,
    var message: String
)

data class ResponseStory(
    var error: String,
    var message: String,
    var listStory: List<ListStory>
)

@Parcelize
data class ListStory(
    var id: String,
    var name: String,
    var description: String,
    var photoUrl: String,
    var createdAt: String,
    var lat: Double,
    var lon: Double
) : Parcelable
