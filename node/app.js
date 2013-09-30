var express = require('express')
  , res = require('./routes/res')
  , auth = require('./routes/auth')
  , http = require('http')
  , path = require('path')
  , expressValidator = require('express-validator');

var app = express();

var _upload_dir = 'tmp';

var validator_option = {
	errorFormatter: function(param, msg, value) {
		var namespace = param.split('.')
		, root    = namespace.shift()
		, formParam = root;

		while(namespace.length) {
			formParam += '[' + namespace.shift() + ']';
		}
		return {
			param : formParam,
			target : formParam,
			msg   : msg,
			value : value,
			result : "failed"
		};
	}
}

app.configure(function(){
	app.set('port', process.env.PORT || 3000);
	app.set('views', __dirname + '/views');
	app.set('view engine', 'ejs');
	app.use(express.favicon());
	app.use(express.logger('dev'));
	app.use(express.bodyParser());
	app.use(expressValidator(validator_option));
	app.use(express.methodOverride());
	app.use(express.cookieParser('keyboard cat'));
	app.use(express.session());
	app.use(app.router);
	app.use(require('stylus').middleware(__dirname + '/public'));
	app.use(express.static(path.join(__dirname, 'public')));
	app.use('/' + _upload_dir, express.static(path.join(__dirname, _upload_dir)));	// 업로드 디렉토리 static 등재
});

app.configure('development', function(){
  app.use(express.errorHandler());
});

/* mobilearn API start */

// 문제
app.get('/res/question', res.question);
app.get('/res/question/:library_id', res.question);
// 콘텐츠에 속한 문제 리스트
app.get('/res/library/question/:content_id', res.library_question);
// 콘텐츠에 속한 문제 리스트 답
app.get('/res/library/answer/:content_id', res.library_answer);
// 콘텐츠 업데이트 날짜
app.get('/res/library/update/:content_id', res.library_update);
// 콘텐츠 리스트
app.get('/res/library', res.library);
// 콘텐츠 정보
app.get('/res/library/:content_id', res.library);

// 인증 토큰 요청  
app.get('/auth/request_token/', auth.request_token);


/* market start*/
app.get('/res/market/contents/', res.market_contents);
app.get('/res/market/contents/:oid_market', res.market_content);
/* market end*/

/* mobilearn API end */

http.createServer(app).listen(app.get('port'), function(){
  console.log("Express server is started to listen on port " + app.get('port'));
});