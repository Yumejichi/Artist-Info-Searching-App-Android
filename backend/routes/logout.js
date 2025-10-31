import express from 'express';
import authenticationMiddleware from '../middleware/authenticationMiddleware.js';
const router = express.Router();

router.post('/logout', authenticationMiddleware, async (req, res) => {
  res.clearCookie('token');

  return res.status(200).json({ msg: 'Logged out' });
});

export default router;
