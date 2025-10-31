package com.example.myapplication.model.auth


data class RegisterUserInfo(
    val fullName: String,
    val emailAddress: String,
    val password: String
)

data class LoginUserInfo(
    val emailAddress: String,
    val password: String
)

data class FavoriteOperationInfo(
    val artistId: String
)

data class UserData(
    val fullName: String,
    val profileImageUrl: String,
    val userFavorites: Map<String, FavoriteArtist>,
    val tokenExpire: String
)

data class FavoriteArtist(
    val artistId: String,
    val artistName: String,
    val artistBirthday: String,
    val artistDeathday: String,
    val artistNationality: String,
    val artistImageUrl: String,
    val favoritedTime: String?
)