const express = require('express');
const router = express.Router();

const modelUsers = require('./models/ModelUsers');
const modelProfessionals = require('./models/ModelProfessionals');
const modelAdm = require('./models/ModelAdm');

//////ROTAS//////

// Model Users
router.post('/users/login', modelUsers.login);
router.post('/users/registerUser', modelUsers.registerUser);
router.post('/users/seekProfessionals', modelUsers.seekProfessionals);
router.put('/users/completCad', modelUsers.completCad);

// Model Professionals
router.post('/professionals/seekProfessions', modelProfessionals.seekProfessions);
router.post('/professionals/registerProfessional', modelProfessionals.registerProfessional);
router.put('/professionals/attDescricao', modelProfessionals.attDescricao);
router.put('/professionals/attFormacao', modelProfessionals.attFormacao);
router.put('/professionals/attContato', modelProfessionals.attContato);
router.put('/professionals/attEndereco', modelProfessionals.attEndereco);

// Model ADM
router.get('/users', modelAdm.getUsers);

module.exports = router;