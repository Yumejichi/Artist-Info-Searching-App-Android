import express from 'express';
import jwt from 'jsonwebtoken';
import User from '../models/User.js';
import bcrypt from 'bcrypt';
import unauthenticatedMiddleware from '../middleware/unauthenticatedMiddleware.js';

const router = express.Router();

// create and return the JWT token
function generateJWTtoken(payload) {
  let token = jwt.sign(payload, process.env.SECRETKEY, {
    algorithm: 'HS256',
    expiresIn: '1h',
  });
  return token;
}

router.post('/login', unauthenticatedMiddleware, async (req, res) => {
  const emailAddress = req.body.emailAddress;
  const password = req.body.password;

  // fetch user and test password verification
  const user = await User.findOne({ emailAddress: emailAddress });
  // console.log('User: ' + user);
  if (!user) {
    return res.status(401).json({ message: 'Password or email is incorrect.' });
  }

  const passwordMatches = await bcrypt.compare(password, user.password);
  if (!passwordMatches) {
    return res.status(401).json({ message: 'Password or email is incorrect.' });
  }

  const payload = {
    name: user.fullName,
    email: user.emailAddress,
  };
  const token = generateJWTtoken(payload);
  // console.log('JWT Token:', token);

  res.cookie('token', token, {
    secure: true,
    httpOnly: true,
    sameSite: 'none',
    maxAge: 60 * 60 * 1000,
  });

  return res.status(200).json({ msg: 'Authentication success' });
});

export default router;
