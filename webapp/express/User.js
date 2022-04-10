var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var attemptSchema = new Schema({
    date: {type: Date, required: true},
    rating: {type: Number, required: true},
    note: {type: String}
    });

var userSchema = new Schema({
    google_uid: {type: String, required: true, unique: true},
    Saved_recipes:  {
       type: Map,
       of: [attemptSchema],
       required: true
        }
    });
    
module.exports = userSchema;
