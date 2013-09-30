var client = require('../sql/pgsql_server').client;
var rollback = require('../sql/pgsql_server').rollback;
var EventEmitter = require('events').EventEmitter;
var check = require('validator').check,
    sanitize = require('validator').sanitize;

exports.question = function(req, res) {
	var question_id = req.params.question_id;
	var query = client.query('SELECT * FROM library WHERE library.oid = $1', [question_id]);
	var question = [];
	query.on('row', function(row){
		question.push(row);
	});
	query.on('end', function(result){
		result = { question:question };
		res.send(result);
	});
	query.on('error', function(){
		result = { question:{} };
		res.send(result);
	});
};

exports.library = function(req, res) {
	var content_id = req.params.content_id;
	if( content_id ) {
		var sql = "SELECT ";
		sql += "library.oid, ";
		sql += "library.name, ";
		sql += "library.type, ";
		sql += "library.thumbnail_url, ";
		sql += "to_char(library.update_date, 'YYYYMMDDHH24MISS') AS update_date ";
		sql += "FROM library ";
		sql += "WHERE library.oid = $1 ";
		sql += "ORDER BY library.oid DESC";
		var query = client.query(sql, [content_id]);
	} else {
		var sql = "SELECT ";
		sql += "library.oid, ";
		sql += "library.name, ";
		sql += "library.type, ";
		sql += "library.thumbnail_url, ";
		sql += "to_char(library.update_date, 'YYYYMMDDHH24MISS') AS update_date ";
		sql += "FROM library ";
		sql += "ORDER BY library.oid DESC";
		var query = client.query(sql);
	}
	
	var library = [];
	query.on('row', function(row){
		library.push(row);
	});
	query.on('end', function(result){
		result = { library:library };
		res.send(result);
	});
	query.on('error', function(){
		result = { library:{} };
		res.send(result);
	});
};

exports.library_question = function(req, res) {
	var content_id = req.params.content_id;
	var question_list = [];
	var sql = "SELECT ";
	sql += "question.oid, ";
	sql += "question.question, ";
	sql += "question.oid_library, ";
	sql += "question.score ";
	sql += "FROM question ";
	sql += "INNER JOIN library ";
	sql += "ON question.oid_library = library.oid ";
	sql += "WHERE library.oid = $1 ";
	sql += "ORDER BY question.question DESC";

	var query = client.query(sql, [content_id]);
	query.on('row', function(row){
		question_list.push(row);
	});
	query.on('end', function(result){
		result = { question_list:question_list };
		res.send(result);
	});
	query.on('error', function(){
		result = { question_list:{} };
		res.send(result);
	});
};

exports.library_answer = function(req, res) {
	var content_id = req.params.content_id;
	var answers = [];
	var sql = "SELECT ";
	sql += "answers.oid, ";
	sql += "answers.reply, ";
	sql += "answers.answer, ";
	sql += "answers.oid_question ";
	sql += "FROM question ";
	sql += "INNER JOIN library ";
	sql += "ON question.oid_library = library.oid ";
	sql += "INNER JOIN answers ";
	sql += "ON question.oid = answers.oid_question ";
	sql += "WHERE library.oid = $1 ";
	sql += "ORDER BY question.question DESC";

	var query = client.query(sql, [content_id]);
	query.on('row', function(row){
		answers.push(row);
	});
	query.on('end', function(result){
		result = { answers:answers };
		res.send(result);
	});
	query.on('error', function(){
		result = { answers:{} };
		res.send(result);
	});
};

exports.library_update = function(req, res) {
	var content_id = req.params.content_id;
	var update_date = [];
	var sql = "SELECT ";
	sql += "library.update_date ";
	sql += "FROM library ";
	sql += "WHERE library.oid = $1 ";

	var query = client.query(sql, [content_id]);
	query.on('row', function(row){
		update_date.push(row);
	});
	query.on('end', function(result){
		result = { content_update_date:update_date };
		res.send(result);
	});
	query.on('error', function(){
		result = { content_update_date:{} };
		res.send(result);
	});
};

exports.market_contents = function(req, res) {
	
	var sql = "SELECT ";
	sql += "market.oid, ";
	sql += "market.content_name, ";
	sql += "market.content_version, ";
	sql += "market.thumbnail_url, ";
	sql += "market.price, ";
	sql += "to_char(market.update_date, 'YYYYMMDDHH24MISS') AS update_date, ";
	sql += "member.nickname ";
	sql += "FROM market ";
	sql += "INNER JOIN member ";
	sql += "ON member.oid = market.oid_member ";
	sql += "ORDER BY market.oid DESC";
	var query = client.query(sql);

	var market_contents = [];
	query.on('row', function(row){
		market_contents.push(row);
	});
	query.on('end', function(result){
		result = { market_contents:market_contents };
		res.send(result);
	});
	query.on('error', function(){
		result = { market_contents:{} };
		res.send(result);
	});
};

exports.market_content = function(req, res) {
	var oid_market = req.params.oid_market;
	var sql = "SELECT ";
	sql += "market.oid, ";
	sql += "market.content_name, ";
	sql += "market.content_version, ";
	sql += "market.thumbnail_url, ";
	sql += "market.price, ";
	sql += "to_char(market.update_date, 'YYYYMMDDHH24MISS') AS update_date, ";
	sql += "member.nickname ";
	sql += "FROM market ";
	sql += "INNER JOIN member ";
	sql += "ON member.oid = market.oid_member ";
	sql += "WHERE market.oid = " + oid_market;
	var query = client.query(sql);

	var market_content = [];
	var questions = [];
	query.on('row', function(row){
		market_content.push(row);
	});
	query.on('end', function(result){
		if( market_content.length > 0 ) {
			var sql = "SELECT ";
			sql += "question.question, ";
			sql += "question.oid_library, ";
			sql += "question.score, ";
			sql += "answers.oid as oid_answer, ";
			sql += "answers.reply, ";
			sql += "answers.answer, ";
			sql += "answers.oid_question ";
			sql += "FROM question ";
			sql += "INNER JOIN answers ";
			sql += "ON question.oid = answers.oid_question ";
			sql += "WHERE question.oid_library = " + market_content[0].oid + " ";
			sql += "ORDER BY question.question ASC ";

			var query_second = client.query(sql);
			query_second.on('row', function(row){
				questions.push(row);
			});
			query_second.on('end', function(result){
				result = { market_content:market_content, questions:questions };
				res.send(result);
			});
			query_second.on('error', function(result){
				result = { market_content:{} };
				res.send(result);
			});
		}
		else {
			result = { market_content:{} };
			res.send(result);
		}
		//var query = client.query(sql);
		//result = { market_contents:market_contents };
	});
	query.on('error', function(){
		result = { market_content:{} };
		res.send(result);
	});
};

/*
client.query('BEGIN', function(err, result) {
	if(err) return rollback(client);
	client.query('INSERT INTO account(money) VALUES(100) WHERE id = $1', [1], function(err, result) {
		if(err) return rollback(client);
		client.query('INSERT INTO account(money) VALUES(-100) WHERE id = $1', [2], function(err, result) {
			if(err) return rollback(client);
				//disconnect after successful commit
			client.query('COMMIT', client.end.bind(client));
		});
	});
});
*/
