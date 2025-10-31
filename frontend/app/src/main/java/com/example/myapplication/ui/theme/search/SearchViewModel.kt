package com.example.myapplication.ui.theme.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.ArtistRepository
import com.example.myapplication.model.response.SearchArtistNameResponse
import com.example.myapplication.ui.theme.home.SearchIconState
import androidx.compose.runtime.State
import com.example.myapplication.model.response.Artist
import com.example.myapplication.model.response.ArtistArtworks
import com.example.myapplication.model.response.CategoryResponse
import com.example.myapplication.model.response.SearchArtistDetailResponse
import com.example.myapplication.model.response.SimilarArtistResponse
import com.example.myapplication.ui.theme.auth.AuthViewModel
import com.example.myapplication.ui.theme.home.ArtistCardClicked

class SearchViewModel(private val repository: ArtistRepository = ArtistRepository()): ViewModel() {
    private val _searchIconState = mutableStateOf(SearchIconState.CLOSED)
    val searchIconState: State<SearchIconState> = _searchIconState

    private val _artistsResults = mutableStateOf<List<Artist>>(emptyList())
    val artistSearchResults: State<List<Artist>> = _artistsResults

    private val _searchTextState = mutableStateOf<String>(value="")
    val searchTextState: State<String> = _searchTextState


    private val _artistCardClicked = mutableStateOf(ArtistCardClicked.FALSE)
    val artistCardClicked: State<ArtistCardClicked> = _artistCardClicked


    private val _clickedArtistId = mutableStateOf("")
    val clickedArtistId: State<String> = _clickedArtistId



    fun updateSearchIconState(newValue: SearchIconState) {
        _searchIconState.value = newValue
    }
    fun updateSearchTextState(newValue: String) {
        _searchTextState.value = newValue
    }

    fun onSearchIconClicked() {
        _searchIconState.value = SearchIconState.OPENED
    }

    fun onCloseIconClicked() {
        _searchIconState.value = SearchIconState.CLOSED
        _searchTextState.value = ""
    }

    fun onArtistCardClicked(artistId: String) {
        _artistCardClicked.value = ArtistCardClicked.TRUE
        _clickedArtistId.value = artistId
    }


    fun onQueryChange(query: String, successCallback: (SearchArtistNameResponse?) -> Unit) {
        updateSearchTextState(query)
        if (query.length >= 3) {
            repository.getArtistsByName(query, successCallback)
        }
    }



    fun getArtistsByName(name: String, successCallback: (response: SearchArtistNameResponse?) -> Unit) {
        repository.getArtistsByName(name) {response ->
            successCallback(response)
        }
    }

    fun getArtistDetailById(artistId: String, successCallback: (response: SearchArtistDetailResponse?) -> Unit) {
        repository.getArtistDetailById(artistId) {response ->
            successCallback(response)
        }
    }


    fun gatArtistArtworks(artistId: String, successCallback: (response: ArtistArtworks?) -> Unit) {
        repository.getArtworksById(artistId) {response ->
            successCallback(response)
        }
    }

    fun getArtworkCategories(artworkId: String, successCallback: (response: CategoryResponse?) -> Unit) {
        repository.getArtistCategoryById(artworkId) {response ->
            successCallback(response)
        }
    }


    fun getSimilarArtists(artistId: String, successCallback: (response: SimilarArtistResponse?) -> Unit) {
        repository.getSimilarArtistById(artistId) {response ->
            successCallback(response)
        }
    }


//
//    fun onUserAction(userAction: UserAction) {
//        when(userAction) {
//            UserAction.CloseClicked ->
//                state = state.copy(
//                    isBarVisible = false
//                )
//            UserAction.SearchClicked -> state = state.copy(
//                isBarVisible = true, query=""
//            )
//        }
//    }
//
//    fun updateQuery(newQuery: String) {
//        state = state.copy(query = newQuery)
//    }


}

//
//sealed class UserAction {
//    object SearchClicked: UserAction()
//    object CloseClicked: UserAction()
//}


@Composable
fun ArtistList() {

}

