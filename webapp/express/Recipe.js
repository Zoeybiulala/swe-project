var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var recipeSchema = new Schema({
        url: {type: String, required: true, unique: true},
        description: {type: String, required: true},
        name: {type: String, required: true},
        tags: {type: [String], required: false},
        list_of_users : {type: [String], required: false}, // Stores list of unique user string IDs 
    });

module.exports = recipeSchema;
