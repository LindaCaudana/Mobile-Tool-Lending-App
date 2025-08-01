const express = require('express');
const router = express.Router();
const loanController = require('../controllers/loan.controller');
const authMiddleware = require('../middleware/auth.middleware');

// Middleware global: proteger todo
router.use(authMiddleware.protect);

// Crear préstamo (usuarios y admin)
router.post('/', authMiddleware.restrictTo('user', 'admin'), loanController.createLoan);

// ✅ Esta ruta es para usuarios y admin (sus propios préstamos)
router.get('/', authMiddleware.restrictTo('user', 'admin'), loanController.getUserLoans);

// ✅ Solo el admin puede ver todos
router.get('/all', authMiddleware.restrictTo('admin'), loanController.getAllLoans);

// Otros endpoints
router.get('/overdue', loanController.getOverdueLoans);
router.patch('/:id/return', loanController.returnTool);
router.delete('/:id', authMiddleware.restrictTo('admin'), loanController.deleteLoan);
router.get('/:id', loanController.getLoan);

module.exports = router;
