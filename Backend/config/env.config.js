const { config } = require('dotenv');
const { bool, cleanEnv, num, str } = require('envalid');

config({ path: `${__dirname}/../.env` }); 

module.exports = cleanEnv(process.env, {
  NODE_ENV: str({
    choices: ['development', 'test', 'production'],
    default: 'development',
    desc: 'Entorno de ejecución'
  }),
  
  PORT: num({
    default: 3000,
    desc: 'Puerto del servidor'
  }),

  // Base de datos
MONGO_URI: str({
  default: 'mongodb://localhost:27017/tool_manager',
  desc: 'URI de conexión a MongoDB',
  example: 'mongodb+srv://user:pass@cluster.mongodb.net/db',
  validate: (value) => {
    if (!value.match(/^mongodb(\+srv)?:\/\/.+/)) {
      throw new Error('La URI de MongoDB no tiene un formato válido');
    }
  }
}),

MONGO_CONNECT_TIMEOUT: num({
    default: 5000, 
    desc: 'Timeout de conexión a MongoDB (ms)'
  }),

  // Autenticación JWT
  JWT_SECRET: str({
    default: 'secret-key-dev-change-in-prod',
    desc: 'Clave secreta para JWT'
  }),
  
  JWT_EXPIRES_IN: str({
    default: '90d',
    desc: 'Duración del token JWT'
  }),

  // Sistema de archivos
  FILE_UPLOAD_PATH: str({
    default: './public/uploads',
    desc: 'Ruta base para subida de archivos'
  }),

  MAX_FILE_UPLOAD: num({
    default: 5,
    desc: 'Tamaño máximo de archivo en MB'
  }),

  // Configuración de email (no la habilite porque el inge dijo que no era obligatorio)
  EMAIL_ENABLED: bool({
    default: false,
    desc: 'Habilitar envío de emails'
  }),

  SMTP_HOST: str({
    default: '',
    desc: 'Servidor SMTP'
  }),

  SMTP_PORT: num({
    default: 587,
    desc: 'Puerto SMTP'
  }),

  SMTP_USER: str({
    default: '',
    desc: 'Usuario SMTP'
  }),

  SMTP_PASS: str({
    default: '',
    desc: 'Contraseña SMTP'
  })
});