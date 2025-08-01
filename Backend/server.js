require('dotenv').config({ path: `${__dirname}/.env` });
const express = require('express');
const cors = require('cors');
const path = require('path');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
const mongoose = require('mongoose');

const app = express();

//  Middlewares de seguridad

app.use(helmet());
app.use(cors({
  origin: process.env.CORS_ORIGIN || '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']
}));

// Limitar peticiones a la API
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutos
  max: 200, // Límite de peticiones por IP
  standardHeaders: true,
  legacyHeaders: false
});
app.use('/api/', limiter);

//Conf d Express

app.use(express.json({ limit: '10kb' }));
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));


// Conexión a Mongo

const connectDB = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI, {
      serverSelectionTimeoutMS: process.env.MONGO_CONNECT_TIMEOUT || 5000,
      socketTimeoutMS: 45000,
      maxPoolSize: 50,
      retryWrites: true,
      retryReads: true
    });
    console.log(' MongoDB conectado');
  } catch (err) {
    console.error(' Error de conexión a MongoDB:', err.message);
    process.exit(1);
  }
};
connectDB();


// Riutas de la API 

app.use('/api/v1/auth', require('./routes/auth.routes'));
console.log('Auth routes loaded');
app.use('/api/v1/users', require('./routes/user.routes'));
app.use('/api/v1/tools', require('./routes/tool.routes'));
app.use('/api/v1/loans', require('./routes/loan.routes'));


// Manejo de errores

// Ruta no encontrada (404)
app.use((req, res) => {
  res.status(404).json({
    success: false,
    message: 'Endpoint no encontrado'
  });
});

// Middleware de errores
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({
    success: false,
    message: 'Error interno del servidor'
  });
});


// Iniciar servidor

const PORT = process.env.PORT || process.env.APP_PORT || 3000;
const ENV = process.env.NODE_ENV || 'development';

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Servidor en modo ${ENV} escuchando en puerto ${PORT}`);
  //console.log(` http://localhost:${PORT}`);
});


module.exports = app;
