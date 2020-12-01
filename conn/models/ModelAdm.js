const mysql = require("../mysql").pool;

// retorna todos usuarios
exports.getUsers = (req, res, next) => {
  mysql.getConnection((error, conn) => {
    if (error) {
      return res.status(400).send({
        error: "true",
        msg: "Error 400. " + error,
      });
    } else {
      conn.query(
        "SELECT * FROM tbl_usuario u " +
        "LEFT OUTER JOIN tbl_profissional p ON u.id = p.idUsuario;",

        (error, result, field) => {
          conn.release();

          if (error) {
            return res.status(500).send({
              error: "true",
              msg: "Error 500. " + error,
            });
          }
          
          return res.status(200).send(result);
        }
      );
    }
  });
};

exports.getPropostas = (req, res, next) => {  

  mysql.getConnection((error, conn) => {
    if (error) {
      console.log(error);
      return res.status(400).send({
        error: "true",
        msg: "Error 400. " + error,
      });
    } else {
      conn.query(
        "SELECT * FROM tbl_proposta;",        

        (error, result, field) => {
          conn.release();

          if (error) {            
            return res.status(500).send({
              error: "true",
              msg: error.sqlMessage,
            });
          }
                    
          if (result.length < 0) {
            return res.status(200).send({
              error: "true",
              msg: "Nenhuma proposta encontrada",
            });
          }

          return res.status(200).send(result);
        }
      );
    }
  });
};