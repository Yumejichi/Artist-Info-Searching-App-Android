import express from 'express';
import crypto from 'crypto';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import User from '../models/User.js';
import unauthenticatedMiddleware from '../middleware/unauthenticatedMiddleware.js';

const router = express.Router();

// generate a profile image URL
function generateProfileImageUrl(email, size) {
  let trimmedEmail = email.trim().toLowerCase();
  const hash = crypto.createHash('sha1').update(trimmedEmail).digest('hex');
  return `https://www.gravatar.com/avatar/${hash}?s=${size}&d=identicon`;
}

// hash the password use bcrypt
function hashPassword(password) {
  const saltRounds = 10;
  const salt = bcrypt.genSaltSync(saltRounds);
  const hashedPassword = bcrypt.hashSync(password, salt);
  return hashedPassword;
}

// create and return the JWT token
function generateJWTtoken(payload) {
  let token = jwt.sign(payload, process.env.SECRETKEY, {
    algorithm: 'HS256',
    expiresIn: '1h',
  });
  return token;
}

router.post('/register', unauthenticatedMiddleware, async (req, res) => {
  //get the user email and the name
  let fullName = req.body.fullName;
  let email = req.body.emailAddress;
  let password = req.body.password;
  // console.log(req.body);

  // check if the email is already exiting or not:
  const emailExistsUser = await User.findOne({ emailAddress: email });
  if (emailExistsUser) {
    res.status(400).send('User with this email already exists.');
    return;
  }

  let hashedPassword = hashPassword(password);

  const size = 200; // Optional size parameter
  const profileImageUrl = generateProfileImageUrl(email, size);
  const payload = {
    name: fullName,
    email: email,
  };
  const token = generateJWTtoken(payload);
  const user = new User({
    fullName: fullName,
    emailAddress: email,
    password: hashedPassword,
    profileImageUrl: profileImageUrl,
  });
  // console.log(user);

  user
    .save()
    .then((result) => {
      res.cookie('token', token, {
        secure: true,
        httpOnly: true,
        sameSite: 'none',
        maxAge: 60 * 60 * 1000,
      });
      return res.status(201).json({ message: 'User registered successfully' });
    })
    .catch((error) => {
      return res.status(500).send('Server error');
    });
});

export default router;
