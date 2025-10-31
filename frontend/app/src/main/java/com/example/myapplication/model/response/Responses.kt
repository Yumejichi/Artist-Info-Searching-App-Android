package com.example.myapplication.model.response

import android.accessibilityservice.GestureDescription
import androidx.navigation.NavDestination

data class SearchArtistNameResponse(
    val artists: List<Artist>
)

data class ArtistArtworks(
    val artworks: List<Artwork>
)

data class CategoryResponse(
    val categories: List<ArtworkCategory>
)

data class SimilarArtistResponse(
    val artists: List<Artist>
)

data class SearchArtistDetailResponse(
    val artistDetail: ArtistDetailInfo
)

data class Artist(
    val artistName: String,
    val imageUrl: String,
    val artistId: String
)


data class ArtistDetailInfo(
    val artistId: String,
    val artistName: String,
    val birthday: String,
    val deathday: String,
    val nationality: String,
    val biography: String
)

data class Artwork(
    val artworkId: String,
    val imageUrl: String,
    val name: String,
    val year: String
)

data class ArtworkCategory(
    val name: String,
    val description: String,
    val imageUrl: String
)

data class RegisterResponse(
    val message: String
)

data class LoginResponse(
    val message: String
)