// middleware/loanMiddleware.js
const Loan = require('../models/loan.model');
const AppError = require('../utils/appError');
const catchAsync = require('../utils/catchAsync');

exports.checkLoanOwnership = catchAsync(async (req, res, next) => {
  const loan = await Loan.findById(req.params.id);
  
  if (!loan) {
    return next(new AppError('Préstamo no encontrado', 404));
  }
  
  // Verificar si el usuario es el dueño o admin
  if (loan.user.toString() !== req.user.id && req.user.role !== 'admin') {
    return next(new AppError('No tienes permiso para acceder a este préstamo', 403));
  }
  
  // Adjuntar el préstamo al request para reutilización
  req.loan = loan;
  next();
});