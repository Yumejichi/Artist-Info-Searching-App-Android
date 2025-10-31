package com.example.myapplication.model

import android.content.Context
import com.example.myapplication.model.api.AuthApiService
import com.example.myapplication.model.auth.FavoriteArtist
import com.example.myapplication.model.auth.FavoriteOperationInfo
import com.example.myapplication.model.auth.LoginUserInfo
import com.example.myapplication.model.auth.RegisterUserInfo
import com.example.myapplication.model.auth.UserData
import com.example.myapplication.model.response.LoginResponse
import com.example.myapplication.model.response.RegisterResponse
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepository(context: Context)  {
    private val webService: AuthApiService = AuthApiService(context = context)
    fun registerUser(registerUserInfo: RegisterUserInfo, successCallback: (registerResponse: RegisterResponse?) -> Unit) {
        return webService.registerUser(registerUserInfo).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {

                if (response.isSuccessful) {
                    // extract data
                    val jsonResponseObj = response.body()?.asJsonObject
                    var message = ""
                    message = jsonResponseObj?.get("message")?.asString ?: ""
                    successCallback(RegisterResponse(message = message.toString()))
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Registration Error"
                    successCallback(RegisterResponse(message = errorMessage))
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(null)
            }

        })
    }


    fun login(loginUserInfo: LoginUserInfo, successCallback: (loginResponse: LoginResponse?) -> Unit) {
        return webService.loginUser(loginUserInfo).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {

                if (response.isSuccessful) {
                    // extract data
                    val jsonResponseObj = response.body()?.asJsonObject
                    val message = when {
                        jsonResponseObj?.has("msg") == true -> jsonResponseObj.get("msg")?.asString
                            ?: ""

                        jsonResponseObj?.has("message") == true -> jsonResponseObj.get("message")?.asString
                            ?: ""

                        else -> ""
                    }
                    successCallback(LoginResponse(message = message))
                } else {
                    val errorBodyInfo = response.errorBody()?.string()
                    var errorMessage = "Login Error"

                    if (!errorBodyInfo.isNullOrEmpty()) {
                        val jsonObject =
                            com.google.gson.JsonParser.parseString(errorBodyInfo).asJsonObject
                        errorMessage = jsonObject.get("message")?.asString ?: "Login Error"
                    }

                    successCallback(LoginResponse(message = errorMessage))
                }
            }

                override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                    t.printStackTrace()
                    successCallback(null)
                }

        })
    }




    fun checkAuth(successCallback: (userData: UserData?) -> Unit) {
        return webService.checkAuth().enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {

                if (response.isSuccessful) {
                    // extract data
                    val responseBody = response.body()

                    if (responseBody != null && responseBody.isJsonObject) {
                        val jsonResponseObj = response.body()?.asJsonObject
                        val fullName = jsonResponseObj?.get("fullName")?.asString ?: ""
                        val profileImageUrl =
                            jsonResponseObj?.get("profileImageUrl")?.asString ?: ""
                        val tokenExpiresTime =
                            jsonResponseObj?.get("tokenExpiresTime")?.asString ?: ""

                        val userFavorites = jsonResponseObj?.getAsJsonArray("userFavorites")
                        val favorites = mutableMapOf<String, FavoriteArtist>()

                        if (userFavorites != null) {
                            for (favorite in userFavorites) {
                                val obj = favorite.asJsonObject
                                val artistId = obj.get("artistId").asString
                                val favInfo = FavoriteArtist(
                                    artistId = artistId,
                                    artistName = obj.get("artistName").asString,
                                    artistBirthday = obj.get("artistBirthday").asString,
                                    artistDeathday = obj.get("artistDeathday").asString,
                                    artistNationality = obj.get("artistNationality").asString,
                                    artistImageUrl = obj.get("artistImageUrl").asString,
                                    favoritedTime = obj.get("favoritedTime").asString
                                )
                                favorites[artistId] = favInfo
                            }
                        }
                        successCallback(
                            UserData(
                                fullName = fullName, profileImageUrl = profileImageUrl,
                                userFavorites = favorites, tokenExpire = tokenExpiresTime
                            )
                        )
                    } else {
                    println("check auth response: ${responseBody?.toString()}")
                    successCallback(null)
                }
            } else {
                successCallback(null)
            }
        }


            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(null)
            }

        })
    }

    fun getFavorites(successCallback: (userFavorites: Map<String, FavoriteArtist>?) -> Unit) {
        webService.getFavorites().enqueue(object : Callback<List<FavoriteArtist>>  {
            override fun onResponse(
                call: Call<List<FavoriteArtist>>,
                response: Response<List<FavoriteArtist>>
            ) {
                if (response.isSuccessful) {
                    // extract data
                    println(response.body())
                    val favoriteList: List<FavoriteArtist> = response.body().orEmpty()
                    val favoriteMap = mutableMapOf<String, FavoriteArtist>()
                    for (favorite in favoriteList) {
                        favoriteMap[favorite.artistId] = favorite
                    }
                    successCallback(favoriteMap)
                } else{
                    successCallback(null)
                }
            }
            override fun onFailure(call: Call<List<FavoriteArtist>>, t: Throwable) {
                t.printStackTrace()
                successCallback(null)
            }
        })
    }


    fun logout(successCallback: (Boolean) -> Unit) {
        webService.logout().enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    webService.clearCookies()
                    successCallback(true)
                } else {
                    successCallback(false)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(false)
            }
        })
    }


    fun deleteAccount(successCallback: (Boolean) -> Unit) {
        webService.deleteAccount().enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    webService.clearCookies()
                    successCallback(true)
                } else {
                    successCallback(false)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(false)
            }
        })
    }

    fun addFavorite(favoriteOperationInfo: FavoriteOperationInfo,
                            successCallback: (Boolean) -> Unit) {
        webService.addFavorite(favoriteOperationInfo).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {

                if (response.isSuccessful) {
                    successCallback(true)
                } else {
                    successCallback(false)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(false)
            }

        })
    }

    fun removeFavorite(favoriteOperationInfo: FavoriteOperationInfo,
                            successCallback: (Boolean) -> Unit) {
        return webService.removeFavorite(favoriteOperationInfo).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {

                if (response.isSuccessful) {
                    successCallback(true)
                } else {
                    successCallback(false)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(false)
            }

        })
    }

}
