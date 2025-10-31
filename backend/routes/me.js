import express from 'express';
import authenticationMiddleware from '../middleware/authenticationMiddleware.js';
import User from '../models/User.js';
import Favorite from '../models/Favorite.js';

const router = express.Router();

router.get('/me', authenticationMiddleware, async (req, res) => {
  const emailAddress = req.user.email;
  // console.log(emailAddress);
  const user = await User.findOne({ emailAddress: emailAddress });
  if (!user) {
    return res.status(404).json({ message: 'User not found.' });
  }

  const favorites = await Favorite.find(
    { userEmail: emailAddress },
    '-userEmail -_id -__v'
  ).sort({
    favoritedTime: -1,
  });

  // console.log(req.user.exp);
  const userData = {
    fullName: user.fullName,
    profileImageUrl: user.profileImageUrl,
    userFavorites: favorites,
    tokenExpiresTime: req.user.exp ? req.user.exp * 1000 : null,
  };

  // console.log(userData.tokenExpiresTime);

  res.json(userData);
});

router.get('/favorites', authenticationMiddleware, async (req, res) => {
  const emailAddress = req.user.email;
  // console.log(emailAddress);
  const user = await User.findOne({ emailAddress: emailAddress });
  if (!user) {
    return res.status(404).json({ message: 'User not found.' });
  }
  const favorites = await Favorite.find(
    { userEmail: emailAddress },
    '-userEmail -_id -__v'
  ).sort({
    favoritedTime: -1,
  });
  // console.log('user favorite in desc: ', favorites);

  res.json(favorites);
});

router.post('/deleteAccount', authenticationMiddleware, async (req, res) => {
  const emailAddress = req.user.email;
  // console.log(emailAddress);
  const user = await User.findOne({ emailAddress: emailAddress });
  
  if (!user) {
    return res.status(404).json({ message: 'User not found.' });
  }
  await User.deleteOne({ emailAddress: emailAddress });

  // delete users favorites too
  await Favorite.deleteMany({ userEmail: emailAddress });

  res.clearCookie('token');
  return res.status(200).json({ msg: 'Account deleted' });
});

export default router;
