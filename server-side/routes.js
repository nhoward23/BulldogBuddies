var express = require('express');
var apiRoutes = express.Router();
var User   = require('./models/user'); 
var jwt    = require('jsonwebtoken'); 
var config = require('./config');
var mongoose    = require('mongoose');
var app         = express();
mongoose.connect(config.database); //Crossing my fingers here
app.set('gina_secret', config.secret); 


//////////////////////////////
// Check Requets, Requested, and Pending
// GET YOUR FRIEND
//////////////////////////////


//////////////////////////////
// FRIENDSHIP SCHEMA
// GET YOUR FRIEND
//////////////////////////////
apiRoutes.get('/friends/',function(req, res){
    var token = req.headers['x-access-token'];    
    if(token){
        jwt.verify(token, app.get('gina_secret'), function(err, decoded) {  
            if(err){
                return res.json({ success: false, 
                    message: 'Failed to authenticate token.' 
                });   
            } 
                    User.getFriends(decoded.user, function (err, friendships) {
                    if(err)
                        throw err;
                    return res.json({
                        array_friends: friendships})
                  });      
        });    
    } 
    else{
        return res.json({ success: false, 
            message: 'No Token' 
        });   
    }
})

//////////////////////////////
// SEND REQUEST FRIEND
// GET YOUR FRIEND
//////////////////////////////
apiRoutes.post('/requestFriend', function(req,res){
    var token = req.headers['x-access-token'];    
    if(token){
        jwt.verify(token, app.get('gina_secret'), function(err, decoded) {  
            if(err){
                return res.json({ success: false, 
                    message: 'Failed to authenticate token.' 
                });   
            } 
            User.findOne({name: decoded.user.name}, function(err, self){
                if(err)
                    throw error;
                if(self){
                    User.findOne({name: req.body.r_friend}, function(err, friend){
                        if(err)
                            throw error;
                        if(friend){
                            User.requestFriend(friend._id, me._id,function(err, friends){
                                res.json({
                                    success: true, message: "Request sent!"
                                });
                            });
                        }
                    });
                }
            });
        });  
    }
    else{
        return res.json({
            success:false,
            message: "I don't know how you got here, but this is not you friend."
        })
    }
})

//////////////////////////////
// LOGIN / AUTH
//RETURNS JWT for Session
//////////////////////////////
apiRoutes.post('/authenticate', function(req, res) {
      User.findOne({name: req.body.name}, function(err, user) {
        if (err) 
            throw err;
        if (!user) {
          res.json({ success: false, message: 'Student does not EXIST, sorry.' });
        } 
        else if (user) {
            user.comparePassword(req.body.password, function(err, isMatch) {
                if (err) 
                    throw err;
                if(isMatch){
                    const payload = {
                        user
                      };
                          var token = jwt.sign(payload, app.get('gina_secret'), {expiresIn: '10h'
                          });
                          res.json({
                            success: true,
                            message: user.name,
                            token: token,
                            user: payload
                          });
                }
                else{
                    res.json({
                        success:false,
                        message: 'Failure to authenticate'
                    });
                }
            });
        }
      });
    });

//////////////////////////////
// REGISTER 
// REGISTERS USER IF THEY DON'T EXIST
//////////////////////////////
apiRoutes.post('/register', function(req, res){
        var new_user;
        /**
         * Implmement this after testing is done so i'm not frustrated and shit
            var splitString = req.body.name.split("@");
            if(splitString[1] == "zagmail.gonzaga.edu"){
            User.findOne({name: req.body.name}, function (err, success) {
                if (err) {
                    console.log(err);
                    res.send(err);
                }
                else {
                    console.log(success);
                    if (success == null) {
                        new_user.save(function(err){
                            if(err)
                                throw err;
                            res.json({
                                success: true,
                                message: 'Created user ' + req.body.name
                            });
                        });
        
                    } else {
                        res.send("Student already present");
                    }
                }
            })
         }
         */
        User.findOne({name: req.body.name}, function (err, success) {
            if (err) {
                console.log(err);
                res.send(err);
            }
            else {
                if (success == null) {
                    var new_user = new User({
                        name: req.body.name,
                        password: req.body.password,
                        department: req.body.department,
                        phone: req.body.phone,
                        survey_results: req.body.survey_data,
                    });
                    new_user.save(function(err){
                        if (err) 
                            throw err;
                        else{
                            res.json({ 
                                success:true,
                                user: new_user,
                                message: "Successful Creation"});
                        }
                    });
                } else {
                    res.send("Student already present");
                }
            }
        })
    });

//////////////////////////////
// GET PROFILE 
// IF USER HAS TOKEN, THEN GET USER INFO
//////////////////////////////
apiRoutes.get('/profile/:id', function(req, res){
    var token = req.headers['x-access-token'];
    if(token){
        jwt.verify(token, app.get('gina_secret'), function(err, decoded) {  
            if(err){
                return res.json({ success: false, 
                    message: 'Failed to authenticate token.' 
                });   
            } 
            if(decoded.user.name == req.params.id){
                return res.json({
                    success:true,
                    message: "Name: " + decoded.user.name,
                    user: decoded.user.matches
                })
            }
            else {
                return res.json({
                    success:false,
                    message: "I don't know how you got here, but this is not you friend."
                })
            }
        })      
    } 
});
//////////////////////////////
// ????????
//////////////////////////////
apiRoutes.get('/', function(req, res) {
  res.json({ message: 'Kevin Tran Default Testing Route localhost:8080/api/' });
});

//////////////////////////////
// GET LIST OF ALL USERS
// JSON OF ALL USERS BACK
//////////////////////////////
apiRoutes.get('/users', function(req, res) {
  User.find({}, function(err, all_users) {
    res.json({
        success: true,
        users: all_users});
  });
});  

//////////////////////////////
// ADD FRIEND/ GET FOLLOWER
// FINDS FOLLOWER VIA ID, THEN IF NOT FOLLOWING WILL
//////////////////////////////
apiRoutes.get('/addFriend/:id/:f_id', function(req, res) {
    var token = req.headers['x-access-token'];
    if(token){
        jwt.verify(token, app.get('gina_secret'), function(err, decoded) {  
            if(err){
                return res.json({ success: false, 
                    message: 'Failed to authenticate token.' 
                });   
            } 
            if(decoded.user.name == req.params.id){
                User.findOne({name: req.params.f_id}, function(err, friend) {
                    var _friend = friend;
                    if(err){
                        return res.json({ success: false, 
                            message: 'Error somewhere' 
                        }); 
                    }
                    else if(friend){
                     User.findOne({name: req.params.id},function(err, user){   
                        var isInArray = user.friends.some(function (check_friend) {
                            return check_friend.equals(friend._id);
                        });
                        if(isInArray){
                            return res.json({ success: false, 
                                message: "Already following" 
                            });
                        }
                        else{
                        user.friends.push(friend)
                        user.save(function(err, results){
                            if(err)
                                throw err;
                            else{
                                return res.json({ success: true, 
                                    message: user 
                                }); 
                            }
                        });
                    }
                     });
                    }
                  });
            }
            else {
                return res.json({
                    success:false,
                    message: "I don't know how you got here, but this is not you friend."
                })
            }
        })      
    }
  });  


//////////////////////////////
// Get Matches Simple
// This shit took way to long im dumb af
//////////////////////////////
apiRoutes.get('/getMatches', function(req, res){
    var token = req.headers['x-access-token'];  
    var a = [];
    if(token){
        jwt.verify(token, app.get('gina_secret'), function(err, decoded) {  
            if(err){
                return res.json({ success: false, 
                    message: 'Failed to authenticate token.' 
                });   
            }
            User.find({}, function(err, users){
                for(var i = 0; i < users.length-1; i++){
                    var matches = 0;
                    for(var j = 0; j < decoded.user.survey_results.length-1; j++){
                        if(users[i].survey_results[j] == decoded.user.survey_results[j]){
                            matches++;
                        }
                    }
                    if(matches > 4)
                        a.push(users[i])
                }
        });
        User.findById(decoded.user._id).populate('matches').exec(function (err, user) {
          if (err)
            throw err;
          else {
              user.matches = a;
              user.save(function(err, zz){
                  if(err)
                    throw err;
              });
            return res.json({
                success: true,
                message: "This works?",
                ms: user.matches
            });
          }
      });
        });      
    } 
});

module.exports = apiRoutes;