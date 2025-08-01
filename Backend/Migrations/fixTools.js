const mongoose = require('mongoose');
const Tool = require('../models/tool.model');

async function fixMissingCurrentLoan() {
  try {
    // Usa tu cadena de conexión de Atlas
    const atlasUri = 'mongodb+srv://admin_taller:ola.mongodb.net/herramientasDB?retryWrites=true&w=majority&appName=Cluster0';

    console.log(' Conectando a MongoDB Atlas...');
    await mongoose.connect(atlasUri, {
      serverSelectionTimeoutMS: 10000 // 10 segundos para conexión lenta
    });

    console.log(' Conexión exitosa a Atlas');
    console.log(' Buscando herramientas sin currentLoan...');

    const result = await Tool.updateMany(
      { currentLoan: { $exists: false } },
      { $set: { currentLoan: null } }
    );

    console.log(` Documentos actualizados: ${result.modifiedCount}`);
    console.log(' Migración completada');
    process.exit(0);

  } catch (error) {
    console.error(' Error:', error.message);
    console.log('Posibles soluciones:');
    console.log('1. Verifica tu cadena de conexión de Atlas');
    console.log('2. Asegúrate de que tu IP esté en la lista de permitidos en Atlas');
    console.log('3. Revisa que el usuario tenga permisos de escritura');
    process.exit(1);
  }
}

fixMissingCurrentLoan();
