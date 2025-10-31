package com.example.myapplication.ui.theme.search

import android.R.attr.onClick
import android.R.attr.shape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.myapplication.R
import com.example.myapplication.model.ArtistRepository
import com.example.myapplication.model.response.Artist
import com.example.myapplication.model.response.ArtistDetailInfo
import com.example.myapplication.model.response.Artwork
import com.example.myapplication.model.response.ArtworkCategory
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.auth.AuthViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.contains
import kotlin.collections.mutableSetOf
import kotlin.collections.orEmpty

@Composable
fun SearchScreen(isLoggedIn: Boolean, viewModel: SearchViewModel, authViewModel: AuthViewModel, query: String, onCardClick: (String) -> Unit) {
    val rememberedArtists: MutableState<List<Artist>> = remember { mutableStateOf(emptyList()) }
    val scrollState = rememberLazyListState()
    var name = query
    val noResults = remember { mutableStateOf(false) }
    val userData = authViewModel.currentUser
//    val userFavorites = userData.value?.userFavorite
//    val userFavoriteSet = authViewModel.userFavoritesSet
//    val userFavorites = remember { mutableStateOf(authViewModel.userFavoritesSet.value) }


    LaunchedEffect(name) {
        if (query.length >= 3) {
            viewModel.getArtistsByName(name) { response ->
                response?.let {
                    rememberedArtists.value = it.artists.orEmpty()
                    noResults.value = rememberedArtists.value.isEmpty()
                } ?: run {
                    noResults.value = true
                }
            }
        } else {
            rememberedArtists.value = emptyList()
            noResults.value = false
        }
    }
    if (query.length >= 3 && noResults.value) {

        Spacer(modifier = Modifier.padding(7.dp))
        Surface(
            modifier = Modifier.fillMaxWidth().padding(10.dp).height(50.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(10.dp)
        )
        {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "No Result Found",
                    textAlign = TextAlign.Center,
                )

            }

        }
    } else {
        LazyColumn(
            state = scrollState,
            modifier = Modifier.fillMaxSize().padding(bottom = 50.dp).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(all = 8.dp)
        ) {

            items(rememberedArtists.value) { artist ->
                Card(
                    modifier = Modifier.padding(8.dp).fillMaxWidth().height(200.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        if (artist.imageUrl.contains("missing_image")) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.artsy_logo),
                                    contentDescription = "artsy logo",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .aspectRatio(1f),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        } else {
                            // Show remote image
                            AsyncImage(
                                model = artist.imageUrl,
                                contentDescription = "artist image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // add star button for logged in user
                        if (isLoggedIn) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd) // Align to top end of the card
                                    .padding(10.dp)) {
                                IconButton(
                                    onClick = {
                                        authViewModel.toggleFavorite(artist.artistId)
                                    },
                                    Modifier.size(50.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (authViewModel.userFavoritesSet.value.contains(artist.artistId) == true) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "favorite or not",
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.align(Alignment.BottomStart)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f))
                                .fillMaxWidth()
                                .height(45.dp).padding(5.dp).clickable {
                                    onCardClick(artist.artistId.toString())
                                },
                        ) {
                            Text(
                                text = artist.artistName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )

                            Icon(
                                modifier = Modifier.padding(end = 0.dp).size(25.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Image"
                            )
                        }
                    }

                }
            }
        }
    }
}

//@Composable
//@Composable
//fun onCardClick(artist: Unit) {
//    val artistId = artist.artistId
//
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(authViewModel: AuthViewModel, isLoggedIn: Boolean, artistId: String, navController: NavController) {

    val viewModel: SearchViewModel = viewModel()
    val rememberedArtistDetail = remember { mutableStateOf<ArtistDetailInfo?>(null) }
    val isLoadingArtistDetails = remember { mutableStateOf(true) }
    LaunchedEffect(artistId) {

        viewModel.getArtistDetailById(artistId) { response ->

            println("Fetched artist detail: $response")
            rememberedArtistDetail.value = response?.artistDetail ?: ArtistDetailInfo(
                artistName = "",
                artistId = artistId,
                birthday = "",
                deathday = "",
                nationality = "",
                biography = ""
            )
            isLoadingArtistDetails.value = false
        }
    }

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(rememberedArtistDetail.value?.artistName ?: "") },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                        },

                        // add star button for logged in user

                        actions = {
                            if (isLoggedIn) {
                                IconButton(
                                    onClick = {
                                        authViewModel.toggleFavorite(rememberedArtistDetail.value?.artistId.toString())
                                    },
                                    modifier = Modifier.padding(end = 10.dp)
                                ) {
                                    Icon(
                                        imageVector = if (authViewModel.userFavoritesSet.value.contains(
                                                rememberedArtistDetail.value?.artistId.toString()
                                            ) == true
                                        ) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "favorite or not",
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                            }
                        },




                                colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                    )
                }

            ) { innerPadding -> Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding)
                ) {
                    val tabs = mutableListOf(
                        ImageTabItem(
                            text = "Details",
                            icon = Icons.Outlined.Info
                        ),
                        ImageTabItem(
                            text = "Artworks",
                            icon = Icons.Outlined.AccountBox,
                        )
                    )
                if (isLoggedIn) {
                    tabs.add(
                        ImageTabItem(
                            text = "Similar",
                            icon = Icons.Outlined.PersonSearch
                        )
                    )
                }
                    val tabIndex = remember { mutableIntStateOf(0) }


                TabRow(
                selectedTabIndex = tabIndex.value,
                modifier = Modifier.fillMaxWidth(),
                containerColor = TabRowDefaults.primaryContainerColor,
                contentColor = TabRowDefaults.primaryContentColor
                    ) {
                    tabs.forEachIndexed { index, item ->
                        Tab(
                            selected = index == tabIndex.value,
                            onClick = {
                                tabIndex.value = index
                            },
                            text = {
                                Text(text = item.text)
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.text
                                )
                            }
                        )
                    }
                }

                    if(isLoadingArtistDetails.value ) {
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentAlignment = Alignment.TopCenter) {
                            Column(
                                modifier = Modifier.padding(top = 30.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,) {
                                CircularProgressIndicator(
                                    color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(5.dp))
                                Text("Loading...")
                            }

                        }

                    } else {
                    when (tabIndex.value) {
                        0 -> ArtistInfoScreen(isLoggedIn, rememberedArtistDetail)
                        1 -> ArtistArtworksScreen(
                            rememberedArtistDetail.value?.artistId ?: "",
                            viewModel
                        )

                        2 -> if (isLoggedIn) {
                            SimilarArtistsScreen(authViewModel,
                                rememberedArtistDetail.value?.artistId ?: "",
                                onCardClick = { artistId ->
                                    navController.navigate("artist_detail_screen/$artistId")
                                },
                                viewModel
                            )
                        }
                    }
                }

            }
        }
    }

@Composable
fun ArtistInfoScreen(isLoggedIn: Boolean, rememberedArtistDetail: MutableState<ArtistDetailInfo?>) {
    val detailInfo = rememberedArtistDetail.value ?: return

    val nationality = detailInfo.nationality.orEmpty().trim()
    val birthday  = detailInfo.birthday.orEmpty().trim()
    val deathday  = detailInfo.deathday.orEmpty().trim()

    // build the display line
    val artistDetail = buildString {
        if (nationality.isNotEmpty()) append(nationality)
        if (nationality.isNotEmpty() && (birthday.isNotEmpty() || deathday.isNotEmpty())) append(", ")
        if (birthday.isNotEmpty()) append(birthday)
        if ((birthday.isNotEmpty() || deathday.isNotEmpty())) append(" - ")
        if (deathday.isNotEmpty()) append(deathday)
    }.trimEnd(' ', '-',' ','\u00A0')


    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally )  {
            Spacer(modifier = Modifier.padding(7.dp))
            Text(text = detailInfo.artistName,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp)

        if (artistDetail.isNotBlank()) {
            Text(
                text = artistDetail,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }

            Spacer(modifier = Modifier.padding(7.dp))

            Text(text = detailInfo.biography,
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                textAlign = TextAlign.Justify)
        }
    }

@Composable
fun SimilarArtistsScreen(authViewModel: AuthViewModel, artistId: String, onCardClick: (String) -> Unit, viewModel: SearchViewModel) {
    val scrollState = rememberLazyListState()

    val rememberedSimilarArtists: MutableState<List<Artist>> = remember { mutableStateOf(emptyList()) }
    val isLoadingSimilarArtists = remember { mutableStateOf(true) }
    LaunchedEffect(artistId) {
        // get artworks
        viewModel.getSimilarArtists(artistId) { response ->
            val res = response?.artists
            rememberedSimilarArtists.value = res.orEmpty()
            isLoadingSimilarArtists.value = false
        }
    }

    when {
        isLoadingSimilarArtists.value -> {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)
                .padding(top = 20.dp),  ) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(

                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Loading...")
                }
            }
        }

        else ->
            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 8.dp)
            ) {
                items(rememberedSimilarArtists.value) { artist ->
                    Card(
                        modifier = Modifier.padding(8.dp).fillMaxWidth().height(200.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (artist.imageUrl.contains("missing_image")) {
                                // Show a smaller logo centered in the card
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.artsy_logo),
                                        contentDescription = "artsy logo",
                                        modifier = Modifier
                                            .height(200.dp)
                                            .aspectRatio(1f),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            } else {
                                // Show remote image
                                AsyncImage(
                                    model = artist.imageUrl,
                                    contentDescription = "artist image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            // add favorite button when logged in
                            // add star button for logged in user
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd) // Align to top end of the card
                                    .padding(10.dp)) {
                                IconButton(
                                    onClick = {
                                        authViewModel.toggleFavorite(artist.artistId)
                                    },
                                    Modifier.size(50.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = if (authViewModel.userFavoritesSet.value.contains(artist.artistId) == true) Icons.Filled.Star else Icons.Filled.StarBorder,
                                        contentDescription = "favorite or not",
                                        modifier = Modifier.size(30.dp)

                                    )
                                }
                            }


                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.align(Alignment.BottomStart).background(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.85f)
                                ).fillMaxWidth()
                                    .height(45.dp).clickable {
                                        onCardClick(artist.artistId.toString())
                                    }.padding(5.dp),
                            ) {
                                Text(
                                    text = artist.artistName,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    modifier = Modifier.padding(end = 0.dp).size(25.dp),
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = "Next Image"
                                )
                            }
                        }

                    }
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistArtworksScreen(
    artistId: String,
    viewModel: SearchViewModel
) {

    val rememberedArtworks: MutableState<List<Artwork>> = remember { mutableStateOf(emptyList()) }
    val isLoadingArtistArtworks = remember { mutableStateOf(true) }

    LaunchedEffect(artistId) {
        // get artworks
        viewModel.gatArtistArtworks(artistId) {response ->
            val res = response?.artworks
            rememberedArtworks.value = res.orEmpty()
            isLoadingArtistArtworks.value = false
        }
    }

    val scrollState = rememberLazyListState()
    val openCategoryDialog = remember{mutableStateOf(false)}
    val selectedArtworkId = remember { mutableStateOf<String?>(null) }
    val artworkCategories = remember { mutableStateOf<List<ArtworkCategory>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }

    when {
        isLoadingArtistArtworks.value -> {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text("Loading...")
                }
            }
        }
        else ->
            if (rememberedArtworks.value.isEmpty()) {
                Spacer(modifier = Modifier.padding(7.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(10.dp).height(50.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(10.dp)
                )
                {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "No Artworks",
                            textAlign = TextAlign.Center,
                        )

                    }

                }
            } else {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier.fillMaxSize().fillMaxWidth()
                        .background(color = Color.Transparent),
                    contentPadding = PaddingValues(all = 8.dp)
                ) {
                    items(rememberedArtworks.value) { artwork ->
                        Card(
                            modifier = Modifier.padding(8.dp).fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 2.dp
                            )
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (artwork.imageUrl.contains("missing_image") || artwork.imageUrl == "") {
                                    // Show a smaller logo centered in the card
                                    Image(
                                        painter = painterResource(id = R.drawable.artsy_logo),
                                        contentDescription = "artsy logo",
                                        contentScale = ContentScale.Fit
                                    )
                                } else {
                                    // Show remote image
                                    AsyncImage(
                                        model = artwork.imageUrl,
                                        contentDescription = "artist image",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.padding(7.dp))
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center,
                                        text = artwork.name + ", " + artwork.year,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        //                            modifier = Modifier.padding(start = 10.dp),
                                    )
                                    Spacer(modifier = Modifier.padding(7.dp))
                                    Button(
                                        onClick = {
                                            openCategoryDialog.value = true
                                            //selectedArtwork
                                            selectedArtworkId.value = artwork.artworkId
                                            isLoading.value = true
//                                viewModel.getArtworkCategories(artwork.artworkId) { response ->
//                                    artworkCategories.value = response?.categories.orEmpty()
//                                    isLoading.value = false
//                                }
                                        },
                                    ) {
                                        Text("View categories")
                                    }
                                    Spacer(modifier = Modifier.padding(7.dp))
                                }


                            }

                        }
                    }
                }
            }
    }

    if (openCategoryDialog.value) {
        // get the categories

        val artworkId = selectedArtworkId.value
        LaunchedEffect(artworkId) {
            viewModel.getArtworkCategories(artworkId = artworkId.toString()) { response ->
                artworkCategories.value = response?.categories.orEmpty()
                isLoading.value = false
            }
        }
        AlertDialog(
            onDismissRequest = { openCategoryDialog.value = false},
            confirmButton = {
                Button(onClick = { openCategoryDialog.value = false }) {
                    Text("Close")
                }
            },
            title = {
                Text("Categories")
            },
            text = {
                when {
                    isLoading.value ->
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentAlignment = Alignment.Center) {
                            Column(modifier = Modifier.align(Alignment.Center)) {
                                CircularProgressIndicator(

                                    color = MaterialTheme.colorScheme.onSurface)
                                Spacer(modifier = Modifier.height(5.dp))
                                Text("Loading...")
                            }
                        }

                    else -> {
                        CategoryDialogCards(categories = artworkCategories.value)
                    }
                }
                Row() {
//                items(categories) { category ->
//                    Card(
//                        modifier = Modifier
//                            .padding(end = 8.dp)
//                            .width(250.dp),
//                        shape = RoundedCornerShape(16.dp)
//                    ) {
//                        Column(modifier = Modifier.padding(12.dp)) {
//                            Text(category.name, fontWeight = FontWeight.Bold)
//                            AsyncImage(
//                                model = category.imageUrl,
//                                contentDescription = "Category image",
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .aspectRatio(1f)
//                                    .padding(vertical = 8.dp),
//                                contentScale = ContentScale.Crop
//                            )
//                            Text(category.description)
//                        }
//                    }
//                }
                }
            }

        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDialogCards(categories: List<ArtworkCategory>) {
    val categoryCount = categories.size
    val currentPage = remember { mutableIntStateOf(0) }

//    Row() {
        Column(
//        verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.height(450.dp)
        ) {
            if (categories.isEmpty()) {
                Text("NoResults Found.")
            } else {
                val pagerState = rememberPagerState(pageCount = { categoryCount })
                val curr = rememberCoroutineScope()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center) {
                        HorizontalPager(
                            state = pagerState,
                            pageSpacing = 5.dp,
                            contentPadding = PaddingValues(horizontal = 13.dp),
                            modifier = Modifier.fillMaxWidth(0.9f).align(Alignment.Center)
                        ) { currentPage ->
                            val category = categories[currentPage]
                            Card(modifier = Modifier
                                .width(280.dp)) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    AsyncImage(
                                        model = category.imageUrl,
                                        contentDescription = "ArtworkCategory Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxWidth().height(170.dp)
                                            .clip(RoundedCornerShape(0.8.dp)),
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = category.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    val linkPatterns = Regex("""\[(.*?)\]\((https?://.*?)\)""")
                                    val uriHandler = LocalUriHandler.current
                                    Text(
                                        buildAnnotatedString {
                                            var last = 0
                                            for (match in linkPatterns.findAll(category.description)) {
                                                append(
                                                    category.description.substring(
                                                        last,
                                                        match.range.first
                                                    )
                                                )

                                                val remain = match.groupValues[1]
                                                val url = match.groupValues[2]
                                                val link = LinkAnnotation.Url(url) {
                                                    uriHandler.openUri(url)
                                                }
                                                withLink(link) { append(remain) }
                                                last = match.range.last + 1
                                            }

                                            if (last < category.description.length) {
                                                append(category.description.substring(last))
                                            }
                                        },
                                        modifier = Modifier
                                            .padding(horizontal = 15.dp)
                                            .verticalScroll(rememberScrollState()),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
//                                    Text(
//                                        text = category.description,
//                                        modifier = Modifier.padding(horizontal = 15.dp)
//                                            .verticalScroll(rememberScrollState())
//                                    )

                                }

                            }

                        }

                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(start = 5.dp).size(30.dp),

                            onClick = {
                                val nextImage = pagerState.currentPage + 1

                                curr.launch {
                                    if (nextImage < categoryCount) {
                                        pagerState.scrollToPage(nextImage)
                                    } else {
                                        // go to first page
                                        pagerState.scrollToPage(0)
                                    }
                                }
                            }

                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize().size(30.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Image"
                            )
                        }

                        IconButton(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(end = 5.dp).size(30.dp),
                            onClick = {
                                val prevImage = pagerState.currentPage - 1
                                curr.launch {
                                    if (prevImage >= 0) {
                                            pagerState.scrollToPage(prevImage)
                                        } else {
                                        pagerState.scrollToPage(categoryCount - 1)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                modifier = Modifier.fillMaxSize().size(30.dp),
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Next Image"
                            )
                        }
                    }
//                    HorizontalMultiBrowseCarousel(
//                        state = carouselState,
//                        preferredItemWidth = 220.dp,
//                        itemSpacing = 10.dp
//                    ) { index ->
//                        val category = categories[index % categoryCount]
//                        Card(modifier = Modifier.fillMaxSize()) {
//                            Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
//                                AsyncImage(
//                                    model = category.imageUrl,
//                                    contentDescription = "ArtworkCategory Image",
//                                    contentScale = ContentScale.Crop,
//                                    modifier = Modifier
//                                        .fillMaxWidth().height(170.dp)
//                                        .clip(RoundedCornerShape(0.8.dp)),
//                                )
//                                Spacer(modifier = Modifier.height(12.dp))
//                                Text(
//                                    text = category.name,
//                                    fontSize = 17.sp,
//                                    fontWeight = FontWeight.Bold,
//                                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                                )
//
//                                Spacer(modifier = Modifier.height(10.dp))
//                                Text(
//                                    text = category.description,
//                                    modifier = Modifier.padding(horizontal = 15.dp)
//                                        .verticalScroll(rememberScrollState())
//                                )
//                            }
//
//                        }
//                    }


                }
            }


//    }


        }}

@Composable
fun TextLinkStyles() {
    TODO("Not yet implemented")
}

data class ImageTabItem(
    val text: String,
    val icon: ImageVector
)