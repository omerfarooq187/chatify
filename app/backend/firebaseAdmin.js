const admin = require('firebase-admin');
const serviceAccount = require('/home/omer/Downloads/chatify-dec73-firebase-adminsdk-z3zgs-e11cba3a7f.json');

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://chatify-dec73-default-rtdb.firebaseio.com/'
});

module.exports = admin;