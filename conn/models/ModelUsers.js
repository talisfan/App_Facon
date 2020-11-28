const mysql = require("../mysql").pool;

//----------- Retorna Usuário Credenciado

exports.login = (req, res, next) => {
  const email = req.body.email;
  // const senha = req.body.senha;

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error
      });
    } else {
      conn.query(
        "SELECT u.id, u.nome, u.email, u.ativo, u.endBairro, u.endCep, u.endCidade, u.foto, " +
          "u.endEstado, u.endNum, u.endRua, u.dtNascimento, u.telCell, u.telFixo, u.email, u.idFb, " +
          "p.idProfissional, p.descricao, p.dtExperiencia, p.formacao, p.idProfissao, " +
          "s.categoria, s.profissao, " +
          "COUNT(a.id) AS qntAv, ROUND(AVG(a.estrelas)) AS estrelas " +
          "FROM tbl_usuario u " +
          "LEFT OUTER JOIN tbl_profissional p ON u.id = p.idUsuario " +
          "LEFT OUTER JOIN tbl_avaliacao a ON p.idProfissional = a.idProfissional " +
          "LEFT OUTER JOIN tbl_tipoServico s ON s.id = p.idProfissao " +
          "WHERE email = ? " +
          "GROUP BY u.id ;",

        //parametros
        [email],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(500).send({
              error: "true",
              msg: error
            });
          }
          //console.log(email);
          if (result.length < 1) {
            return res.send({
              error: "true",
              msg: "Credenciais inválidas.",
            });
          }

          //destruct de array result (usuario)
          const [response] = result;

          return res.status(200).send(response);
        }
      );
    }
  });
};

//----------- Cadastro de Usuário

exports.registerUser = (req, res, next) => {
  const dados = [
    (nome = req.body.nome),
    (email = req.body.email),
    (telCell = req.body.telCell),
    (telFixo = req.body.telFixo),
    (cpf = req.body.cpf),
    (rg = req.body.rg),
    (dtNascimento = req.body.dtNascimento),
    (senha = req.body.senha),
    (idFb = req.body.idFb)
  ];

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(500).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "SELECT * FROM tbl_usuario " +
          "WHERE email = ? OR cpf = ? OR telCell = ?;",

        [req.body.email, req.body.cpf, req.body.telCell],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(500).send({
              error: "true",
              msg: error,
            });
          }

          if (result.length > 0) {
            return res.status(200).send({
              error: "true",
              msg: "Email, CPF ou Telefone Celular já cadastrado !",
            });
          } else {
            mysql.getConnection((error, conn) => {
              if (error) {
                return res.status(500).send({
                  error: "true",
                  msg: error,
                });
              }
              conn.query(
                "INSERT INTO tbl_usuario (nome, email, telCell, telFixo, cpf, rg, dtNascimento, senha, idFb) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);",

                dados,

                (error, result, field) => {
                  conn.release();

                  if (error) {
                    return res.status(500).send({
                      error: "true",
                      msg: error,
                    });
                  }

                  if (result.affectedRows > 0) {
                    return res.status(201).send({
                      error: "false",
                      msg: "Success",
                    });
                  } else {
                    return res.send({
                      error: "true",
                      msg: "Erro ao inserir usuário.",
                    });
                  }
                }
              );
            });
          }
        }
      );
    }
  });
};

//----------- Completar Cadastro

exports.completCad = (req, res, next) => {
  const dados = [
    (endCidade = req.body.endCidade),
    (endCep = req.body.endCep),
    (endBairro = req.body.endBairro),
    (endNum = req.body.endNum),
    (endEstado = req.body.endEstado),
    (endRua = req.body.endRua),
    (foto = req.body.foto),
    (id = req.body.id),
  ];

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(500).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "UPDATE tbl_usuario SET endCidade = ?, endCep = ?, endBairro = ?, endNum = ?, " +
          "endEstado = ?, endRua = ?, foto = ? WHERE id = ?;",

        dados,

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(500).send({
              error: "true",
              msg: error,
            });
          }

          if (result.changedRows == 0) {
            return res.send({
              error: "true",
              msg: "Nenhum registro alterado.",
            });
          } else {
            return res.status(200).send({
              error: "false",
              msg: "Success",
            });
          }
        }
      );
    }
  });
};

//----------- Retorna todos Profissionais para o Cliente
//              Por pesquisa ou categoria

exports.seekProfessionals = (req, res, next) => {
  const categoria = req.body.categoria;
  const profissao = req.body.profissao;
  var pesquisa = "";

  if (categoria == "vazio") {
    pesquisa = profissao;
  } else {
    pesquisa = categoria;
  }

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(500).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "SELECT p.idProfissional, p.idUsuario, p.dtExperiencia, p.descricao, " +
          "u.id, u.nome, u.endCidade, u.endEstado, u.dtNascimento, u.ativo, u.idFb, u.foto," +
          "s.profissao, s.categoria, " +
          "ROUND(AVG(a.estrelas)) AS estrelas, COUNT(a.id) AS qntAv " +
          "FROM tbl_profissional p " +
          "INNER JOIN tbl_usuario u ON (u.id = p.idUsuario) " +
          "INNER JOIN tbl_tipoServico s ON (s.id = p.idProfissao) " +
          "LEFT OUTER JOIN tbl_avaliacao a on p.idProfissional = a.idProfissional " +
          pesquisa +
          ";", // Exemplo ("WHERE s.categoria = '"+ categoria +"' AND u.id <> " + idUser + " AND u.ativo = 1 group by p.idProfissional order by estrelas desc")

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(500).send({
              error: "true",
              msg: error,
            });
          }

          if (result.length < 1) {
            return res.send([
              {
                error: "true",
                msg: "vazio",
              },
            ]);
          } else {
            return res.status(200).send(result);
          }
        }
      );
    }
  });
};
