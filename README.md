# Dispositivos-Moviles
# Backend - Sistema de Control de Herramientas

## URL pública (deploy)
https://api.onrender.com

## Cómo usar la colección de Postman
- Importa el archivo `Gestor de Herramientas API.postman_collection.json` en Postman.
- Importa también el archivo `GestorLocal.postman_environment.json`.
- Configura el environment para usar la URL del backend desplegado.
- Prueba los endpoints antes de integrarlos al frontend.
## Tecnologías
- Node.js + Express: servidor y rutas REST.
- MongoDB + Mongoose: base de datos en la nube (MongoDB Atlas).
- JWT: autenticación con tokens.
- dotenv: manejo de variables de entorno.
- helmet y rate-limit: seguridad.
- multer: manejo de imágenes.
- CORS habilitado para acceso externo ( frontend móvil).

## Scripts
- `npm install` para instalar dependencias
- `npm run dev` → inicia servidor en desarrollo (con `nodemon`)
- `npm start` → inicia servidor en producción (`node server.js`)

## Variables de Entorno (.env)

Crea un archivo `.env` con estas variables:

PORT=3000
MONGO_URI=mongodb+srv://usuario:password@cluster.mongodb.net/db
JWT_SECRET=tu_secreto
JWT_EXPIRES_IN=90d
NODE_ENV=development
CORS_ORIGIN=*
MAX_FILE_UPLOAD=5
FILE_UPLOAD_PATH=./public/uploads

## Endpoints base

Autenticación:
POST /api/v1/auth/register – Crear usuario (solo admin)
POST /api/v1/auth/login – Iniciar sesión

Usuarios (solo admin):
GET /api/v1/users
POST /api/v1/users
PATCH /api/v1/users/:id
DELETE /api/v1/users/:id
POST /api/v1/users/import

Herramientas:
GET /api/v1/tools
POST /api/v1/tools (admin)
GET /api/v1/tools/:id
PATCH /api/v1/tools/:id (admin)
DELETE /api/v1/tools/:id (admin)
GET /api/v1/tools/:id/availability
POST /api/v1/tools/:id/request-loan

Préstamos:
POST /api/v1/loans
GET /api/v1/loans
GET /api/v1/loans/:id
GET /api/v1/loans/overdue
PATCH /api/v1/loans/:id/return
DELETE /api/v1/loans/:id (admin)

## Postman

Para probar la API con Postman:

1. Abre Postman y ve a "Import".  
2. Selecciona el archivo `postman/Herramientas.postman_collection.json` para importar la colección.  
3. Luego importa el archivo `postman/Herramientas.postman_environment.json` para configurar las variables de entorno.  
4. Selecciona el environment importado y prueba los endpoints con la URL base:  
   `https://api-herramientas-qf4v.onrender.com`
