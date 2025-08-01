const express = require('express');
const router = express.Router();
const userController = require('../controllers/user.controller');
const authMiddleware = require('../middleware/auth.middleware');

// Proteger todas las rutas con middleware de autenticación y autorización (solo admin)
router.use(authMiddleware.protect);
router.use(authMiddleware.restrictTo('admin'));

// CRUD usuarios
router.post('/', userController.createUser);
router.get('/', userController.getAllUsers);
router.get('/:id', userController.getUser);
router.patch('/:id', userController.updateUser);
router.delete('/:id', userController.deleteUser);

// Cambiar propia contraseña (usuaris pueden hacerlo sin ser admin)
router.patch('/update-my-password', authMiddleware.protect, userController.updateMyPassword);

// Importación masiva de usuarios (solo admin)
router.post('/import', userController.importUsers);

module.exports = router;
