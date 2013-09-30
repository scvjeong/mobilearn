var client = require('../sql/pgsql_server').client;
var rollback = require('../sql/pgsql_server').rollback;
var EventEmitter = require('events').EventEmitter;
var check = require('validator').check,
    sanitize = require('validator').sanitize;

exports.auth_for_token = function(req, res) {
	var req_header = req.headers.authorization.split(" ");
	var auth_token = req_header[1];
	var sql = "SELECT * FROM member WHERE email = $1";
	console.log(auth_token);
	var query = client.query(sql, [auth_token]);
	var info = [];
	query.on('row', function(row){
		info.push(row);
	});
	query.on('end', function(result){
		result = { info:info };
		res.send(result);
	});
	query.on('error', function(){
		result = { info:{} };
		res.send(result);
	});
};

exports.request_token = function(req, res) {
	var email = req.param("email");
	var passwd = req.param("passwd");

	var sql = "SELECT * FROM member WHERE email = $1 AND passwd = $2";

	var query = client.query(sql, [email, passwd]);
	var is_ture = false;
	query.on('row', function(row){
		if(row.email)
			is_ture = true;
		else
			is_ture = false;
	});
	query.on('end', function(result){
		if(is_ture) {
			require('crypto').randomBytes(48, function(ex, buf) {
				token = buf.toString('hex');
				sql = "UPDATE member SET token = $1 WHERE email = $2";
				var update_query = client.query(sql, [token, email], function(err, result) { 
					if(err) return;
					if(result.rowCount>0) {
						result = { token:token };
						res.send(result);
					}
					else {
						result = { token:{} };
						res.send(result);
					}
				});
			});
		}
		else {
			result = { token:{} };
			res.send(result);
		}
	});
	query.on('error', function(){
		result = { token:{} };
		res.send(result);
	});
};

