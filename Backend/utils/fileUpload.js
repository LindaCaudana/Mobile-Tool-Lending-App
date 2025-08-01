const multer = require('multer');
const path = require('path');
const fs = require('fs');
const AppError = require('./appError');
const env = require('../config/env.config');

// Configuración de Multer
const storage = multer.diskStorage({
  destination: (req, file, cb) => {
    const dir = path.join(__dirname, '../public/uploads/tools');
    if (!fs.existsSync(dir)) fs.mkdirSync(dir, { recursive: true });
    cb(null, dir);
  },
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname);
    cb(null, `tool-${req.user.id}-${Date.now()}${ext}`);
  }
});

const fileFilter = (req, file, cb) => {
  if (['image/jpeg', 'image/png', 'image/webp'].includes(file.mimetype)) {
    cb(null, true);
  } else {
    cb(new AppError('Solo imágenes (JPEG/PNG/WEBP)', 400), false);
  }
};

const upload = multer({
  storage,
  fileFilter,
  limits: { fileSize: env.MAX_FILE_UPLOAD * 1024 * 1024 } // 5MB
});

// Eliminar imagen anterior
exports.deletePreviousImage = async (imagePath) => {
  if (!imagePath) return;
  const fullPath = path.join(__dirname, '../public', imagePath);
  if (fs.existsSync(fullPath)) fs.unlinkSync(fullPath);
};

exports.uploadToolImage = upload.single('image');