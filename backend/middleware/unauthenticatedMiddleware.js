import JWT from 'jsonwebtoken';

export default function authenticationMiddleware(req, res, next) {
  const token = req.cookies.token;
  if (!token) {
    return next();
  }

  JWT.verify(token, process.env.SECRETKEY, (error, user) => {
    if (error) {
      return next();
    }
    return res.status(401).json('Authenticated already');
  });
}
