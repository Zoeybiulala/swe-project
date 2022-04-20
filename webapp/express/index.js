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
const {userSchema} = require('./User.js');
const {attemptSchema} = require('./User.js');
const { off } = require('./Recipe.js');
const conn = mongoose.createConnection('mongodb+srv://thdang:PfNuJS36uRbLXFB@cluster0.xiudz.mongodb.net/exploreJournalDb?retryWrites=true');
var Recipe = conn.model('Recipe', require('./Recipe.js'));
var User = conn.model('User', userSchema)
var Attempt = conn.model('Attempt', attemptSchema);

var count = 0;
/*************************************************/
// Endpoints that return HTML 
/*************************************************/

// Display all recipes in recipe database
app.use('/all', (req, res) => {

    // find all the Recipe objects in the database
    Recipe.find({}, (err, recipes) => {

        if (err) {
            res.type('html').status(200);
            console.log('uh oh' + err);
            res.write(err);
        } else {

            // Create title 
            res.type('html');
            res.write("<h1>Recipes</h1>");

            // show all the recipes
            recipes.forEach((recipe) => {
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
    if (!req.query.id) {
        res.type('html').status(200);
        res.write('invalid input');
        res.end();
    }

    //find the recipe in db
    var queryObject = { "_id": req.query.id };
    Recipe.findOne(queryObject, (err, recipe) => {
        console.log(recipe);
        if (err) {
            res.type('html').status(200);
            console.log('uh oh' + err);
            res.write(err);
        } else {
            if (recipe.length == 0) {
                res.type('html').status(200);
                res.write('no recipe for this id');
                res.end();
                return;
            } else {
                res.type('html').status(200);
                res.write('<h1>' + recipe.name + '</h1>');
                res.write('<p>ID:' + recipe._id + '</p>');
                res.write('<p>URL: <a href=<a href=\"' + recipe.url + '\">' +
                    recipe.url + '</a></p>');
                res.write('<p>Description: ' + recipe.description + '</p>');
                //TODO: adding tags and deleting recipe (other usr stories)

                res.write("<br><a href=\"/public/recipeform.html\">[Add a recipe]</a>");

                // adding tags form
                res.write('<p>Tags: ' + recipe.tags);
                res.write("<form action=\"/add_tags\" method=\"post\">");
                res.write("<input name=\"recipe_id\" type=\"hidden\" value=\"" + recipe._id + "\">");
                res.write("Tags <input name=\"tag\">");
                res.write("<input type=\"submit\" value=\"Submit!\">");


                res.write("<a href=\"/delete?recipe_id=" + recipe.recipe_id + "\">[Delete this recipe]<br></a>");
                res.write("<a href=\"/all\">[Go back]</a>");
                res.end();
            }
        }
    })
});

//endpoint to create a new recipe and add it to db 
app.use('/create_recipe', (req, res) => {

    //construct the recipe from request body
    var newRecipe = new Recipe({
        name: req.body.name,
        url: req.body.url,
        description: req.body.description,
    });

    newRecipe.save((err) => {
        if (err) {
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

// end point to add tags to a recipe
app.use('/add_tags', (req, res) => {

    //construct the recipe from request body

    // add tag to a recipe

    // take recipe from request?
    console.log("body!");
    console.log(req.body);
    var recipe_id = req.body.recipe_id;
    var tagsString = req.body.tag;
    // { $push: { scores: { $each: [ 90, 92, 85 ] } } }
    var tagList = tagsString.split(" ");
    var queryObject = { _id: recipe_id };

    Recipe.findOneAndUpdate(queryObject, { $push: { tags: { $each: tagList } } }, { returnDocument: 'after' }, (err, recipe) => {
        if (err) {
            res.type('html').status(200);
            console.log('uh oh' + err);
            res.write(err);
        } else {
            // make sure that tag isn't empty!
            if (!tagsString) {
                res.json({ 'status': 'no tag input!' });
            } else {
                res.write("<h1>Recipe</h1>");
                res.write("<a href=\"/recipe?id=" + recipe_id + "\">");
                res.write("Finish adding tags! Go back to recipe!");
                res.end();
            }
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
        if (err) {
            console.log(err);
            res.json({ "status": "error" });
        } else {
            res.json({ "status": "success", "data": recipes });
        }
    });

});

app.use('/users', (req, res) => {
    User.find({}, (err, users) => {
        if (err) {
            console.log(err);
            res.json({ "status": "error" });
        } else {
            res.json({ "status": "success", "data": users });
        }
    });
});

app.use('/ping', (req, res) => {
    console.log("ping");
    res.json({ "status": "success" })
});

app.use('/checklogin', (req, res) => {

    //no id 
    if (!req.query.id) {
        res.json({ "status": "error" });
    }

    //find the user in db
    var queryObject = { "google_uid": req.query.id };
    User.findOne(queryObject, (err, user) => {
        console.log(user);
        if (err) {
            res.json({ "status": "error" });
        } else {
            if (!user) {
                // Add user to database 
                var newUser = new User({
                    google_uid: req.query.id,
                    saved_recipes: {}
                });
                newUser.save((err) => { if (err) { console.log(err) } });
                res.json({ "status": "success", "action": "user created" });
            } else {
                res.json({ "status": "success", "action": "none" });
            }
        }
    })
});

app.use('/userattempts', (req, res) => {

    //no user id 
    if (!req.query.uid) {
        res.json({ "status": "error" });
        return;
    }

	// no recipe id 
	if (!req.query.rid) {
        res.json({ "status": "error" });
        return;
    }

    //find the user in db
    var queryObject = { "google_uid": req.query.uid };
    User.findOne(queryObject, (err, user) => {
        if (err) {
            res.json({ "status": "error" });
        } else {
            if (!user) {
                res.json({ "status": "error" });
            } else {
				this_recipe_attempts = user.saved_recipes.get(req.query.rid);
				if(this_recipe_attempts){
					res.json({"status":"success", "data":this_recipe_attempts})
				} else {
					res.json({"status":"error"});
				}
            }
        }
    })
});

app.use('/myrecipes', (req, res) => {

    //no id 
    if (!req.query.id) {
        res.json({ "status": "error" });
        return;
    }

    //find the user in db
    var queryObject = { "google_uid": req.query.id };
    User.findOne(queryObject, (err, user) => {
        if (err) {
            res.json({ "status": "error" });
        } else {
            if (!user) {
                res.json({ "status": "error" });
            } else {
                recipeKeys = user.saved_recipes.keys();
                let result = recipeKeys.next();
                var recipeKeyArray = [];

                while (!result.done) {
                    recipeKeyArray.push(result.value);
                    result = recipeKeys.next();
                }

                Recipe.find({ "_id": { $in: recipeKeyArray } }, (err, recipes) => {
                    if (err) {
                        console.log(err);
                    } else {
                        res.json({ "status": "success", "data": recipes });
                    }
                });
            }
        }
    })
})

app.use('/newattempt', (req, res) => {

    // missing parameters 
    if (!req.query.uid || !req.query.rid || !req.query.note || !req.query.rating) {
        res.json({ "status": "error" });
        return;
    }

    if(isNaN(req.query.rating)){
        res.json({ "status": "error" });
        return;
    }

    //find the user in db
    var queryObject = { "google_uid": req.query.uid };
    User.findOne(queryObject, (err, user) => {
        if (err) {
            res.json({ "status": "error" });
        } else {
            if (!user) {
                res.json({ "status": "error" });
            } else {
				this_recipe_attempts = user.saved_recipes.get(req.query.rid);
				if(this_recipe_attempts){
					// Add new attempt 
                    var newAttempt = new Attempt({
                        date: new Date(),
                        rating: req.query.rating,
                        note: req.query.note
                        });
                    this_recipe_attempts.push(newAttempt);
                    console.log(user);
                    User.replaceOne({ _id: user._id}, user, (err,docs)=>{
                        if(err){
                            console.log(err);
                        }
                    });
                    res.json({"status":"success"});
				} else {
					res.json({"status":"error"});
				}
            }
        }
    })
});

/*************************************************/
// Endpoints used for testing 
/*************************************************/

// Clear recipe database and user database 
app.use('/clearDatabase', (req, res) => {
    count = 0;
    Recipe.deleteMany({}, (err) => { if (err) { console.log(err) } });
    User.deleteMany({}, (err) => { if (err) { console.log(err) } });
    res.end();
})

// Create some example recipes and add them to the database
app.use('/addExamples', (req, res) => {
    count++;

    var exampleRecipe = new Recipe({
        recipe_id: count,
        url: "google.com",
        description: "delicious",
        name: "chicken ala google",
        tags: [],
        list_of_users: []
    });
    count++;
    var exampleRecipe2 = new Recipe({
        recipe_id: count,
        url: "google.net",
        description: "bad",
        name: "chicken ala fake google",
        tags: [],
        list_of_users: []
    });

    exampleRecipe.save((err) => { if (err) { console.log(err) } });
    exampleRecipe2.save((err) => { if (err) { console.log(err) } });

    var exampleUser = new User({
        google_uid: "example",
        saved_recipes: {}
    });
    exampleUser.saved_recipes.set(exampleRecipe.id, [{
        date: '2022-03-29',
        rating: 10000,
        note: "I love chicken ala google"
    }]);
    exampleUser.save((err) => { if (err) { console.log(err) } });

    res.end();
})

/*************************************************/

app.use('/public', express.static('public'));
app.use('/', (req, res) => { res.redirect('/all'); });

app.listen(3000, () => {
    console.log('Listening on port 3000');
});