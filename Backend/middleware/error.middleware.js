const AppError = require('../utils/appError');

const handleErrors = (err, req, res, next) => {
  console.error(' Error:', err.stack);
  
  if (err instanceof AppError) {
    return res.status(err.statusCode).json({
      status: err.status,
      message: err.message
    });
  }

  // Error no controlado
  res.status(500).json({
    status: 'error',
    message: 'Algo salió muy mal!'
  });
};

module.exports = handleErrors;