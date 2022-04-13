//formatting purposes, maybe later
var ejs = require('ejs');

// set up Express
var express = require('express');
var app = express();

// set up BodyParser
var bodyParser = require('body-parser');
app.use(bodyParser.urlencoded({ extended: true }));

// Connect to database and model relevant classes
const mongoose = require('mongoose');
const conn = mongoose.createConnection('mongodb+srv://rmalusa:xwrFY3DZa5BWpirA@cluster0.xiudz.mongodb.net/exploreJournalDb?retryWrites=true&w=majority');
var Recipe = conn.model('Recipe', require('./Recipe.js'));
var User = conn.model('User', require('./User.js'));

var count = 0;

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
				res.write("<a href=\"/recipe?id=" + recipe._id + "\">");
				res.write(recipe.name + " (" + recipe._id + ")");
				res.write("</a>");
				res.write("</p>");
			});
			res.write("<br><a href=\"/public/recipeform.html\">[Add a recipe]</a>");
			res.end();
		}
		}).sort({ 'name': 'asc' }); // this sorts them BEFORE rendering the results
}); 

//endpoint to view specific recipe
app.use('/recipe', (req, res) => {
	//no id 
	if(!req.query.id) {
		res.type('html').status(200);
		res.write('invalid input');
		res.end();
	}

	//find the recipe in db
	var queryObject = {"_id" : req.query.id};
	Recipe.findOne( queryObject, (err, recipe) => {
		console.log(recipe);
		if(err){
			res.type('html').status(200);
		    console.log('uh oh' + err);
		    res.write(err);
		} else {
			if( recipe.length == 0) {
				res.type('html').status(200);
				res.write('no recipe for this id');
				res.end();
				return;
			} else {
				res.type('html').status(200);
				res.write('<h1>' + recipe.name + '</h1>');
				res.write('<p>ID:' + recipe._id + '</p>');
				res.write('<p>URL: <a href=<a href=\"' + recipe.url + '\">'
							+ recipe.url+'</a></p>');
				res.write('<p>Description: ' + recipe.description + '</p>');
				//TODO: adding tags and deleting recipe (other usr stories)
				res.write('<p>Tags: ' + recipe.tags + 
						'<a href=\"/add_tags\">[add tags]</a></p>');
				res.write("<a href=\"/delete?recipe_id=" + recipe.recipe_id + "\">[Delete this recipe]<br></a>");
				res.write("<a href=\"/all\">[Go back]</a>");
				res.end();
			}
		}
	})
});

//endpoint to create a new recipe and add it to db 
app.use('/create_recipe', (req,res) => {

	//construct the recipe from request body
	var newRecipe = new Recipe ( {
		name: req.body.name,
		url: req.body.url,
		description: req.body.description,
	});
	
	newRecipe.save( (err) => {
		if(err) {
			res.type('html').status(200);
		    res.write('uh oh: ' + err);
		    console.log(err);
		    res.end();
		} else {
			res.type('html').status(200);
			res.write('<p>successfully added ' + newRecipe.name + ' to the database</p>');
			res.write("<a href=\"/all\">[Go back]</a>");
			res.end();
			console.log(count);
		}
	});
});


//possibily implementing a error endpoint ?
// app.use('/err', (req, res) => {
// 	res.send('uh oh');
// })

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

app.use('/checklogin', (req, res) => {
	
	console.log("checking login");
	
	//no id 
	if(!req.query.id) {
		res.json({"status":"invalid parameters"});
	}

	console.log(req.query.id);

	//find the user in db
	var queryObject = {"google_uid" : req.query.id};
	User.findOne( queryObject, (err, user) => {
		console.log(user);
		if(err){
			res.json({"status":"error"});
		} else {
			if(!user) {
				console.log("Adding new user");
				// Add user to database 
				var newUser = new User({
					google_uid: req.query.id,
					saved_recipes: {}
					});
				newUser.save((err)=>{if(err){console.log(err)}});
				res.json({"status":"user created"});
			} else {
				console.log("User already exists");
				res.json({"status":"user exists"});
			}
		}
	})	
})

app.use('/myrecipes', (req, res) => {
	
	//no id 
	if(!req.query.id) {
		res.json({"status":"error"});
		return;
	}

	//find the user in db
	var queryObject = {"google_uid" : req.query.id};
	User.findOne( queryObject, (err, user) => {
		if(err){
			res.json({"status":"error"});
		} else {
			if(!user) {
				res.json({"status":"error"});
			} else {
				recipeKeys = user.saved_recipes.keys();
				let result = recipeKeys.next();
				var recipeKeyArray = [];

				while (!result.done) {
					recipeKeyArray.push(result.value);
					result = recipeKeys.next();
				}

				Recipe.find({"_id":{ $in: recipeKeyArray}}, (err, recipes) => {
					if(err){	
						console.log(err);
					} else {
						res.json({"status":"success", "data":recipes});
					}
				});
			}
		}
	})	
})

/*************************************************/
// Endpoints used for testing 
/*************************************************/

// Clear recipe database and user database 
app.use('/clearDatabase', (req, res) => {
	count = 0;
	Recipe.deleteMany({}, (err)=>{if(err){console.log(err)}});
	User.deleteMany({}, (err)=>{if(err){console.log(err)}});
	res.end();
})

// Create some example recipes and users and add them to the database
app.use('/addExamples', (req, res) =>{
	count ++;

	var exampleRecipe = new Recipe ({
		recipe_id: count,
		url: "google.com",
		description: "delicious",
		name: "chicken ala google",
		tags: [],
		list_of_users : []
	});
	var exampleRecipe2 = new Recipe ({
		url: "google.net",
		description: "bad",
		name: "chicken ala fake google",
		tags: [],
		list_of_users : []
	});

	exampleRecipe.save((err)=>{if(err){console.log(err)}}); 
	exampleRecipe2.save((err)=>{if(err){console.log(err)}});

	var exampleUser = new User({
		google_uid: "example",
		saved_recipes: {}
		});
	exampleUser.saved_recipes.set(exampleRecipe.id, [{
		date: '2022-03-29',
		rating: 10000,
		note: "I love chicken ala google"
		}]);
	exampleUser.save((err)=>{if(err){console.log(err)}});

	// To do: add this user to list of users associated with recipe it owns

	res.end();
})

/*************************************************/

app.use('/public', express.static('public'));
app.use('/', (req, res) => { res.redirect('/all'); } );

app.listen(3000,  () => {
	console.log('Listening on port 3000');
    });
