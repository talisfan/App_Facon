const mysql = require('mysql');

var pool = mysql.createPool({
    "user": "root",
    "password" : "",
    "database": "Facon_DataBase",
    "host": "127.0.0.1",
    "port": 3307
});

exports.pool = pool;