const mysql = require("../mysql").pool;

//procura profissao que profissional exerce
exports.seekProfessions = (req, res, next) => {
  // 'profissao' contém o que profissional tiver pesquisado ou não. ex: AND profissao like '%arquiteto%'
  var profissao = req.body.profissao;
  //se view nulo de o valor de vazio
  if (!profissao) {
    profissao = "";
  }

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "SELECT id, categoria, profissao FROM tbl_tipoServico " +
          "WHERE categoria = ? " +
          profissao +
          " order by profissao asc;",

        [req.body.categoria],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(400).send({
              error: "true",
              msg: error,
            });
          }

          if (result.length < 1) {
            return res.send({
              error: "true",
              msg: "vazio",
            });
          } else {
            return res.status(200).send(result);
          }
        }
      );
    }
  });
};

//registra profissional
exports.registerProfessional = (req, res, next) => {
  const professional = [
    (idUser = req.body.id),
    (idProfissao = req.body.idProfissao),
    (dtExperiencia = req.body.dtExperiencia),
  ];

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "INSERT INTO tbl_profissional (idUsuario, idProfissao, dtExperiencia) " +
          "VALUES(?, ?, ?); ",
        professional,

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(400).send({
              error: "true",
              msg: error,
            });
          }

          if (result.affectedRows > 0) {
            return res.status(201).send({
              error: "false",
              msg: "Success",
              idProfissional: result.insertId,
            });
          } else {
            return res.send({
              error: "true",
              msg: "Erro ao cadastrar profissional.",
            });
          }
        }
      );
    }
  });
};

exports.attDescricao = (req, res, next) => {
  let idProfissional = req.body.idProfissional;
  let desc = req.body.descricao;

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "UPDATE tbl_profissional SET descricao = ? " +
          "WHERE idProfissional = ?;",

        [desc, idProfissional],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(400).send({
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

exports.attFormacao = (req, res, next) => {
  let idProfissional = req.body.idProfissional;
  let formacao = req.body.formacao;

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "UPDATE tbl_profissional SET formacao = ? " +
          "WHERE idProfissional = ?;",
        [formacao, idProfissional],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(400).send({
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

exports.getFotosServices = (req, res, next) => {

  let idProfissional = req.body.idProfissional;  

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "SELECT * FROM tbl_fotosServices " +
          "WHERE idProfissional = ?;",
        [idProfissional],

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(400).send({
              error: "true",
              msg: error,
            });
          }

          console.log(result.length);
          if(result.length < 0){
            return res.status(404).send({
              error: "true",
              msg: "vazio",
            });
          }else {
            return res.status(200).send(result);
          }
        }
      );
    }
  });
};
