var pg_client = require('pg').Client;

var conString = "postgres://mobilearn:@localhost/mobilearn";
var client = new pg_client(conString);
client.connect();

var rollback = function(client) {
  client.query('ROLLBACK', function() {
    client.end();
  });
};

module.exports = {
	client:client,
	rollback:rollback
}
