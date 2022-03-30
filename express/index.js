// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// Connect to personDatabase and model relevant classes
// (This won't be used in our project, it's just to show the use of multiple databases)
// source: https://mongoosejs.com/docs/connections.html
const mongoose = require('mongoose');
const conn = mongoose.createConnection('mongodb://localhost:27018/personDatabase');
var Person = conn.model('Person', require('./Person.js'));

// Connect to recipeDatabase and model relevant classes 
const conn2 = mongoose.createConnection('mongodb://localhost:27018/recipeDatabase');
var Recipe = conn2.model('Recipe', require('./Recipe.js'));

/***************************************/

// Code that tests creating and saving recipe objects in a database 

// Clear recipe database 
console.log(Recipe.deleteMany());

// Create new recipes
var exampleRecipe = new Recipe ({
	recipe_id: 1,
	url: "google.com",
	description: "delicious",
	name: "chicken ala google",
	tags: [],
	list_of_users : []
});

var exampleRecipe2 = new Recipe ({
	recipe_id: 2,
	url: "google.net",
	description: "bad",
	name: "chicken ala fake google",
	tags: [],
	list_of_users : []
});

// save the recipes to the database
exampleRecipe.save(); 
exampleRecipe2.save();

/****************************************/

app.use('/recipes', (req, res) => {
	// find all the Recipe objects in the (recipe) database
	Recipe.find( {}, (err, recipes) => {
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
		}
		else {
			if (recipes.length == 0) {
			res.type('html').status(200);
			res.write('There are no recipes');
			res.end();
			return;
			}
			else {
			res.type('html').status(200);
			res.write('Here are the recipes in the database:');
			res.write('<ul>');
			// show all the recipes
			recipes.forEach( (recipe) => {
				res.write('<li>');
				res.write('Name: ' + recipe.name + '; ID: ' + recipe.recipe_id);
				res.write('</li>');
						
			});
			res.write('</ul>');
			res.end();
			}
		}
		}).sort({ 'ID': 'asc' }); // this sorts them BEFORE rendering the results
});

/*************************************************/

// Person endpoints will be deleted eventually
app.use('/public', express.static('public'));
app.use('/', (req, res) => { res.redirect('/public/personform.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
