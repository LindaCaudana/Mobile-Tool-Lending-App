const mongoose = require('mongoose');
const Loan = require('../models/loan.model');
const {Tool} = require('../models/tool.model');
const User = require('../models/user.model');
const catchAsync = require('../utils/catchAsync');
const AppError = require('../utils/appError');

exports.getUserLoans = catchAsync(async (req, res, next) => {
  const loans = await Loan.find({ user: req.user.id })
    .populate('tool', 'name description image')
    .populate('user', 'name email')   // <--- Agrega esta línea
    .sort('-createdAt');

  res.status(200).json({
    status: 'success',
    results: loans.length,
    data: {
      loans
    }
  });
});

exports.getOverdueLoans = catchAsync(async (req, res, next) => {
  const overdueLoans = await Loan.find({
    user: req.user.id,
    status: 'activo',
    endDate: { $lt: new Date() }
  })
  .populate('tool', 'name image')
  .sort('-endDate');
  
  res.status(200).json({
    status: 'success',
    results: overdueLoans.length,
    data: {
      loans: overdueLoans,
      message: overdueLoans.length === 0 
        ? 'No tienes préstamos vencidos' 
        : `Tienes ${overdueLoans.length} préstamo(s) vencido(s)`
    }
  });
});

exports.getLoan = catchAsync(async (req, res, next) => {
  const loan = await Loan.findById(req.params.id)
    .populate('tool', 'name image')
    .populate('user', 'name email');


  if (!loan) {
    return next(new AppError('Préstamo no encontrado', 404));
  }

  if (loan.user.toString() !== req.user.id && req.user.role !== 'admin') {
    return next(new AppError('No tienes permiso para acceder a este préstamo', 403));
  }

  res.status(200).json({
    status: 'success',
    data: {
      loan
    }
  });
});

exports.returnTool = catchAsync(async (req, res, next) => {
  const session = await mongoose.startSession();
  session.startTransaction();

  try {
    const loan = await Loan.findById(req.params.id).session(session);
    
    if (!loan) {
      throw new AppError('Préstamo no encontrado', 404);
    }

    if (loan.user.toString() !== req.user.id) {
      throw new AppError('No tienes permiso para esta acción', 403);
    }

    if (loan.status === 'devuelto') {
      throw new AppError('Esta herramienta ya fue devuelta', 400);
    }

    loan.status = 'devuelto';
    loan.returnDate = Date.now();
    await loan.save({ session });

    const tool = await Tool.findById(loan.tool).session(session);
    if (tool) {
      tool.availableQuantity += 1;
      tool.borrowedQuantity -= 1;
      tool.loans.pull(loan._id);
      tool.status = 'disponible';
      tool.currentLoan = null;
      await tool.save({ session });
    }

    await User.findByIdAndUpdate(
      req.user.id,
      { $pull: { toolsBorrowed: loan.tool } },
      { session }
    );

    await session.commitTransaction();
    
    res.status(200).json({
      status: 'success',
      message: 'Herramienta devuelta exitosamente',
      data: {
        loan
      }
    });
  } catch (err) {
    await session.abortTransaction();
    next(err);
  } finally {
    session.endSession();
  }
});

exports.deleteLoan = catchAsync(async (req, res, next) => {
  const loan = await Loan.findByIdAndDelete(req.params.id);

  if (!loan) {
    return next(new AppError('Préstamo no encontrado', 404));
  }

  if (loan.status === 'activo') {
    await Tool.findByIdAndUpdate(loan.tool, {
      $inc: { 
        availableQuantity: 1,
        borrowedQuantity: -1 
      },
      $pull: { loans: loan._id },
      status: 'disponible',
      currentLoan: null
    });
  }

  res.status(204).json({
    status: 'success',
    data: null
  });
});
exports.getAllLoans = catchAsync(async (req, res, next) => {
  const loans = await Loan.find()
    .populate('tool', 'name image')
    .populate('user', 'name email') // Esto es lo importante
    .sort('-createdAt');

  res.status(200).json({
    status: 'success',
    results: loans.length,
    data: { loans }
  });
});

exports.createLoan = catchAsync(async (req, res, next) => {
  const session = await mongoose.startSession();
  session.startTransaction();
  
  try {
    const { toolId, endDate, notes } = req.body;

    if (!toolId || !endDate) {
      throw new AppError('Los campos toolId y endDate son obligatorios', 400);
    }

    const parsedEndDate = new Date(endDate);
    if (parsedEndDate < new Date()) {
      throw new AppError('La fecha de devolución debe ser futura', 400);
    }

    const tool = await Tool.findById(toolId).session(session);
    if (!tool) {
      throw new AppError('Herramienta no encontrada', 404);
    }

    if (tool.availableQuantity <= 0) {
      throw new AppError('No hay unidades disponibles', 400);
    }

    const user = await User.findById(req.user.id).session(session);
    if (!user) {
      throw new AppError('Usuario no encontrado', 404);
    }

    const [loan] = await Loan.create([{
      tool: toolId,
      user: req.user.id,
      startDate: Date.now(),
      endDate: parsedEndDate,
      status: 'activo',
      notes
    }], { session });

    tool.availableQuantity -= 1;
    tool.borrowedQuantity += 1;
    tool.loans.push(loan._id);
    tool.currentLoan = loan._id;
    tool.status = tool.availableQuantity === 0 ? 'prestado' : 'disponible';
    await tool.save({ session });

    user.toolsBorrowed.push(tool._id);
    await user.save({ session });

    await session.commitTransaction();
    
    res.status(201).json({
      status: 'success',
      message: 'Préstamo creado exitosamente',
      data: {
        loan: {
          ...loan.toObject(),
          toolName: tool.name,
          userName: user.name
        }
      }
    });

  } catch (err) {
        console.error("❌ Error interno al crear préstamo:", err); // <-- Agrega esto

    await session.abortTransaction();
    next(err);
  } finally {
    session.endSession();
  }
});