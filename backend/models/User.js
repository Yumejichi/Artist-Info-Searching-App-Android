import mongoose from 'mongoose';

const userSchema = mongoose.Schema({
  fullName: { type: String, required: true },
  emailAddress: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  profileImageUrl: { type: String, required: true },
});

const User = mongoose.model('User', userSchema);
export default User;
