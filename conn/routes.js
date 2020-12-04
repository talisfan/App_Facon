const express = require('express');
const router = express.Router();

const modelUsers = require('./models/ModelUsers');
const modelProfessionals = require('./models/ModelProfessionals');
const modelAdm = require('./models/ModelAdm');
const modelProposta = require('./models/ModelProposta');

//////ROTAS//////

// Model Users
router.post('/users/login', modelUsers.login);
router.post('/users/registerUser', modelUsers.registerUser);
router.post('/users/seekProfessionals', modelUsers.seekProfessionals);
router.put('/users/completCad', modelUsers.completCad);
router.put('/users/attContato', modelUsers.attContato);
router.put('/users/attEndereco', modelUsers.attEndereco);

// Model Professionals
router.post('/professionals/seekProfessions', modelProfessionals.seekProfessions);
router.post('/professionals/registerProfessional', modelProfessionals.registerProfessional);
router.post('/professionals/insertFoto', modelProfessionals.insertFoto);
router.post('/professionals/deleteFoto', modelProfessionals.deleteFoto);
router.put('/professionals/attDescricao', modelProfessionals.attDescricao);
router.put('/professionals/attFormacao', modelProfessionals.attFormacao);
router.get('/professionals/fotos', modelProfessionals.getFotosServices);
router.get('/professionals/infosPro', modelProfessionals.getInfosPro);

// Model Propostas
router.get('/proposta', modelProposta.getProposta);
router.post('/proposta', modelProposta.criarProposta);
router.put('/proposta', modelProposta.updateProposta);

// Model ADM
router.get('/users', modelAdm.getUsers);
router.get('/propostas', modelAdm.getPropostas);

module.exports = router;