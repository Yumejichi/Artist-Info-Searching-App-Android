import mongoose from 'mongoose';

const favoriteSchema = mongoose.Schema({
  artistId: { type: String, required: true },
  artistName: { type: String, required: true },
  artistBirthday: { type: String },
  artistDeathday: { type: String },
  artistNationality: { type: String },
  artistImageUrl: { type: String, required: true },
  favoritedTime: { type: Date, required: true },
  userEmail: { type: String, required: true },
});

const Favorite = mongoose.model('Favorites', favoriteSchema);
export default Favorite;
