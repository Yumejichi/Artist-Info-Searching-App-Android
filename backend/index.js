import express from 'express';
import mongoose from 'mongoose';
import dotenv from 'dotenv';
import loginRouter from './routes/login.js';
import logoutRouter from './routes/logout.js';
import artsyRouter from './routes/artsy.js';
import registerRouter from './routes/register.js';
import meRouter from './routes/me.js';
import cookieParser from 'cookie-parser';
dotenv.config();

let token =
  'eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6IiIsInN1YmplY3RfYXBwbGljYXRpb24iOiIxMjczZjhjZS1kMzRhLTQzYmUtYTcwMy04ODUwYTk4MDJhYWQiLCJleHAiOjE3NDQ4OTIxMzMsImlhdCI6MTc0NDI4NzMzMywiYXVkIjoiMTI3M2Y4Y2UtZDM0YS00M2JlLWE3MDMtODg1MGE5ODAyYWFkIiwiaXNzIjoiR3Jhdml0eSIsImp0aSI6IjY3ZjdiNjY1OTZmNmU1MTc1ZGIyZGMyYSJ9.pglmHxxXDtM6gadAIDW1f-3ntIeej5QKv4u_Tj19QO4';

const app = express();
const PORT = process.env.PORT || 8080;
const MONGO_DB_URI = process.env.MONGO_URI;

app.use(cookieParser());

async function get_new_token() {
  try {
    const response = await fetch(
      'https://api.artsy.net/api/tokens/xapp_token',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          client_id: process.env.ARTSY_TOKEN_CLIENTID,
          client_secret: process.env.ARTSY_TOKEN_CLIENTSECRET,
        }),
      }
    );
    if (!response.ok) {
      throw new Error(`Response status: ${response.status}`);
    }

    const result = await response.json();
    // console.log(result);

    let new_token = result['token'];
    console.log('New TOken: ' + new_token);
    return new_token;
  } catch (error) {
    console.error(error.message);
  }
}

// check if token is expired or not, if expired, get a new one
async function check_token_validation() {
  let artsy_api_url = 'https://api.artsy.net/api/search?q=Picasso&size=1';
  let headers = { 'X-XAPP-Token': token };
  try {
    const response = await fetch(artsy_api_url, { headers: headers });
    const result = await response.json();
    if (!response.ok) {
      if (response.status === 401) {
        console.log(
          'Artsy responded 401 => token invalid or expired: ',
          result
        );
        return false;
      }
    }

    if (result['type'] == 'auth_error') {
      console.log('Auth error');
      return false;
    }
    return true;
  } catch (error) {
    console.error(error.message);
  }
}

mongoose
  .connect(MONGO_DB_URI)
  .then(() => console.log('Connected to MongoDB!'))
  .catch((error) => console.log(error));

app.use(express.json());

// Routing
app.use('/api/user', loginRouter);
app.use(
  '/api/artsy',
  (req, res, next) => {
    req.token = req.app.locals.artsyToken;
    next();
  },
  artsyRouter
);

app.use('/api/user', registerRouter);
app.use('/api/user', logoutRouter);
app.use('/api/user', meRouter);
app.use('/', express.static('public'));
app.use('*', express.static('public'));

async function startApp() {
  try {
    const res = await check_token_validation();
    if (res == false) {
      token = await get_new_token();
    }
    app.locals.artsyToken = token;
    app.listen(PORT, () => {
      console.log(`Example app listening on port ${PORT}`);
    });
  } catch (error) {
    console.error(error.message);
  }
}

startApp();
