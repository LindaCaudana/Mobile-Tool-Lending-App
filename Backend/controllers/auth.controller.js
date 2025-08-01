const jwt = require('jsonwebtoken');
const User = require('../models/user.model');
const env = require('../config/env.config');
const AppError = require('../utils/appError');

// Registro de usuario (solo accesible si restrictTo('admin') ya se aplicó en la ruta)
exports.register = async (req, res, next) => {
  try {
    const { name, email, password, role = 'user' } = req.body;

    const user = await User.create({ name, email, password, role });
    console.log('Usuario creado:', user);
    res.status(201).json({
      id: user._id,
      name: user.name,
      email: user.email,
      role: user.role
    });

  } catch (error) {
        console.error('❌ Error al registrar:', error); // 👈 Agrega esto

    next(error);
  }
};

// Login de usuario
exports.login = async (req, res, next) => {
  try {
    const { identifier, password } = req.body;

    if (!identifier || !password) {
      return next(new AppError('Por favor ingresa usuario y contraseña', 400));
    }

    const user = await User.findByCredentials(identifier, password);

    const token = jwt.sign({ id: user._id }, env.JWT_SECRET, {
      expiresIn: env.JWT_EXPIRES_IN
    });

    user.password = undefined;

    res.status(200).json({
      status: 'success',
      token,
      data: {
        user
      }
    });
  } catch (error) {
    next(new AppError(error.message || 'Error en login', 401));
  }
};

// Placeholder para recuperación de contraseña (NO HABILITADO)
exports.forgotPassword = (req, res) => {
  res.status(501).json({ error: 'Método no implementado' });
};

exports.resetPassword = (req, res) => {
  res.status(501).json({ error: 'Método no implementado' });
};