const mongoose = require('mongoose');
const express = require('express');
const sha = require('sha.js');
const fs = require('fs');
const jwt = require('jsonwebtoken')
const { json } = require('body-parser');
const { Console } = require('console');

//Me conecto a mi base de datos
mongoose.Promise = global.Promise;
mongoose.connect('mongodb://127.0.0.1:27017/ejercicioFinal').catch(e => console.log(e));

//Creo la palabra secreta para los tokens
const jwbSecretWord = "secret";

//Creo el esquema para los usuarios
let userSchema = new mongoose.Schema({
    name: {
        type: String,
        requierd: true,
        trim: true,
        minlength: 1,
        match: /(\d|\w)*/,
        unique: true
    },
    password: {
        type: String,
        required: true,
        minlength: 4
    },
    image: {
        type: String,
        required: true
    }
});

let user = mongoose.model('user', userSchema);

//Creo el esquema para los mensajes
let messageSchema = new mongoose.Schema({
    from: {
        type: mongoose.Schema.ObjectId,
        required: true
    },
    to: {
        type: mongoose.Schema.ObjectId,
        required: true
    },
    message: {
        type: String,
        required: true,
        trim: true,
        minlength: 1
    },
    image: {
        type: String,
        required: false
    },
    sent: {
        type: String,
        required: true,
        trim: true,
        minlength: 10
    }
});

let message = mongoose.model('message', messageSchema);


let server = express();
server.use(express.json());
server.listen(8080);

server.use(express.static('public'));
server.use('/img', express.static(__dirname + '/img'));



//POST para hacer entrar en tu usuario
server.post('/ejercicioFinal/login', function(req, res){

    //Busca si hay un usuario que concuerde con el nombre de usuario y contraseña
    user.findOne({
        name: sha('SHA256').update(req.body.name).digest('hex'),
        password: sha('SHA256').update(req.body.password).digest('hex')
    
    //Si encuentra el usuario, le devuelve un token de sesion, junto con su nombre y la ubicacion de la imagen en el servidor
    }).then(data => {
        if (data != null){
            console.log(req.body.name + " ha iniciado sesion");
            let newToken = jwt.sign({name: req.body.name, id: data._id}, jwbSecretWord, {expiresIn: "2h"});
            res.send({
                access: "admitted",
                token: newToken,
                name: req.body.name,
                image: data.image
            });

        //Si no lo encuentra, devuelve un error de login
        } else{
            console.log("Se intento iniciar sesion a nombre de " + req.body.name + " sin exito");
            res.send({
                access: "Denied",
                error: "Incorrect username or password"
            });
        }
    });
});


//POST para registrar un nuevo usuario
server.post('/ejercicioFinal/register', function(req, res){

    //Intenta crear un nuevo usuario, si no tiene nombre, contraseña y foto, devolvera un error, y borrara la foto en caso de que haya sido creada
    try{

        //Crea la ruta de la nueva imagen, recibe la imagen en BASE64 y la crea en la carpeta "profilePhotos"
        let fileName = Date.now() + ".png";
        let filePath = '0EjercicioFinal/profilePhotos/' + fileName;
        let data = Buffer.from(req.body.image, 'base64');
        fs.writeFileSync(filePath, data);

        //Crea el nuevo usuario
        let newUser = new user({
            name: sha('SHA256').update(req.body.name).digest('hex'),
            password: sha('SHA256').update(req.body.password).digest('hex'),
            image: fileName
        });
    
        //Guarda el nuevo usuario, y captura cualquier error inesperado
        newUser.save().then(result => {
            console.log("Usuario añadido con nombre " + req.body.name);
            res.send({access: "Admitted", result: "The user " + req.body.name + " has been successfully created"});
        }).catch(error => {
            console.log("Se intento añadir un nuevo usuario a nombre de " + newUser.name + " pero dio error");
            console.log(error);
            res.send({access: "Admitted", error: "An unexpected error happend when trying to register"});
        });
    } catch(error){
        try{
            fs.unlink(filePath, (err) =>{
                if(err){
                    throw err;
                }
            });
        } catch(error){
            console.log("El campo no rellenado fue la imagen");
        };
        console.log("Se intento crear un nuevo usuario, pero no relleno todos los campos");
        res.send({access: "Denied", error: "You must fill the three fields (Username, password and image) to create a new user"});
    };
});


//GET para obtener todos los usuarios de la base de datos. Debes haber hecho login para poder verlos
server.get('/ejercicioFinal/users', function(req,res){
    let loginToken = req.headers['authorization'];
    //Revisa si hay un token, si no lo hay, devuelve un error
    try{
        loginToken = loginToken.substring(7);
        //Revisa si el token esta caducado, si lo esta, devuelve un error
        try{
            let autentifiedUser = jwt.verify(loginToken, jwbSecretWord);

            //Envia todos los datos de los usuarios de la base de datos, y captura los errores inesperados
            user.find().then(result => {
                let response = {
                    access: "Admitted",
                    users: result
                };
                console.log("El usuario " + autentifiedUser.name + " esta viendo todos los usuarios de la base de datos")
                res.send(response);
            }).catch(error => {
                console.log("Error inesperado cuando el usuario " + autentifiedUser + " trataba de ver los usuarios de la base de datos");
                res.send({access: "Admitted", error: "An unexpected error happend when trying to send the results of the query"});
            });
        } catch(error){
            console.log("Se intentaron ver los usuarios de la base de datos, pero el token caduco");
            res.send({access: "Denied", error: "The login time has been expired, please log in again"});
        }
    } catch(error){
        console.log("Se intentaron ver los usuarios de la base de datos, pero no habia un registro previo (sin token)");
        res.send({acees: "Denied", error: "You must login to delete your messages"});
    }
});


//PUT para cambiar la imagen de perfil de un usuario
server.put('/ejercicioFinal/users', function(req, res){

    let loginToken = req.headers['authorization'];
    //Revisa si hay un token, si no lo hay, devuelve un error
    try{
        loginToken = loginToken.substring(7);
        //Revisa si el token esta caducado, si lo esta, devuelve un error
        try{
            let autentifiedUser = jwt.verify(loginToken, jwbSecretWord);

            //Crea la nueva imagen de perfil y su ruta en el servidor
            let fileName = Date.now() + ".png";
            let filePath = '0EjercicioFinal/profilePhotos/' + fileName;
            let image = Buffer.from(req.body.image, 'base64');
            fs.writeFileSync(filePath, image);

            //Encuentra al usuario en la base de datos, y le asigna su nueva imagen de perfil, borrando su antigua foto de perfil si existia, y capturando cualquier error inesperado
            //Aqui fallaba
            user.findById(autentifiedUser.id).then(user => {
                let oldFilePath = '0EjercicioFinal/profilePhotos/' + user.image;
                try{
                    fs.unlink(oldFilePath, (err) =>{
                        if(err){
                            throw err;
                        }
                    });
                } catch(error){
                    console.log("La antigua imagen de perfil no existia");
                };
                user.image = fileName;
                user.save().catch(error => {
                    console.log("Hubo un error inesperado guardando la nueva foto de perfil de " + autentifiedUser.name + " en la base de datos");
                    console.log(error);
                    res.send({access: "Admitted", error: "An unexpected error happend when trying to save the image in the database"});
                });
            });
            console.log(autentifiedUser.name + " ha actualizado su imagen de perfil");
            res.send({access: "Admitted", result: "Your image has been updated"});
        } catch(error){
            console.log("Se intento cambiar una foto de perfil, pero su token caduco");
            res.send({access: "Denied", error: "The login time has been expired, please log in again"});
        }
    } catch(error){
        console.log("Se intento actualizar una imagen de perfil, pero no habia un registro previo (sin token)");
        res.send({acees: "Denied", error: "You must login to change your profile photo"});
    }
});


//GET para ver todos los mensajes recibidos
server.get('/ejercicioFinal/messages', function(req,res){
    let loginToken = req.headers['authorization'];
    //Revisa si hay un token, si no lo hay, devuelve un error
    try{
        loginToken = loginToken.substring(7);
        //Revisa si el token esta caducado, si lo esta, devuelve un error
        try{
            let autentifiedUser = jwt.verify(loginToken, jwbSecretWord);
            
            //Busca todos los mensajes recibidos y los envia al usuario, captura cualquier error inesperado
            message.find({"to": autentifiedUser.id}).then(message => {
                let response = {
                    access: "Admitted",
                    messages: message
                };
                console.log("El usuario " + autentifiedUser.name + " esta viendo sus mensajes recibidos");
                res.send(response);
            }).catch(error => {
                console.log("El usuario " + autentifiedUser.name + " queria ver sus mensajes recibidos, pero ha tenido un error desconocido");
                console.log(error);
                res.send({access: "Admitted", error: "An unexpected error happend when trying to recive the messages"});
            });
        } catch(error){
            console.log("Se intento ver los mensajes recibidos, pero el token caduco");
            res.send({access: "Denied", error: "The login time has been expired, please log in again"});
        }
    } catch(error){
        console.log("Se intento ver los mensajes recibidos, pero no habia un registro previo (sin token)");
        res.send({acees: "Denied", error: "You must login to see your messages"});
    }
});


//POST para enviar un mensaje a otro usuario
server.post('/ejercicioFinal/messages/:toUserId', function(req, res){
    let loginToken = req.headers['authorization'];
    //Revisa si hay un token, si no lo hay, devuelve un error
    try{
        loginToken = loginToken.substring(7);
        //Revisa si el token esta caducado, si lo esta, devuelve un error
        try{
            let autentifiedUser = jwt.verify(loginToken, jwbSecretWord);
            //Revisa si existe el usuario que recibira el mensaje en la base de datos, si no, devuelve un error
            user.findById(req.params.toUserId).then(result => {
                //Crea la fecha actual
                let today = new Date();
                let year = today.getFullYear();
                let month = today.getMonth() + 1;
                let day = today.getDate();
                if (day < 10) {
                    day = '0' + day;
                }
                if (month < 10){
                    month = '0' + month;
                }

                //Crea la imagen del mensaje (si hay) y crea el mensaje, si no, crea el mensaje sin imagen. Captura cualquier error inesperado
                if(req.body.image != null){
                    let fileName = Date.now() + ".png";
                    let filePath = '0EjercicioFinal/profilePhotos/' + fileName;
                    let image = Buffer.from(req.body.image, 'base64');
                    fs.writeFileSync(filePath, image);

                    let newMessage = new message({
                        from: autentifiedUser.id,
                        to: req.params.toUserId,
                        message: req.body.message,
                        image: fileName,
                        sent: day + '/' + month + '/' + year
                    });
                    newMessage.save().then(result =>{
                        console.log(autentifiedUser.name + " envio un mensaje a " + req.params.toUserId);
                        res.send(newMessage);
                    }).catch(error => {
                        console.log("Hubo algun tipo de error tratando de enviar el mensaje de " + autentifiedUser.name + " a " + req.params.toUserId);
                        console.log(error);
                        res.send({access: "Admitted", error: "An unexpected error happend when trying to send the message"});
                    });

                } else{
                    let newMessage = new message({
                        from: autentifiedUser.id,
                        to: req.params.toUserId,
                        message: req.body.message,
                        sent: day + '/' + month + '/' + year
                    });
                    newMessage.save().then(result =>{
                        console.log(autentifiedUser.name + " envio un mensaje a " + req.params.toUserId);
                        res.send(newMessage);
                    }).catch(error => {
                        console.log("Hubo algun tipo de error tratando de enviar el mensaje de " + autentifiedUser.name + " a " + req.params.toUserId);
                        console.log(error);
                        res.send({access: "Admitted", error: "An unexpected error happend when trying to send the message"});
                    });
                }
            }).catch(error => {
                console.log("El usuario " + autentifiedUser.name + " trato de enviar un mensaje, pero el destinatario no existia");
                res.send({access: "Admitted", error: "The user you sent the message could not be found"});
            });
        } catch(error){
            console.log("Se intento enviar un mensaje, pero su token caduco");
            res.send({access: "Denied", error: "The login time has been expired, please log in again"});
        }
    } catch(error){
        console.log("Se intento enviar un mensaje, pero no habia un registro previo (sin token)");
        res.send({acees: "Denied", error: "You must login to send messages"});
    }
});


//DELETE para borrar un mensaje el cual el usuario sea dueño de el
server.delete('/ejercicioFinal/messages/:id', function(req, res){
let loginToken = req.headers['authorization'];
    //Revisa si hay un token, si no lo hay, devuelve un error
    try{
        loginToken = loginToken.substring(7);
        //Revisa si el token esta caducado, si lo esta, devuelve un error
        try{
            let autentifiedUser = jwt.verify(loginToken, jwbSecretWord);

            //Busca el mensaje, si no existe, devuelve un error
            message.findById(req.params.id).then(result =>{
                //Revisa si el mensaje encontrado pertenece de alguna forma al usuario, ya sea porque ha sido quien envio el mensaje, o por ser quien lo recibio. Si el mensaje no esta relacionado con el usuario de ninguna forma, devuelve un error
                let userId = autentifiedUser.id;
                if(userId == result.to.toString() || userId == result.from.toString()){
                    //Trata de borrar el mensaje el cual le pertenece, captura cualquier error inesperado
                    message.deleteOne({"_id": req.params.id}).then(result =>{
                        console.log("El usuario " + autentifiedUser.name + " ha borrado un mensaje");
                        res.send({access: "Admitted", result: "Message was deleted succesfully"});
                    }).catch(error =>{
                        console.log("El usuario " + autentifiedUser.name + " ha tratado de borrar un mensaje el cual pertenecia, pero ha surgido un error inesperado");
                        console.log(error);
                        res.send({access: "Admitted", error: "An unexpected error happend when trying to delete your message"});
                    });
                } else{
                    console.log("El usuario " + autentifiedUser.name + " intento borrar un mensaje el cual no le pertenecia");
                    res.send({access: "Admitted", error: "The message you tried to delete does not belong to you"});
                }
            }).catch(error =>{
                console.log("El usuario " + autentifiedUser.name + " intento borrar un mensaje, pero no existia");
                res.send({access: "Admitted", error: "The message you tried to delete does not exist"});
            });
        } catch(error){
            console.log("Se intento eliminar un mensaje, pero el token caduco");
            res.send({access: "Denied", error: "The login time has been expired, please log in again"});
        }
    } catch(error){
        console.log("Se intento eliminar un mensaje, pero no habia un registro previo (sin token)");
        res.send({acees: "Denied", error: "You must login to delete your messages"});
    }
});