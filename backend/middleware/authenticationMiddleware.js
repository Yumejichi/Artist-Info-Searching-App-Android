import JWT from 'jsonwebtoken';

export default function authenticationMiddleware(req, res, next) {
  const token = req.cookies.token;
  // console.log('req', req.cookies);
  if (!token) {
    // console.log("no token ")
    return res
      .status(401)
      .json({ message: 'Authentication failed, No token available' });
  }

  // console.log(token);

  JWT.verify(token, process.env.SECRETKEY, (error, user) => {
    if (error) {
      return res.status(401).json(error);
    }
    req.user = user;
    next();
  });
}
