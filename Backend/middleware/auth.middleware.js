const jwt = require('jsonwebtoken');
const { promisify } = require('util');
const User = require('../models/user.model');
const AppError = require('../utils/appError');
const env = require('../config/env.config');

exports.protect = async (req, res, next) => {
  try {
    let token;
    // Verifica que el header Authorization exista y comience con Bearer
    if (req.headers.authorization && req.headers.authorization.startsWith('Bearer')) {
      token = req.headers.authorization.split(' ')[1];
    }
    if (!token) {
      return next(new AppError('Acceso no autorizado', 401));
    }

    // Verifica y decodifica el token
    const decoded = await promisify(jwt.verify)(token, env.JWT_SECRET);

    // Busca el usuario correspondiente en la DB
    const currentUser = await User.findById(decoded.id);
    if (!currentUser) {
      return next(new AppError('Usuario no existe', 401));
    }
  
    req.user = currentUser;
    next();

  } catch (err) {
    next(err);
  }
};

exports.restrictTo = (...roles) => {
  return (req, res, next) => {
    console.log('Rol usuario:', req.user.role, 'Roles permitidos:', roles);
    if (!roles.includes(req.user.role)) {
      return next(new AppError('No tienes permisos', 403));
    }
    next();
  };
};

