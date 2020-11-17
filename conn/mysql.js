const mysql = require('mysql');

var pool = mysql.createPool({
    "user": "facon_database",
    "password" : "123456789",
    "database": "facon_database",
    "host": "db4free.net",
    "port": 3306
});

exports.pool = pool;