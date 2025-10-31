package com.example.myapplication.model

import com.example.myapplication.R
import com.example.myapplication.model.api.ArtistApiService
import com.example.myapplication.model.response.Artist
import com.example.myapplication.model.response.ArtistArtworks
import com.example.myapplication.model.response.ArtistDetailInfo
import com.example.myapplication.model.response.Artwork
import com.example.myapplication.model.response.ArtworkCategory
import com.example.myapplication.model.response.CategoryResponse
import com.example.myapplication.model.response.SearchArtistDetailResponse
import com.example.myapplication.model.response.SearchArtistNameResponse
import com.example.myapplication.model.response.SimilarArtistResponse
import com.google.gson.JsonElement
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.jvm.Throws
import kotlin.text.Regex
import kotlin.text.substringAfter

class ArtistRepository(private val webService: ArtistApiService = ArtistApiService()) {
    private val linkPatternForCategoryPart = Regex("""\[(.*?)\]\((.*?)\)""")
    fun getArtistsByName(name: String, successCallback: (response: SearchArtistNameResponse?) -> Unit) {
        return webService.getArtistsByName(name).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                println("Artist fetched: $name")

                if (response.isSuccessful) {
                    // extract data
                    val jsonResponseArray = response.body()?.asJsonArray
                    val artistsResult = mutableListOf<Artist>()

                    if (jsonResponseArray != null) {
                        for (artistInfo in jsonResponseArray) {
                            val artist = artistInfo.asJsonObject
//                            var imageUrl:Any =  R.drawable.artsy_logo
                            val artistName = artist["title"].asString
                            val commonUrlBeforeId = "https://api.artsy.net/api/artists/"
                            val _linksObj = artist["_links"]?.asJsonObject
                            val thumbnailObj = _linksObj?.getAsJsonObject("thumbnail")
                            val imageUrlFromRes = thumbnailObj?.get("href")?.asString
                            val hrefString = _linksObj?.get("self")?.asJsonObject?.get("href")?.asString

                            var imageUrl = ""
                            if (imageUrlFromRes != null) {
                                imageUrl = imageUrlFromRes
                            }
                            var artistId = ""
                            if (hrefString != null) {
                                artistId = hrefString.substringAfter(commonUrlBeforeId)
                            }
                            artistsResult.add(
                                Artist(
                                    artistName = artistName,
                                    imageUrl = imageUrl,
                                    artistId = artistId
                                )
                            )
                        }
                    }
                    successCallback(SearchArtistNameResponse(artists = artistsResult))
                }

                else {
                    successCallback(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()
                successCallback(null)
            }

        })
    }


    fun getArtistDetailById(artistId: String, successCallback: (response: SearchArtistDetailResponse?) -> Unit) {
        return webService.getArtistDetailById(artistId).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                println("Artist fetched: $artistId")

                if (response.isSuccessful) {
                    // extract data
                    val jsonResponseObj = response.body()?.asJsonObject
                    val artistName = jsonResponseObj?.get("name")?.asString
                    val birthday = jsonResponseObj?.get("birthday")?.asString
                    val deathday = jsonResponseObj?.get("deathday")?.asString
                    val nationality = jsonResponseObj?.get("nationality")?.asString
                    val biography = jsonResponseObj?.get("biography")?.asString
                        .orEmpty()
                        .replace("\u0096", "–")
                        .replace(Regex("-\\s(?=\\w)"), "")
                        .replace(Regex("\\.(\\r?\\n)(?=[A-Z])"), ".\n\n")
                        .split(Regex("\\r?\\n\\r?\\n"))
                        .joinToString("\n\n") { para ->
                            para.replace(Regex("\\s+"), " ").trim()
                        }

                    val detailResult = ArtistDetailInfo(
                        artistName = artistName ?: "",
                        artistId = artistId,
                        birthday = birthday ?: "",
                        deathday = deathday ?: "",
                        nationality = nationality ?: "",
                        biography = biography
                    )
                    successCallback(SearchArtistDetailResponse(artistDetail = detailResult))
                } else {
                    successCallback(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()//log error
                successCallback(null)
            }

        })
    }


    fun getArtworksById(id: String, successCallback: (response: ArtistArtworks?) -> Unit) {
        return webService.getArtworks(id).enqueue(object: Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                val artworksResult = mutableListOf<Artwork>()

                if (response.isSuccessful) {
                    // artworks: response._embedded.artworks
                    val jsonResponseObj = response.body()?.asJsonObject
                    val _embedded = jsonResponseObj?.get("_embedded")?.asJsonObject
                    val artworks = _embedded?.get("artworks")?.asJsonArray

                    if (artworks != null) {
                        for (artwork in artworks) {
                            var artworkJsonObj = artwork.asJsonObject
                            val artworkId = artworkJsonObj["id"].asString
                            val name = artworkJsonObj["title"].asString
                            val year = artworkJsonObj["date"].asString
                            val _linksObj = artworkJsonObj["_links"]?.asJsonObject
                            val thumbnailObj = _linksObj?.getAsJsonObject("thumbnail")
                            val imageUrlFromRes = thumbnailObj?.get("href")?.asString

                            var imageUrl = ""
                            if (imageUrlFromRes != null) {
                                imageUrl = imageUrlFromRes
                            }
                            artworksResult.add(
                                Artwork(
                                    artworkId = artworkId,
                                    imageUrl = imageUrl,
                                    name = name,
                                    year = year
                                )
                            )
                        }
                    }
                    successCallback(ArtistArtworks(artworks = artworksResult))
                } else {
                    successCallback(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()//log error
                successCallback(null)
            }
        })
    }

    fun getArtistCategoryById(id: String, successCallback: (response: CategoryResponse?) -> Unit) {
        return webService.getArtworkCategory(id).enqueue(object: Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                val categoryResult = mutableListOf<ArtworkCategory>()

                if (response.isSuccessful) {
                    // categories: response._embedded.genes
                    val jsonResponseObj = response.body()?.asJsonObject
                    val _embedded = jsonResponseObj?.get("_embedded")?.asJsonObject
                    val categories = _embedded?.get("genes")?.asJsonArray

                    if (categories != null) {
                        for (category in categories) {
                            var categoryJsonObj = category.asJsonObject
                            val name = categoryJsonObj["name"].asString
                            val descriptionOriginal = categoryJsonObj["description"].asString
                            // deal with the Latex in description here:
                            val baseUrl = "https://www.artsy.net/"
                            val description = descriptionOriginal.replace(linkPatternForCategoryPart)  { m ->
                                val firstPart   = m.groupValues[1]
                                val linkInfo    = m.groupValues[2]
                                val newUrl = baseUrl.trimEnd('/') + linkInfo
                                "[$firstPart]($newUrl)"
                            }

                            val additionalStuff = Regex("_(.*?)_")
                            val finalDescription = description.replace(additionalStuff) { it.groupValues[1] }

                            val _linksObj = categoryJsonObj["_links"]?.asJsonObject
                            val thumbnailObj = _linksObj?.getAsJsonObject("thumbnail")
                            val imageUrlFromRes = thumbnailObj?.get("href")?.asString
                            var imageUrl = ""
                            if (imageUrlFromRes != null) {
                                imageUrl = imageUrlFromRes
                            }
                            categoryResult.add(
                                ArtworkCategory(
                                    imageUrl = imageUrl,
                                    name = name,
                                    description = finalDescription
                                )
                            )
                        }
                    }
                    successCallback(CategoryResponse(categories = categoryResult))
                } else {
                    successCallback(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()//log error
                successCallback(null)
            }
        })

    }

    fun getSimilarArtistById(id: String, successCallback: (response: SimilarArtistResponse?) -> Unit) {
        return webService.getSimilarArtists(id).enqueue(object : Callback<JsonElement> {
            override fun onResponse(
                call: Call<JsonElement>,
                response: Response<JsonElement>
            ) {
                if (response.isSuccessful) {
                    // extract data
                    val jsonResponseArray = response.body()?.asJsonArray
                    val artistsResult = mutableListOf<Artist>()

                    if (jsonResponseArray != null) {
                        for (artistInfo in jsonResponseArray) {
                            val artist = artistInfo.asJsonObject
                            val artistName = artist["name"].asString
                            var artistId = artist["id"].asString
                            val _linksObj = artist["_links"]?.asJsonObject
                            val thumbnailObj = _linksObj?.getAsJsonObject("thumbnail")
                            val imageUrlFromRes = thumbnailObj?.get("href")?.asString

                            var imageUrl = ""
                            if (imageUrlFromRes != null) {
                                imageUrl = imageUrlFromRes
                            }
                            artistsResult.add(
                                Artist(
                                    artistName = artistName,
                                    imageUrl = imageUrl,
                                    artistId = artistId
                                )
                            )
                        }
                    }
                    successCallback(SimilarArtistResponse(artists = artistsResult))
                }

                else {
                    successCallback(null)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                t.printStackTrace()//log error
                successCallback(null)
            }

        })
    }
}