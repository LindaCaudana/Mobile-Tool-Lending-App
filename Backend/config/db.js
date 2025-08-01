const mongoose = require('mongoose');
const env = require('./env.config');

const connectDB = async () => {
  try {
    await mongoose.connect(env.MONGO_URI, {
      autoIndex: true,
      serverSelectionTimeoutMS: 5000,
      socketTimeoutMS: 45000, // Añadir timeout para sockets
      maxPoolSize: 50, // Conexiones máximas
      wtimeoutMS: 2500 // Timeout para operaciones de escritura
    });
    
    console.log(' MongoDB Conectado');
  } catch (err) {
    console.error(' Error de conexión a MongoDB:', err.message);
    process.exit(1); 
  }
};


mongoose.connection.on('connecting', () => {
  console.log(' Intentando conectar a MongoDB...');
});

mongoose.connection.on('disconnected', () => {
  console.log('Desconectado de MongoDB');
});

mongoose.connection.on('error', (err) => {
  console.error(' Error de MongoDB:', err.message);
});

module.exports = connectDB;