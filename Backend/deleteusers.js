const mongoose = require('mongoose');
require('dotenv').config();

const User = require('./models/user.model'); 

const borrarUsuarios = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI, {
      dbName: 'herramientasDB', 
    });
    console.log(' Conectado a MongoDB');

    const resultado = await User.deleteMany({});
    console.log(` Usuarios borrados: ${resultado.deletedCount}`);

    process.exit(0);
  } catch (err) {
    console.error(' Error al borrar usuarios:', err.message);
    process.exit(1);
  }
};

borrarUsuarios();
