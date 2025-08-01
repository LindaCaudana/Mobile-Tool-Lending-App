const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const validator = require('validator');

const userSchema = new mongoose.Schema({
  name: {
    type: String,
    required: [true, 'El nombre es requerido'],
    trim: true,
    maxlength: [50, 'El nombre no puede exceder 50 caracteres']
  },
  username: {
    type: String,
    unique: true,
    trim: true,
    sparse: true,
    index: true
  },
  email: {
    type: String,
    required: [true, 'El email es requerido'],
    unique: true,
    lowercase: true,
    validate: [validator.isEmail, 'Por favor ingrese un email válido'],
    index: true
  },
  password: {
    type: String,
    required: [true, 'La contraseña es requerida'],
    minlength: [8, 'La contraseña debe tener al menos 8 caracteres'],
    select: false
  },
  role: {
    type: String,
    enum: ['admin', 'user'],
    default: 'user'
  },
  toolsBorrowed: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Tool'
  }]
}, { 
  timestamps: true,
  toJSON: { 
    virtuals: true,
    transform: function(doc, ret) {
      delete ret.password;
      delete ret.__v;
      return ret;
    }
  },
  toObject: { virtuals: true }
});

// Hash de contraseña antes de guardar
userSchema.pre('save', async function(next) {
  if (!this.isModified('password')) return next();
  try {
    const salt = await bcrypt.genSalt(12);
    this.password = await bcrypt.hash(this.password, salt);
    next();
  } catch (err) {
    next(err);
  }
});

// Método para comparar contraseñas
userSchema.methods.comparePassword = async function(candidatePassword) {
  return await bcrypt.compare(candidatePassword, this.password);
};

// Método para buscar por username o email y validar contraseña
userSchema.statics.findByCredentials = async function(identifier, password) {
  console.log(' Buscando usuario con:', identifier);

  const user = await this.findOne({
    $or: [
      { email: identifier },
      { username: identifier }
    ]
  }).select('+password');

  if (!user) {
    console.log(' Usuario no encontrado');
    throw new Error('Credenciales incorrectas');
  }

  const isMatch = await bcrypt.compare(password, user.password);
  console.log(` Comparando contraseña: ${password} con hash: ${user.password}`);
  console.log('Resultado:', isMatch);

  if (!isMatch) {
    console.log(' Contraseña incorrecta');
    throw new Error('Credenciales incorrectas');
  }

  return user;
};

module.exports = mongoose.model('User', userSchema);
