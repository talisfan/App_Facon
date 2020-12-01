const mysql = require("../mysql").pool;

exports.criarProposta = (req, res, next) => {
  const dados = [
    (token = req.body.token),
    (prestador = req.body.prestador),
    (idFbPrestador = req.body.idFbPrestador),
    (cliente = req.body.cliente),
    (idFbCliente = req.body.idFbCliente),
    (statusProp = req.body.statusProp),
    (dtInicio = req.body.dtInicio),
    (dtFim = req.body.dtFim),
    (valor = req.body.valor),
    (formaPag = req.body.formaPag),
    (localServ = req.body.localServ),
    (descricao = req.body.descricao),
  ];

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "INSERT INTO tbl_proposta VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?); ",
        dados,

        (error, result, field) => {
          conn.release();

          console.log("Result");
          console.log(result);

          if (error) {
            return res.status(400).send({
              error: "true",
              msg: error.sqlMessage,
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
              msg: "Erro ao cadastrar proposta.",
            });
          }
        }
      );
    }
  });
};

exports.getProposta = (req, res, next) => {
  let token = req.query.token;
  let idFb = req.query.idFb;
  
  mysql.getConnection((error, conn) => {
    if (error) {
      console.log(error);
      return res.status(400).send({
        error: "true",
        msg: "Error 400. " + error,
      });
    } else {
      conn.query(
        "SELECT * FROM tbl_proposta WHERE token = ?;",

        [token],

        (error, result, field) => {
          conn.release();
          
          if (error) {            
            return res.status(500).send({
              error: "true",
              msg: error.sqlMessage,
            });
          }
          
          if (result) {
            if (result[0].idFbCliente != idFb && result[0].idFbPrestador != idFb) {
              return res.status(401).send({
                error: "true",
                msg: "Você não tem acesso há essa proposta.",
              });
            }
          }
          if (result.length < 0) {
            return res.status(200).send({
              error: "true",
              msg: "Proposta não encontrada",
            });
          }

          return res.status(200).send(result[0]);
        }
      );
    }
  });
};

exports.updateProposta = (req, res, next) => {
  let dados = [req.body.statusProp, req.body.token];

  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: error,
      });
    } else {
      conn.query(
        "UPDATE tbl_proposta SET statusProp = ? WHERE token = ?;",

        dados,

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
