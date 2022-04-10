// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// Connect to database and model relevant classes
const mongoose = require('mongoose');
const conn = mongoose.createConnection('mongodb+srv://thdang:PfNuJS36uRbLXFB@cluster0.xiudz.mongodb.net/exploreJournalDb?retryWrites=true');
var Recipe = conn.model('Recipe', require('./Recipe.js'));
var User = conn.model('User', require('./User.js'));

/*************************************************/
// Endpoints that return HTML 
/*************************************************/

// Display all recipes in recipe database
app.use('/all', (req, res) => {
	
	// find all the Recipe objects in the database
	Recipe.find( {}, (err, recipes) => {
		
		if (err) {
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
		}
		
		else {

			// Create title 
			res.type('html');
			res.write("<h1>Recipes</h1>");
	
			// show all the recipes
			recipes.forEach( (recipe) => {
				res.write("<p>");
				res.write("<a href=\"/recipe?id=" + recipe.recipe_id + "\">");
				res.write(recipe.name + " (" + recipe.recipe_id + ")");
				res.write("</a>")
				res.write("</p>");
			});
			res.end();
		}
		}).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
});

/*************************************************/
// Endpoints that return JSON 
/*************************************************/

app.use('/api', (req, res) => {
	Recipe.find({}, (err, recipes) => {
		if(err){
			console.log(err);
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
		} else {
			res.json(recipes);
		}
	});

});

app.use('/users', (req, res) => {
	User.find({}, (err, users) => {
		if(err){
			console.log(err);
			res.type('html').status(200);
			console.log('uh oh' + err);
			res.write(err);
		} else {
			res.json(users);
		}
	});
});

app.use('/ping', (req,res) => {
	console.log("ping"); 
	res.json({"status":"success"})
	});

/*************************************************/
// Endpoints used for testing 
/*************************************************/

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

/*************************************************/

app.use('/public', express.static('public'));
app.use('/', (req, res) => { res.redirect('/all'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
