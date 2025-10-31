package com.example.myapplication.model.api
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


class ArtistApiService() {
    private val api: ArtistApi


    init {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://artist-info-searching-app.wl.r.appspot.com/api/")
//            .baseUrl("http://10.0.2.2:3000/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ArtistApi::class.java)
    }

    fun getArtistsByName(name: String): Call<JsonElement> {
        return api.getArtistsByName(name)
    }

    fun getArtistDetailById(artistId: String): Call<JsonElement> {
        return api.getArtistDetailById(artistId)
    }

    fun getArtworks(artistId: String): Call<JsonElement> {
        return api.getArtworksById(artistId)
    }

    fun getArtworkCategory(artworkId: String): Call<JsonElement> {
        return api.getArtistCategoryById(artworkId)
    }

    fun getSimilarArtists(artistId: String): Call<JsonElement> {

        return api.getSimilarArtistById(artistId)
    }



    interface ArtistApi {
        @GET("artsy/search/{name}")
        fun getArtistsByName(@Path("name") name: String): Call<JsonElement>


        @GET("artsy/details/{artistId}")
        fun getArtistDetailById(@Path("artistId") artistId: String): Call<JsonElement>

        @GET("artsy/artworks/{artistId}")
        fun getArtworksById(@Path("artistId") artistId: String): Call<JsonElement>

        @GET("artsy/category/{artworkId}")
        fun getArtistCategoryById(@Path("artworkId") artistId: String): Call<JsonElement>

        @GET("artsy/similar/{artistId}")
        fun getSimilarArtistById(@Path("artistId") artistId: String): Call<JsonElement>
    }
}
