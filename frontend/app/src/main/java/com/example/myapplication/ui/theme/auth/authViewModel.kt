package com.example.myapplication.ui.theme.auth

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.Routes
import com.example.myapplication.model.ArtistRepository
import com.example.myapplication.model.AuthRepository
import com.example.myapplication.model.auth.FavoriteOperationInfo
import com.example.myapplication.model.auth.LoginUserInfo
import com.example.myapplication.model.auth.RegisterUserInfo
import com.example.myapplication.model.auth.UserData
import com.example.myapplication.model.response.Artist
import com.example.myapplication.model.response.ArtistArtworks
import com.example.myapplication.model.response.CategoryResponse
import com.example.myapplication.model.response.LoginResponse
import com.example.myapplication.model.response.RegisterResponse
import com.example.myapplication.model.response.SearchArtistDetailResponse
import com.example.myapplication.model.response.SearchArtistNameResponse
import com.example.myapplication.model.response.SimilarArtistResponse
import com.example.myapplication.showSnackbar
import com.example.myapplication.ui.theme.home.ArtistCardClicked
import com.example.myapplication.ui.theme.home.SearchIconState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AuthViewModel(): ViewModel()  {


    private val scope = viewModelScope
    private lateinit var repository: AuthRepository

    private val _currentUser = mutableStateOf<UserData?>(null)
    val currentUser: State<UserData?> = _currentUser

    private val _userFavoritesSet = mutableStateOf<MutableSet<String>>(mutableSetOf())
    val userFavoritesSet: State<MutableSet<String>> = _userFavoritesSet

    private val _isLoggedIn = mutableStateOf(false)
    val isLoggedIn: State<Boolean> get() = _isLoggedIn


    fun init(context: Context) {
        repository = AuthRepository(context)
        checkAuth()
    }
    fun registerUser(userInfo: RegisterUserInfo, successCallback: (RegisterResponse?) -> Unit) {
        repository.registerUser(userInfo) { response ->
            successCallback(response)
        }
    }


    fun loginUser(userInfo: LoginUserInfo, successCallback: (LoginResponse?) -> Unit) {
        repository.login(userInfo) { response ->
            successCallback(response)
        }
    }

//    fun checkAuth() {
//        repository.checkAuth { response ->
//            _currentUser.value = response
//            _isLoggedIn.value = (response != null)
//        }
//    }
    fun checkAuth(onResult: (UserData?) -> Unit = {}) {
        repository.checkAuth { user ->
            _currentUser.value = user
            _isLoggedIn.value = (user != null)
            _userFavoritesSet.value.addAll(_currentUser.value?.userFavorites?.keys.orEmpty())

            onResult(user)

            // after 60 minutes, log out
            user?.tokenExpire?.toLongOrNull()?.let { expiresAt ->
                val delayMs = expiresAt - System.currentTimeMillis()
                if (delayMs > 0) {
                    viewModelScope.launch {
                        delay(delayMs)
                        if (_isLoggedIn.value) {
                            logout(viewModelScope) {}
                        }
                    }
                }
            }

        }

    }

    fun updateFavorites() {
        repository.getFavorites { res ->
            if (res != null) {
                // update the full‐details map
                _currentUser.value = _currentUser.value?.copy(
                    userFavorites = res
                )
                // a
            } else {
                // on error, clear or leave as you prefer
                _currentUser.value = _currentUser.value?.copy(
                    userFavorites = emptyMap()
                )
            }
        }
    }

    fun addFavorite(artistId: String) {
        _userFavoritesSet.value =
            _userFavoritesSet.value.toMutableSet().apply { add(artistId) }

        // need to do:
        // call server to get favorites info
        repository.addFavorite(FavoriteOperationInfo(artistId)) { response ->
            if (response) {

                // update the map here
                updateFavorites()
            }
        }


    }

    fun removeFavorite(artistId: String) {

        _userFavoritesSet.value =
            _userFavoritesSet.value.toMutableSet().apply { remove(artistId) }
        // need to do:
        // call server to get favorites info
        repository.removeFavorite(FavoriteOperationInfo(artistId)) { response ->
            if (response) {
                // update the map here
                updateFavorites()
            }
        }


    }


    fun toggleFavorite(artistId: String) {
        if (_userFavoritesSet.value.contains(artistId)) {
            removeFavorite(artistId)
            showSnackbar(scope, "Removed from favorites")
        } else {
            addFavorite(artistId)

            showSnackbar(scope, "Added to favorites")
        }
    }

    fun logout(scope: CoroutineScope, successCallback: (Boolean) -> Unit) {

        repository.logout { success ->
            if (success) {
                _isLoggedIn.value = false
                _currentUser.value = null
                _userFavoritesSet.value = mutableSetOf()
            }

            showSnackbar(scope, "Logged out successfully")
            successCallback(success)
        }
    }

    fun deleteAccount(scope: CoroutineScope, successCallback: (Boolean) -> Unit) {

        repository.deleteAccount { success ->
            if (success) {
                _isLoggedIn.value = false
                _currentUser.value = null
                _userFavoritesSet.value = mutableSetOf()
            }

            showSnackbar(scope, "Deleted user successfully")
            successCallback(success)
        }
    }



}

