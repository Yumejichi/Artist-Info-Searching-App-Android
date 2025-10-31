package com.example.myapplication.model.api

import android.content.Context
import com.example.myapplication.model.auth.FavoriteArtist
import com.example.myapplication.model.auth.FavoriteOperationInfo
import com.example.myapplication.model.auth.LoginUserInfo
import com.example.myapplication.model.auth.RegisterUserInfo
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.JsonElement
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


class AuthApiService (context: Context) {
    private val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))
    private val authClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    fun clearCookies() {
        cookieJar.clear()
    }

    val retrofitUseCookies = Retrofit.Builder()
        .baseUrl("https://artist-info-searching-app.wl.r.appspot.com/api/")
        //            .baseUrl("http://10.0.2.2:3000/api/")
        .client(authClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiUseCookie = retrofitUseCookies.create(AuthApi::class.java)

//    val retrofitWithoutCookies = Retrofit.Builder()
//        .baseUrl("https://artist-info-searching-app.wl.r.appspot.com/api/")
//        //            .baseUrl("http://10.0.2.2:3000/api/")
//        .addConverterFactory(GsonConverterFactory.create())
//        .build()
//    private val apiWithoutCookies  = retrofitWithoutCookies.create(AuthApi::class.java)

    private val newClient = authClient.newBuilder()
        .addInterceptor { chain ->
            val req = chain.request().newBuilder()
                .removeHeader("Cookie")
                .build()
            chain.proceed(req)
        }
        .build()

    private val retrofitNew = Retrofit.Builder()
        .baseUrl("https://artist-info-searching-app.wl.r.appspot.com/api/")
        .client(newClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiNew = retrofitNew.create(AuthApi::class.java)

    fun registerUser(registerUserInfo: RegisterUserInfo): Call<JsonElement> {
        return apiNew.register(registerUserInfo)
    }

    fun loginUser(loginUserInfo: LoginUserInfo): Call<JsonElement> {
        return apiNew.login(loginUserInfo)
    }

    fun logout(): Call<JsonElement> {
        return apiUseCookie.logout()
    }

    fun deleteAccount(): Call<JsonElement> {
        return  apiUseCookie.deleteAccount()
    }

    fun checkAuth(): Call<JsonElement> {
        return apiUseCookie.checkAuth()
    }

    fun addFavorite(favoriteOperationInfo: FavoriteOperationInfo): Call<JsonElement> {
        return apiUseCookie.addFavorite(favoriteOperationInfo)
    }


    fun removeFavorite(favoriteOperationInfo: FavoriteOperationInfo): Call<JsonElement> {
        return apiUseCookie.removeFavorite(favoriteOperationInfo)
    }


    fun getFavorites(): Call<List<FavoriteArtist>> =
        apiUseCookie.getFavorites()


    interface AuthApi {
        @GET("user/me")
        fun checkAuth(): Call<JsonElement>


        @POST("user/register")
        fun register(
            @Body registerUserInfo: RegisterUserInfo
        ): Call<JsonElement>

        @POST("user/login")
        fun login(
            @Body loginUserInfo: LoginUserInfo
        ): Call<JsonElement>


        @POST("user/logout")
        fun logout(): Call<JsonElement>


        @POST("user/deleteAccount")
        fun deleteAccount(): Call<JsonElement>


        @POST("artsy/addFavorite")
        fun addFavorite(
            @Body favoriteOperationInfo: FavoriteOperationInfo
        ): Call<JsonElement>

        @POST("artsy/removeFavorite")
        fun removeFavorite(
            @Body favoriteOperationInfo: FavoriteOperationInfo
        ): Call<JsonElement>


        @GET("user/favorites")
        fun getFavorites(): Call<List<FavoriteArtist>>
    }
}


