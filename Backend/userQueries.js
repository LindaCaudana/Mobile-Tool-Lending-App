const mongoose = require('mongoose');
require('dotenv').config(); // .env  MONGO_URI

const User = require('./models/user.model'); 

const usuarios = [
  {
    name: 'admin',
    username: 'admin_taller',
    password: 'olaolaola',
    role: 'admin'
  },
  {
    name: 'empleado1',
    username: 'empleado1',
    password: 'ola12345',
    role: 'user'
  }
];

const crearUsuarios = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI, {
      dbName: 'herramientasDB',
    });
    console.log(' Conectado a MongoDB');

    for (const usuario of usuarios) {
      const existente = await User.findOne({ 
        $or: [{ username: usuario.username }, { email: `${usuario.username}@taller.local` }] 
      });

      if (existente) {
        console.log(` Usuario ya existe: ${usuario.username}, se omite.`);
        continue;
      }

      const nuevoUsuario = new User({
        name: usuario.name,
        username: usuario.username,
        email: `${usuario.username}@taller.local`,
        password: usuario.password, 
        role: usuario.role
      });

      await nuevoUsuario.save();
      console.log(`Usuario creado: ${usuario.username}`);
    }

    console.log(' Todos los usuarios fueron importados correctamente.');
    process.exit(0);
  } catch (err) {
    console.error(' Error al importar usuarios:', err.message);
    process.exit(1);
  }
};

crearUsuarios();
