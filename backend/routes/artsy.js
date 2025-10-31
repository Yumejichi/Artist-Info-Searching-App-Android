import express from 'express';
import authenticationMiddleware from '../middleware/authenticationMiddleware.js';
const router = express.Router();
import User from '../models/User.js';
import Favorite from '../models/Favorite.js';

import dotenv from 'dotenv';
const result = dotenv.config();

// let token = process.env.ARTSY_TOKEN;
//fetch data from Artsy Server
// search from the name of the artist
async function getArtistDataByName(name, token) {
  let artsy_api_url =
    'https://api.artsy.net/api/search?type=artist&q=' + name + '&size=10';
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(artsy_api_url, { headers: headers });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const result = await response.json();
    const artists_results = result['_embedded']['results'];
    return artists_results;
  } catch (error) {
    console.error(error.message);
  }
}

async function getSimilarArtistsid(id, token) {
  const url = 'https://api.artsy.net/api/artists?similar_to_artist_id=' + id;
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(url, { headers: headers });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const resultJson = await response.json();

    const detail = resultJson['_embedded']?.artists || [];
    // console.log('Similar Artists:', detail);

    return detail;
  } catch (error) {
    console.error(error.message);
  }
}

async function getArtistDetailById(id, token) {
  let artist_detail_url = 'https://api.artsy.net/api/artists/' + id;
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(artist_detail_url, { headers: headers });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const resultJson = await response.json();

    // const detail = resultJson['_embedded'];
    return resultJson;
  } catch (error) {
    console.error(error.message);
  }
}

async function getArtworksById(id, token) {
  let artist_artworks_url =
    'https://api.artsy.net/api/artworks?artist_id=' + id + '&size=10';
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(artist_artworks_url, { headers: headers });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const resultJson = await response.json();
    // console.log('artworks: ', resultJson);
    return resultJson;
  } catch (error) {
    console.error(error.message);
  }
}

async function getcategoryByArtworkId(id, token) {
  let artwork_gene_url = 'https://api.artsy.net/api/genes?artwork_id=' + id;
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(artwork_gene_url, { headers: headers });
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const resultJson = await response.json();
    return resultJson;
  } catch (error) {
    console.error(error.message);
  }
}

router.get('/search/:name', async (req, res) => {
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let name = req.params.name;
  let response = await getArtistDataByName(name, req.token);
  // console.log(response);
  res.send(response);
});

router.get('/similar/:id', async (req, res) => {
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let id = req.params.id;
  const response = await getSimilarArtistsid(id, req.token);
  // console.log('similar artists: ', response);
  res.send(response);
});

router.get('/details/:id', async (req, res) => {
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let id = req.params.id;
  let response = await getArtistDetailById(id, req.token);
  res.send(response);
});

router.get('/artworks/:id', async (req, res) => {
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let id = req.params.id;
  let response = await getArtworksById(id, req.token);
  res.send(response);
});

router.get('/category/:id', async (req, res) => {
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let id = req.params.id;
  let response = await getcategoryByArtworkId(id, req.token);
  res.send(response);
});

router.post('/addFavorite', authenticationMiddleware, async (req, res) => {
  const artistId = req.body.artistId;
  const emailAddress = req.user.email;
  // const result = await check_token_validation();
  // if (result == false) {
  //   token = await get_new_token();
  // }
  let artistDetail = await getArtistDetailById(artistId, req.token);
  // console.log('artistId', artistId);
  let imageUrl = 'assets/images/artsy_logo.svg';
  if (
    artistDetail &&
    artistDetail._links &&
    artistDetail._links.thumbnail &&
    artistDetail._links.thumbnail.href
  ) {
    if (imageUrl !== '/assets/shared/missing_image.png') {
      imageUrl = artistDetail._links.thumbnail.href;

      console.log(artistDetail._links.thumbnail.href);
    }
  }

  const favorite = new Favorite({
    artistId: artistId,
    artistName: artistDetail.name,
    artistBirthday: artistDetail.birthday,
    artistDeathday: artistDetail.deathday,
    artistNationality: artistDetail.nationality,
    artistImageUrl: imageUrl,
    favoritedTime: new Date(),
    userEmail: emailAddress,
  });
  // console.log(favorite);
  await favorite.save();
  return res.status(200).json({ msg: 'Added to favorites' });
});

router.post('/removeFavorite', authenticationMiddleware, async (req, res) => {
  const artistId = req.body.artistId;
  const emailAddress = req.user.email;
  await Favorite.deleteOne({ artistId: artistId, userEmail: emailAddress });

  return res.status(200).json({ msg: 'Removed from favorites' });
});

export default router;
