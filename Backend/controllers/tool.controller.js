const { Tool, AdminTool } = require('../models/tool.model');
const Loan = require('../models/loan.model');
const catchAsync = require('../utils/catchAsync');
const AppError = require('../utils/appError');
const { deletePreviousImage } = require('../utils/fileUpload');

// Importa el modelo para la colección admin_tools

// Crear herramienta 
exports.createTool = catchAsync(async (req, res, next) => {
  req.body.owner = req.user.id;

  if (req.file) {
    req.body.image = `/uploads/tools/${req.file.filename}`;
  }

  // Guardar herramienta en colección principal
  const tool = await Tool.create(req.body);

  // Guardar también en colección admin_tools
  await AdminTool.create(req.body);

  res.status(201).json({
    status: 'success',
    data: { tool }
  });
});

// Actualizar herramienta (solo admins pueden modificar cualquier campo)
exports.updateTool = catchAsync(async (req, res, next) => {
  const tool = await Tool.findById(req.params.id);
  if (!tool) return next(new AppError('Herramienta no encontrada', 404));

  if (req.user.role !== 'admin') {
    return next(new AppError('No autorizado para modificar herramientas', 403));
  }

  if (req.file) {
    if (tool.image) await deletePreviousImage(tool.image);
    req.body.image = `/uploads/tools/${req.file.filename}`;
  }

  const updatedTool = await Tool.findByIdAndUpdate(req.params.id, req.body, {
    new: true,
    runValidators: true
  });

  res.status(200).json({
    status: 'success',
    data: { tool: updatedTool }
  });
});

// Obtener todas las herramientas
exports.getAllTools = catchAsync(async (req, res, next) => {
  const tools = await Tool.find();

  res.status(200).json({
    status: 'success',
    results: tools.length,
    data: { tools }
  });
});

// Obtener una herramienta específica
exports.getTool = catchAsync(async (req, res, next) => {
  const tool = await Tool.findById(req.params.id);
  if (!tool) return next(new AppError('Herramienta no encontrada', 404));

  res.status(200).json({
    status: 'success',
    data: { tool }
  });
});

// Eliminar herramienta (solo admins)
exports.deleteTool = catchAsync(async (req, res, next) => {
  const tool = await Tool.findByIdAndDelete(req.params.id);
  if (!tool) return next(new AppError('Herramienta no encontrada', 404));

  if (tool.image) await deletePreviousImage(tool.image);

  res.status(204).json({
    status: 'success',
    data: null
  });
});

// Verificar disponibilidad
exports.checkToolAvailability = catchAsync(async (req, res, next) => {
  const tool = await Tool.findById(req.params.id);
  if (!tool) return next(new AppError('Herramienta no encontrada', 404));

  const activeLoans = await Loan.countDocuments({ 
    tool: req.params.id, 
    status: { $in: ['activo', 'pendiente'] }
  });

  res.status(200).json({
    status: 'success',
    data: {
      available: tool.availableQuantity > activeLoans,
      quantity: tool.availableQuantity,
      onLoan: activeLoans
    }
  });
});

// Solicitar préstamo
exports.requestToolLoan = catchAsync(async (req, res, next) => {
  const tool = await Tool.findById(req.params.id);
  if (!tool) return next(new AppError('Herramienta no encontrada', 404));

  const activeLoans = await Loan.countDocuments({
    tool: req.params.id,
    status: { $in: ['activo', 'pendiente'] }
  });

  if (activeLoans >= tool.availableQuantity) {
    return next(new AppError('No hay herramientas disponibles', 400));
  }

  const loan = await Loan.create({
    tool: req.params.id,
    user: req.user.id,
    requestDate: Date.now(),
    status: 'pendiente'
  });

  res.status(201).json({
    status: 'success',
    data: { loan }
  });
});
