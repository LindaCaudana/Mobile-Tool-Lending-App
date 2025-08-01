const User = require('../models/user.model');
const AppError = require('../utils/appError');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const env = require('../config/env.config');

const filterObj = (obj, ...allowedFields) => {
  const newObj = {};
  Object.keys(obj).forEach(el => {
    if (allowedFields.includes(el)) newObj[el] = obj[el];
  });
  return newObj;
};

exports.createUser = async (req, res, next) => {
  try {
    const filteredBody = filterObj(req.body, 'name', 'email', 'password', 'role');

    const existingUser = await User.findOne({ email: filteredBody.email });
    if (existingUser) {
      return next(new AppError('El email ya está registrado', 400));
    }

    if (filteredBody.password) {
      filteredBody.password = await bcrypt.hash(filteredBody.password, 12);
    }

    const newUser = await User.create({
      ...filteredBody,
      role: filteredBody.role || 'mecanico',
    });

    const token = jwt.sign({ id: newUser._id }, env.JWT_SECRET, {
      expiresIn: env.JWT_EXPIRES_IN
    });

    newUser.password = undefined;

    res.status(201).json({
      status: 'success',
      token,
      data: {
        user: newUser
      }
    });

  } catch (err) {
    next(err);
  }
};

exports.getAllUsers = async (req, res, next) => {
  try {
    const users = await User.find().select('-password -__v');

    res.status(200).json({
      status: 'success',
      results: users.length,
      data: {
        users
      }
    });
  } catch (err) {
    next(err);
  }
};

exports.updateUser = async (req, res, next) => {
  try {
    if (req.user.id === req.params.id) {
      return next(new AppError('No puedes modificar tu propio rol/estado', 400));
    }

    const filteredBody = filterObj(req.body, 'name', 'email', 'role'); 

    if (req.body.password) {
      filteredBody.password = await bcrypt.hash(req.body.password, 12);
    }

    const updatedUser = await User.findByIdAndUpdate(
      req.params.id,
      filteredBody,
      { new: true, runValidators: true }
    ).select('-password -__v');

    if (!updatedUser) {
      return next(new AppError('Usuario no encontrado', 404));
    }

    res.status(200).json({
      status: 'success',
      data: {
        user: updatedUser
      }
    });

  } catch (err) {
    next(err);
  }
};

exports.deleteUser = async (req, res, next) => {
  try {
    if (req.user.id === req.params.id) {
      return next(new AppError('No puedes desactivar tu propia cuenta', 400));
    }

    await User.findByIdAndDelete(req.params.id);

    res.status(204).json({
      status: 'success',
      data: null
    });

  } catch (err) {
    next(err);
  }
};

exports.getUser = async (req, res, next) => {
  try {
    const user = await User.findById(req.params.id).select('-password -__v');

    if (!user) {
      return next(new AppError('Usuario no encontrado', 404));
    }

    res.status(200).json({
      status: 'success',
      data: {
        user
      }
    });
  } catch (err) {
    next(err);
  }
};

exports.updateMyPassword = async (req, res, next) => {
  try {
    const user = await User.findById(req.user.id).select('+password');

    if (!(await user.comparePassword(req.body.currentPassword))) {
      return next(new AppError('Tu contraseña actual es incorrecta', 401));
    }

    user.password = await bcrypt.hash(req.body.newPassword, 12);
    await user.save();

    const token = jwt.sign({ id: user._id }, env.JWT_SECRET, {
      expiresIn: env.JWT_EXPIRES_IN
    });

    res.status(200).json({
      status: 'success',
      token,
      data: {
        user: {
          id: user._id,
          name: user.name,
          email: user.email,
          role: user.role
        }
      }
    });

  } catch (err) {
    next(err);
  }
};

exports.importUsers = async (req, res, next) => {
  const usuarios = req.body.usuarios;

  if (!Array.isArray(usuarios) || usuarios.length === 0) {
    return next(new AppError('Envía un array con usuarios para importar', 400));
  }

  try {
    const resultados = [];

    for (const usuario of usuarios) {
      const { name, username, password, role } = usuario;

      if (!name || !username || !password) {
        resultados.push({ username, status: 'failed', reason: 'Faltan campos obligatorios' });
        continue;
      }

      const emailFake = `${username}@taller.local`;
      const existente = await User.findOne({ $or: [{ username }, { email: emailFake }] });

      if (existente) {
        resultados.push({ username, status: 'skipped', reason: 'Usuario ya existe' });
        continue;
      }

      const hashedPassword = await bcrypt.hash(password, 12);

      const nuevoUsuario = new User({
        name,
        username,
        email: emailFake,
        password: hashedPassword,
        role: role && role.toLowerCase().includes('admin') ? 'admin' : 'user',
      });

      await nuevoUsuario.save();

      resultados.push({ username, status: 'created' });
    }

    res.status(201).json({
      status: 'success',
      message: 'Importación finalizada',
      results: resultados
    });
  } catch (err) {
    next(new AppError('Error interno al importar usuarios', 500));
  }
};
