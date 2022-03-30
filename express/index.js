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

// Connect to userDatabase and model relevant classes 
const conn3 = mongoose.createConnection('mongodb://localhost:27018/userDatabase');
var User = conn3.model('User', require('./User.js'));

// Clear recipe database and user database 
app.use('/clearDatabase', (req, res) => {
	Recipe.deleteMany({}, (err)=>{if(err){console.log(err)}});
	User.deleteMany({}, (err)=>{if(err){console.log(err)}});
	res.end();
})

// Create some example recipes and add them to the database
app.use('/addExamples', (req, res) =>{

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

	exampleRecipe.save((err)=>{if(err){console.log(err)}}); 
	exampleRecipe2.save((err)=>{if(err){console.log(err)}});

	var exampleUser = new User({
		User_id: "ruby",
		Email: "rmalusa@bmc",
		Saved_recipes:  {
			"1":[{
					date: '2022-03-29',
					rating: 10000,
					note: "I love chicken ala google"
				}],
			"2":[]
			}
		});
	exampleUser.save((err)=>{if(err){console.log(err)}});

	res.end();
})

// Display all recipes in recipe database
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

// Person-related endpoints will be deleted eventually
app.use('/public', express.static('public'));
app.use('/', (req, res) => { res.redirect('/public/personform.html'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
