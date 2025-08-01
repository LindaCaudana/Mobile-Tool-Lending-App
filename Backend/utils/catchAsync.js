/**
 * Wrapper para manejar errores en controladores async
 * @param {Function} fn - Función del controlador async
 * @returns {Function} Función middleware que maneja errores
 */
const catchAsync = (fn) => (req, res, next) => {
  // Usamos Promise.resolve para asegurar que siempre sea una promesa
  Promise.resolve(fn(req, res, next)).catch((err) => {
    console.error('⚠️ Error en controlador async:', err.stack);
    next(err);
  });
};

module.exports = catchAsync;