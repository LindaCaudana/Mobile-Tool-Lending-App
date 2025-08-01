const express = require('express');
const router = express.Router();
const toolController = require('../controllers/tool.controller');
const authMiddleware = require('../middleware/auth.middleware');
const { uploadToolImage } = require('../utils/fileUpload');

// rutas protegidas por JWT
router.use(authMiddleware.protect);

// Rutas para herramientas
router.route('/')
  .get(authMiddleware.restrictTo('admin', 'user'), toolController.getAllTools)
  .post(
    authMiddleware.restrictTo('admin'),
    uploadToolImage,
    toolController.createTool
  );

router.route('/:id')
  .get(authMiddleware.restrictTo('admin', 'user'), toolController.getTool)
  .patch(
    authMiddleware.restrictTo('admin'),
    uploadToolImage,
    toolController.updateTool
  )
  .delete(authMiddleware.restrictTo('admin'), toolController.deleteTool);

// Ruta para verificar disponibilidad
router.route('/:id/availability')
  .get(authMiddleware.restrictTo('user', 'admin'), toolController.checkToolAvailability);

// Ruta para solicitar préstamo
router.route('/:id/request-loan')
  .post(authMiddleware.restrictTo('user'), toolController.requestToolLoan);

module.exports = router;