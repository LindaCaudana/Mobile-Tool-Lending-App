const mongoose = require('mongoose');

const loanSchema = new mongoose.Schema({
  tool: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Tool',
    required: [true, 'Un préstamo debe estar asociado a una herramienta']
  },
  user: {
    type: mongoose.Schema.Types.ObjectId,
    ref: 'User',
    required: [true, 'Un préstamo debe estar asociado a un usuario']
  },
  startDate: {
    type: Date,
    default: Date.now
  },
  endDate: Date,
status: {
  type: String,
  enum: ['activo', 'devuelto', 'vencido'], 
  default: 'activo'
},
  notes: String
}, {
  timestamps: true,
  toJSON: { virtuals: true },
  toObject: { virtuals: true }
});

// Índices 
loanSchema.index({ tool: 1, user: 1 });

const Loan = mongoose.model('Loan', loanSchema);

module.exports = Loan;