var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var personSchema = new Schema({
	name: {type: String, required: true, unique: true},
	age: Number
    });
    
module.exports = personSchema;
