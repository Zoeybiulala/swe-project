var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var recipeSchema = new Schema({
        recipe_id: {type: Number, required: true, unique: true},
        url: {type: String, required: true, unique: true},
        description: {type: String, required: true},
        name: {type: String, required: true},
        tags: {type: [String], required: true},
        list_of_users : {type: [String], required: true}, // Stores list of unique user string IDs 
    });

module.exports = recipeSchema;
