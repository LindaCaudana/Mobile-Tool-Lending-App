const express = require('express');
const router = express.Router();
const authController = require('../controllers/auth.controller');
const authMiddleware = require('../middleware/auth.middleware');

// Registro (solo admin) 
router.post(
  '/register',
  authMiddleware.protect,
  authMiddleware.restrictTo('admin'),
  authController.register
);

// Login (todos)
router.post('/login', authController.login);

// Recuperación de contraseña (NO HABILITADO)
router.post('/forgot-password', authController.forgotPassword);
router.post('/reset-password', authController.resetPassword);

module.exports = router;
